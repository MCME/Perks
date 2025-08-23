/*
 * Copyright (C) 2017 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.perks.permissions;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.gui.DefinitionItem;
import com.mcmiddleearth.perks.gui.GuiItem;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class PermissionData {

    private final static File creditDataFile = new File(PerksPlugin.getInstance().getDataFolder(),"creditData.yml");
    private final static File creditDefinitionFile= new File(PerksPlugin.getInstance().getDataFolder(),"creditDefinition.yml");
    private final static File manualPerkFile= new File(PerksPlugin.getInstance().getDataFolder(),"manualPerks.yml");

    private static final YamlConfiguration creditDefinitionConfig = new YamlConfiguration();

    private static final YamlConfiguration manualPerkConfig = new YamlConfiguration();

    private static final Map<UUID, List<String>> creditEntries = new HashMap<>();
    private static Map<UUID, CreditData> creditDatas = new HashMap<>();
    
    private static final Set<Perk> freePerks = new HashSet<>();
    
    public static void load() {
        try {
            creditDefinitionConfig.load(creditDefinitionFile);
            manualPerkConfig.load(manualPerkFile);
            try {
                YamlConfiguration creditDataConfig = new YamlConfiguration();
                creditDataConfig.load(creditDataFile);
                updateCredits(creditDataConfig);
            } catch (IOException | InvalidConfigurationException ex) {
                Logger.getLogger(PermissionData.class.getName()).log(Level.SEVERE, "Error reading credit data.", ex);
            }
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(PermissionData.class.getName()).log(Level.SEVERE, "Error reading credit definitions.", ex);
        }
    }
    
    public static void saveCreditData() {
        try {
            YamlConfiguration creditDataConfig = new YamlConfiguration();
            for(UUID playerID: creditEntries.keySet()) {
                creditDataConfig.set(playerID.toString(), creditEntries.get(playerID));
            }
            creditDataConfig.save(creditDataFile);
        } catch (IOException ex) {
            Logger.getLogger(PermissionData.class.getName()).log(Level.SEVERE, "Error while saving credit Data.");
        }
    }

    public static void clearCredits() {
        creditEntries.clear();
    }

    public static synchronized void updateCredits(Configuration newCredits) {
        for(String playerID:newCredits.getKeys(false)) {
            UUID playerUuid = UUID.fromString(playerID);
            List<String> credits = newCredits.getStringList(playerID);
            List<String> entry = creditEntries.computeIfAbsent(playerUuid, k -> new ArrayList<>());
            entry.addAll(credits);
/*for(String credit: credits) {
Logger.getGlobal().info("Giving permission to "+ playerID+" for credit "+credit);
}*/
        }
    }
    
    public static void updatePerkPermissions(Player player) {
        PermissionAttachment attachment = getPermissionAttachment(player);
        player.removeAttachment(attachment);
        List<String> credits = creditEntries.get(player.getUniqueId());
        if(credits==null) {
            return;
        }
        CreditData creditData = new CreditData(credits);
//Logger.getGlobal().info("Set perk perms for: "+player.getName()+" - "+creditKey);
        setPermissions(player.getUniqueId(), creditData);
        player.recalculatePermissions();
        creditDatas.put(player.getUniqueId(), creditData);
    }

    public static void setCredits(HashMap<UUID, CreditData> creditDatas) {
        PermissionData.creditDatas = creditDatas;
    }

    public static CreditData getCredits(Player player) {
        return creditDatas.get(player.getUniqueId());
    }
    
    private static void setPermissions(UUID playerId, CreditData creditData) {
        Player player = Bukkit.getPlayer(playerId);
        if(player==null) {
            return;
        }
        PermissionAttachment attachment = getPermissionAttachment(player);
        for(String perkKey: creditDefinitionConfig.getKeys(false)) {
            ConfigurationSection perkSection = creditDefinitionConfig.getConfigurationSection(perkKey);
            double forum = perkSection.getDouble("forum",0);
            double patreon = perkSection.getDouble("patreon", 0);
            double total = perkSection.getDouble("total", 0);
            List<String> tiers = perkSection.getStringList("tiers");
            if(creditData.getForum() >= forum && creditData.getPatreon() >= patreon && creditData.getTotal() >= total) {
                boolean hasAllTiers = true;
                for(String tier: tiers) {
                    if(!creditData.getTiers().contains(tier)) {
                        hasAllTiers = false;
                        break;
                    }
                }
                if(hasAllTiers) {
                    List<String> perks = perkSection.getStringList("perks");
                    for(String perkName:perks) {
                        Perk perk = PerkManager.forName(perkName);
                        if(perk!=null) {
//Logger.getGlobal().info("Perk: "+player.getName()+" "+perk.getName());
                            attachment.setPermission(perk.getPermissionNode(), true);
                        }
                    }
                }
            }
        }
    }
    
    private static PermissionAttachment getPermissionAttachment(Player player) {
        Set<PermissionAttachmentInfo> infos = player.getEffectivePermissions();
        for(PermissionAttachmentInfo info:infos) {
            if(info.getAttachment()!=null
                    && info.getAttachment().getPlugin()==PerksPlugin.getInstance()) {
                return info.getAttachment();
            }
        }
        return player.addAttachment(PerksPlugin.getInstance());
    }
    
    public static boolean isAllowed(Player player, Perk perk) {
        if(perk == null) {
            return false;
        }
        return player.hasPermission(Permissions.USER.getPermissionNode()) 
                && (player.hasPermission(perk.getPermissionNode())
                    || freePerks.contains(perk)
                    || hasManualPerk(player, perk));
    }

    private static boolean hasManualPerk(Player player, Perk perk) {
        List<String> manualPerks = manualPerkConfig.getStringList(player.getUniqueId().toString());
//Logger.getGlobal().info("Player "+player.getName()+" has manual perk "+perk.getName()+": "+manualPerks.contains(perk.getName()));
        return manualPerks.contains(perk.getName());
    }

    public static ConfigurationSection getPerkDefinitions() {
        return creditDefinitionConfig;
    }

    public static List<GuiItem> getPerkDefinitionItems(Player player) {
        List<GuiItem> result = new ArrayList<>();
        for(String key: creditDefinitionConfig.getKeys(false)) {
            ConfigurationSection section = creditDefinitionConfig.getConfigurationSection(key);
            CreditData data =  creditDatas.get(player.getUniqueId());
            if(data != null) {
                result.add(DefinitionItem.load(section, key, player, data));
            }
        }
        return result;
    }

    /**
     * Makes a perk available for everyone with Permission.USER.
     * @param perk
     * @param duration 
     */
    public static void enableFreePerk(final Perk perk, int duration) {
        freePerks.add(perk);
        new BukkitRunnable() {
            @Override
            public void run() {
                disableFreePerk(perk);
            }
        }.runTaskLater(PerksPlugin.getInstance(), (long) duration *60*20);
    }

    public static void disableFreePerk(Perk perk) {
        freePerks.remove(perk);
    }

}
