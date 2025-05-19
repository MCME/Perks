package com.mcmiddleearth.perks.supporter;

import com.google.gson.*;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.PermissionUpdater;
import com.mcmiddleearth.perks.utils.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatreonClient {
    private static String clientId;
    private static String clientSecret;
    private static String REDIRECT_URI = "https://build.mcmiddleearth.com";
    private static final String PATREON_URI = "https://www.patreon.com/api/oauth2";
    private static String campaignId = null;
    private static String refreshToken;
    private static String accessToken;

    public static void main(String[] args) throws Exception {
        String code = getAuthorizationCode();
        exchangeCodeForTokens(code);
        scheduleMemberFetch();
    }

    public static void init() {
        ConfigurationSection patreonConfig = PerksPlugin.getInstance().getConfig().getConfigurationSection("patreon");
        if(patreonConfig == null) {
            patreonConfig = PerksPlugin.getInstance().getConfig().createSection("patreon");
        }
        clientId = patreonConfig.getString("client_id");
        clientSecret = patreonConfig.getString("client_secret");
        REDIRECT_URI = patreonConfig.getString("redirect_uri");
        accessToken = patreonConfig.getString("access_token","");
        refreshToken = patreonConfig.getString("refresh_token","");
    }

    private static String getAuthorizationCode() throws IOException {
        System.out.println("Open link and enter 'code' here:");
        String url = PATREON_URI+"/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8");
        System.out.println(url);

        System.out.print("Code: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine().trim();
    }

    public static void exchangeCodeForTokens(String code) throws IOException {
        String params = "grant_type=authorization_code&code=" + URLEncoder.encode(code, "UTF-8") +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, "UTF-8");
//Logger.getGlobal().info(PATREON_URI+"/token"+params);
        HttpURLConnection conn = (HttpURLConnection) new URL(PATREON_URI+"/token").openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes());
        }

        String response = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines()
                .reduce("", (acc, line) -> acc + line);

        accessToken = extractJsonField(response, "access_token");
        refreshToken = extractJsonField(response, "refresh_token");
        saveTokens();

        System.out.println("AccessToken: " + accessToken);
    }

    private static void refreshAccessToken() throws IOException {
        String params = "grant_type=refresh_token" +
                "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8") +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret;

        HttpURLConnection conn = (HttpURLConnection) new URL(PATREON_URI+"/token").openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes());
        }

        String response = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines()
                .reduce("", (acc, line) -> acc + line);

        accessToken = extractJsonField(response, "access_token");
        refreshToken = extractJsonField(response, "refresh_token");
        saveTokens();
        System.out.println("Token refreshed.");
    }

    private static void saveTokens() {
        ConfigurationSection patreonConfig = PerksPlugin.getInstance().getConfig().getConfigurationSection("patreon");
        patreonConfig.set("access_token", accessToken);
        patreonConfig.set("refresh_token", refreshToken);
        PerksPlugin.getInstance().saveConfig();
    }

    private static void scheduleMemberFetch() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    fetchMembers(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5 * 60 * 1000); // alle 5 Minuten
    }

    static String getCampaignId(boolean refresh) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL("https://www.patreon.com/api/oauth2/v2/campaigns").openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestMethod("GET");

        int status = conn.getResponseCode();
        if (status == 401 && refresh) {
            // Access Token abgelaufen
            refreshAccessToken();
            return getCampaignId(false);
        }

        String response = new String(conn.getInputStream().readAllBytes());
//Logger.getGlobal().info(response);
        JsonObject json = JsonParser.parseString(response).getAsJsonObject();
        return json.get("data").getAsJsonArray().get(0).getAsJsonObject()
                   .get("id").getAsString();
    }

    public static List<Member> fetchMembers(boolean refresh) throws IOException {
        if(campaignId == null) {
            campaignId = getCampaignId(refresh);
        }
//Logger.getGlobal().info("Campaign ID: "+campaignId);
        HttpURLConnection conn = (HttpURLConnection) new URL(PATREON_URI+"/v2/campaigns/"+campaignId
                +"/members?include=currently_entitled_tiers,user&fields[member]=lifetime_support_cents&fields[user]=social_connections&fields[tier]=title")
                .openConnection();
        /*String includeParams = URLEncoder.encode("include=currently_entitled_tiers,user", StandardCharsets.UTF_8);
        String fieldsMemberParams = URLEncoder.encode("fields[member]=full_name,lifetime_support_cents,currently_entitled_tiers", StandardCharsets.UTF_8);
        String fieldsUserParams = URLEncoder.encode("fields[user]=social_connections,full_name", StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) new URL(PATREON_URI+"/v2/campaigns/"+campaignId
                +"/members?" + includeParams + "&" + fieldsMemberParams + "&" + fieldsUserParams)
                .openConnection();*/
// Sample response for (url decoded) https://www.patreon.com/api/oauth2/v2/campaigns/{campaign_id}/members?include=currently_entitled_tiers,address&fields[member]=full_name,is_follower,last_charge_date,last_charge_status,lifetime_support_cents,currently_entitled_amount_cents,patron_status&fields[tier]=amount_cents,created_at,description,discord_role_ids,edited_at,patron_count,published,published_at,requires_shipping,title,url&fields[address]=addressee,city,line_1,line_2,phone_number,postal_code,state
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);

        int status = conn.getResponseCode();
        if (status == 401 && refresh) {
            // Access Token abgelaufen
            refreshAccessToken();
            return fetchMembers(false);
        }

        String response = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines()
                .reduce("", (acc, line) -> acc + line);
