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
package com.mcmiddleearth.perks.perks;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.commands.NameTagHandler;
import com.mcmiddleearth.perks.listeners.NameTagListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 *
 * @author Eriol_Eandur
 */
public class NameTagPerk extends Perk {

    private static final String TEAM_PREFIX = "NameTagPerk_";
    private static final ChatColor[] RAINBOW_COLORS = {
        ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN,
        ChatColor.AQUA, ChatColor.BLUE, ChatColor.LIGHT_PURPLE
    };

    private final Scoreboard board;
    private final Map<String, Team> tierTeams = new LinkedHashMap<>();
    private final Map<UUID, String> playerTiers = new HashMap<>();
    private final Set<UUID> rainbowPlayers = new HashSet<>();
    private BukkitTask rainbowTask;
    private int rainbowIndex = 0;

    public NameTagPerk() {
        super("name");
        board = Bukkit.getScoreboardManager().getMainScoreboard();
        setCommandHandler(new NameTagHandler(this, Permissions.USER.getPermissionNode()), "name");
        enable();
        setListener(new NameTagListener(this));
    }

    public void setNameTag(Player player, boolean apply) {
        if (!apply) {
            removeFromAllTeams(player);
            playerTiers.remove(player.getUniqueId());
            rainbowPlayers.remove(player.getUniqueId());
            return;
        }
        String currentTier = playerTiers.get(player.getUniqueId());
        if (currentTier == null) {
            applyTier(player, "default");
        }
    }

    public void applyTier(Player player, String tierName) {
        removeFromAllTeams(player);
        rainbowPlayers.remove(player.getUniqueId());

        if (tierName.equals("rainbow")) {
            rainbowPlayers.add(player.getUniqueId());
            Team rainbowTeam = getOrCreateTier("rainbow");
            if (rainbowTeam != null) {
                rainbowTeam.addPlayer(player);
            }
            playerTiers.put(player.getUniqueId(), "rainbow");
            ensureRainbowTask();
            return;
        }

        Team team = getOrCreateTier(tierName);
        if (team != null) {
            team.addPlayer(player);
            playerTiers.put(player.getUniqueId(), tierName);
        }
    }

    public void removeTier(Player player) {
        removeFromAllTeams(player);
        playerTiers.remove(player.getUniqueId());
        rainbowPlayers.remove(player.getUniqueId());
    }

    private void removeFromAllTeams(Player player) {
        for (Team team : tierTeams.values()) {
            team.removePlayer(player);
        }
    }

    private Team getOrCreateTier(String tierName) {
        if (tierTeams.containsKey(tierName)) {
            return tierTeams.get(tierName);
        }

        String teamId = TEAM_PREFIX + tierName;
        Team team = board.getTeam(teamId);
        if (team == null) {
            team = board.registerNewTeam(teamId);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }

        ConfigurationSection tierConfig = getTierConfig(tierName);
        if (tierConfig != null) {
            String colorName = tierConfig.getString("color", "YELLOW");
            try {
                team.setColor(ChatColor.valueOf(colorName));
            } catch (IllegalArgumentException e) {
                team.setColor(ChatColor.YELLOW);
            }

            String prefix = tierConfig.getString("prefix", "");
            if (!prefix.isEmpty()) {
                team.prefix(MiniMessage.miniMessage().deserialize(prefix));
            }

            String suffix = tierConfig.getString("suffix", "");
            if (!suffix.isEmpty()) {
                team.suffix(MiniMessage.miniMessage().deserialize(suffix));
            }
        } else {
            team.setColor(ChatColor.YELLOW);
        }

        tierTeams.put(tierName, team);
        return team;
    }

    private ConfigurationSection getTierConfig(String tierName) {
        ConfigurationSection perksSection = PerksPlugin.getInstance().getConfig()
            .getConfigurationSection("perks");
        if (perksSection == null) return null;
        ConfigurationSection nameSection = perksSection.getConfigurationSection("name");
        if (nameSection == null) return null;
        ConfigurationSection tiersSection = nameSection.getConfigurationSection("tiers");
        if (tiersSection == null) return null;
        return tiersSection.getConfigurationSection(tierName);
    }

    public List<String> getAvailableTiers(Player player) {
        List<String> tiers = new ArrayList<>();
        tiers.add("default");

        ConfigurationSection perksSection = PerksPlugin.getInstance().getConfig()
            .getConfigurationSection("perks");
        if (perksSection == null) return tiers;
        ConfigurationSection nameSection = perksSection.getConfigurationSection("name");
        if (nameSection == null) return tiers;
        ConfigurationSection tiersSection = nameSection.getConfigurationSection("tiers");
        if (tiersSection == null) return tiers;

        for (String tier : tiersSection.getKeys(false)) {
            if (tier.equals("default")) continue;
            if (player.hasPermission("perks.name." + tier)) {
                tiers.add(tier);
            }
        }
        return tiers;
    }

    public String getPlayerTier(Player player) {
        return playerTiers.getOrDefault(player.getUniqueId(), "default");
    }

    private void ensureRainbowTask() {
        if (rainbowTask != null) return;
        rainbowTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (rainbowPlayers.isEmpty()) {
                    this.cancel();
                    rainbowTask = null;
                    return;
                }
                Team rainbowTeam = tierTeams.get("rainbow");
                if (rainbowTeam != null) {
                    rainbowTeam.setColor(RAINBOW_COLORS[rainbowIndex % RAINBOW_COLORS.length]);
                    rainbowIndex++;
                }
            }
        }.runTaskTimer(PerksPlugin.getInstance(), 0, 10);
    }

    @Override
    public void disable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeFromAllTeams(player);
        }
        for (Team team : tierTeams.values()) {
            team.unregister();
        }
        tierTeams.clear();
        playerTiers.clear();
        rainbowPlayers.clear();
        if (rainbowTask != null) {
            rainbowTask.cancel();
            rainbowTask = null;
        }
    }

    @Override
    public void check() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PermissionData.isAllowed(player, this)) {
                if (!playerTiers.containsKey(player.getUniqueId())) {
                    applyTier(player, "default");
                }
            } else {
                removeTier(player);
            }
        }
    }

    @Override
    public final void enable() {
        getOrCreateTier("default");
        check();
    }

    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        config.set("donorTagColor", "YELLOW");

        ConfigurationSection tiers = config.createSection("tiers");

        ConfigurationSection defaultTier = tiers.createSection("default");
        defaultTier.set("color", "YELLOW");
        defaultTier.set("prefix", "");
        defaultTier.set("suffix", "");

        ConfigurationSection goldTier = tiers.createSection("gold");
        goldTier.set("color", "GOLD");
        goldTier.set("prefix", "<gold>\u2605 </gold>");
        goldTier.set("suffix", "");

        ConfigurationSection diamondTier = tiers.createSection("diamond");
        diamondTier.set("color", "AQUA");
        diamondTier.set("prefix", "<aqua>\u25C6 </aqua>");
        diamondTier.set("suffix", " <aqua>\u25C6</aqua>");

        ConfigurationSection rainbowTier = tiers.createSection("rainbow");
        rainbowTier.set("color", "WHITE");
        rainbowTier.set("prefix", "<yellow>\u2728 </yellow>");
        rainbowTier.set("suffix", " <yellow>\u2728</yellow>");
    }
}
