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
import com.mcmiddleearth.perks.commands.PotionEffectHandler;
import com.mcmiddleearth.perks.listeners.PotionEffectListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 *
 * @author Eriol_Eandur
 */
public class PotionEffectPerk extends Perk {

    private String itemName = PerksPlugin.getPerkString(this.getName(),
            "itemName",null);

    private final ItemStack item;
    private final PotionEffectData[] effectData;

    public PotionEffectPerk(String name, ItemStack item, String itemName, PotionEffectData... effectData) {
        super(name);
        String mat = PerksPlugin.getPerkString(this.getName(),"item",null);
        if(mat==null) {
            this.item = item;
        } else {
            this.item = new ItemStack(Material.valueOf(mat));
        }
        if(this.itemName==null) {
            this.itemName = itemName;
        }
        this.effectData = effectData;

        setListener(new PotionEffectListener(name));
        setCommandHandler(new PotionEffectHandler(this, Permissions.USER.getPermissionNode()),name);
    }

    public void giveItem(Player player) {
        ItemStack resultItem = new ItemStack(item);
        ItemMeta meta = resultItem.getItemMeta();
        meta.setDisplayName(player.getDisplayName()+"'s "+itemName);
        meta.setLore(Arrays.asList(getName(), "Right click to use."));
        meta.setUnbreakable(true);
        resultItem.setItemMeta(meta);
        player.getInventory().addItem(resultItem);
    }

    public void giveEffect(final Player player) {
        for(PotionEffectData data:effectData) {
            player.addPotionEffect(data.getPotionEffect(), true);

            final PotionEffectData dataF = data;
            new BukkitRunnable() {
                @Override
                public void run() {
                }
            }.runTaskLater(PerksPlugin.getInstance(),1);
            player.getWorld().playSound(player.getLocation(), data.getWorldSound(),1,1);
            for(Sound sound: data.getPlayerSounds()) {
                player.playSound(player.getLocation(), sound, 1, 0);
            }
        }
    }

    public boolean isActive(Player player) {
        for(PotionEffectData data:effectData) {
            if(!player.hasPotionEffect(data.type)) {
                return false;
            }
        }
        return true;
    }

    public void removeEffect(Player player) {
        for(PotionEffectData data:effectData) {
            if(player.hasPotionEffect(data.type)) {
                player.getWorld().playSound(player.getLocation(), data.getWorldSound(),1,1);
                player.removePotionEffect(data.type);
            }
        }
    }

    public static class PotionEffectData {
        private final int duration, amplifier;
        private final PotionEffectType type;

        private final Sound worldSound;
        private final Sound[] playerSounds;

        public PotionEffectData(String name, PotionEffectType type, //int duration, int amplifier,
                                Sound worldSound, Sound... sounds) {
            this.worldSound = worldSound;
            this.playerSounds = sounds;
            this.duration = PerksPlugin.getPerkInt(name, "duration",-1);
            this.amplifier = PerksPlugin.getPerkInt(name, "amplifier",1);
            this.type = type;
        }

        public PotionEffect getPotionEffect() {
            return new PotionEffect(type, duration, amplifier);
        }

        public Sound getWorldSound() {
            return worldSound;
        }

        public Sound[] getPlayerSounds() {
            return playerSounds;
        }
    }

    @Override
    public void check() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            if(!PermissionData.isAllowed(player, this)) {
                removeEffect(player);
            }
        }
    }

    @Override
    public void disable() {
        check();
    }

    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        config.set("itemName", itemName);
        config.set("duration", -1);
        config.set("amplifier", 1);
        config.set("item", item.getType().name());
    }

    public String getItemName() {
        return itemName;
    }
}