//Logger.getGlobal().info(response);
        return parseMembers(response);
    }

    public static List<Member> parseMembers(String json) {
        List<Member> result = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonArray data = jsonObject.get("data").getAsJsonArray();
        for (JsonElement jsonMember : data.asList()) {
//Logger.getGlobal().info("member: "+jsonMember.toString());
            int lifetimeSupport = jsonMember.getAsJsonObject().get("attributes").getAsJsonObject()
                    .get("lifetime_support_cents").getAsInt();
//Logger.getGlobal().info("lifetime: "+lifetimeSupport);
            JsonObject relationships = jsonMember.getAsJsonObject().get("relationships").getAsJsonObject();
            JsonArray tiers = relationships.get("currently_entitled_tiers").getAsJsonObject()
                    .get("data").getAsJsonArray();
            List<String> tierIdList = new ArrayList<>();
            List<String> tierTitleList = new ArrayList<>();
            for (JsonElement tier : tiers.asList()) {
                tierIdList.add(tier.getAsJsonObject().get("id").getAsString());
            }
            int userId = relationships.get("user").getAsJsonObject()
                    .get("data").getAsJsonObject()
                    .get("id").getAsInt();
//Logger.getGlobal().info("userId: "+userId);
            String discordId = "";
            JsonArray included = jsonObject.get("included").getAsJsonArray();
            for (JsonElement additionalData : included.asList()) {
                if (additionalData.getAsJsonObject().get("id").getAsInt() == userId
                        && additionalData.getAsJsonObject().get("type").getAsString().equals("user")) {
                    JsonElement socialConnections = additionalData.getAsJsonObject()
                                                        .get("attributes").getAsJsonObject()
                                                        .get("social_connections");
//Logger.getGlobal().info("Found user: "+socialConnections);
                    if(socialConnections != null && socialConnections.isJsonObject()) {
                        JsonElement discord = socialConnections.getAsJsonObject().get("discord");
                        if (discord != null && discord.isJsonObject()) {
                            discordId = discord.getAsJsonObject().get("user_id").getAsString();
                        }
                    }
                } else if(additionalData.getAsJsonObject().get("type").getAsString().equals("tier")
                        && tierIdList.contains(additionalData.getAsJsonObject().get("id").getAsString())) {
//Logger.getGlobal().info("Found tier: "+additionalData.getAsJsonObject().get("attributes").getAsJsonObject()
//        .get("title").getAsString());
                    tierTitleList.add("tier_"+additionalData.getAsJsonObject().get("attributes").getAsJsonObject()
                                                                      .get("title").getAsString());
                }
            }
//Logger.getGlobal().info("discordId: "+discordId);
            if(!discordId.isEmpty()) {
                Member member = new Member(discordId, lifetimeSupport/100, tierTitleList);
                result.add(member);
            }
        }
        return result;
    }

    public static void updateCredits(PermissionUpdater updater) {
        Bukkit.getScheduler().runTaskAsynchronously(PerksPlugin.getInstance(), ()  -> {
            Configuration config = new YamlConfiguration();
            try {
                List<Member> members = fetchMembers(true);
//Logger.getGlobal().info("members found: "+ members.size());
                for(Member member: members) {
//Logger.getGlobal().info("discordId: "+ member.discordId);
                    UUID uuid = DiscordUtil.getUniqueId(member.discordId);
                    if(member.discordId.equals("1257826230561149069")) {
//Logger.getGlobal().info("discordId replacement Eriol");
                        uuid = DiscordUtil.getUniqueId("258267590516801536");
                    }
//Logger.getGlobal().info("uuid: "+ uuid);
                    if(uuid != null) {
                        member.tiers.add("patreon_"+member.sum);
                        config.set(uuid.toString(), member.tiers);
                    }
                }
                PermissionData.updateCredits(config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            updater.updatePermissions();
        });
    }

    private static String extractJsonField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    static String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"?([^\"]+?)\"?(,|\\})");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static class Member {
        public int sum;
        public List<String> tiers;
        public String discordId;

        public Member(String discordId, int sum, List<String> tiers) {
            this.sum = sum;
            this.tiers = tiers;
            this.discordId = discordId;
        }

        public String toString() {
            return "Sum: " + sum + " EUR, Tier: " + tiers;
        }
    }

}
