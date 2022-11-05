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
package com.mcmiddleearth.perks;

import com.mcmiddleearth.perks.commands.PerksCommandExecutor;
import com.mcmiddleearth.perks.listeners.PermissionListener;
import com.mcmiddleearth.perks.perks.*;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.PermissionUpdater;
import com.mcmiddleearth.perks.tabCompleter.TabComplete;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class PerksPlugin extends JavaPlugin {
    
    private static PerksPlugin instance;
    private static ConfigurationSection perkSettings;

    private static final MessageUtil messageUtil = new MessageUtil();

    private PerksCommandExecutor perksExecutor;
    private PermissionUpdater permissionUpdater;
    
    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        instance = this;
        messageUtil.setPluginName("Perks");
        perkSettings = this.getConfig().getConfigurationSection("perks");
        if(perkSettings == null) {
            perkSettings = this.getConfig().createSection("perks");
            this.saveConfig();
        }
        PermissionData.load();
        getServer().getPluginManager().registerEvents(new PermissionListener(), this);
        permissionUpdater = new PermissionUpdater();
        permissionUpdater.runTaskTimer(this, 500, 2000);
        
        perksExecutor = new PerksCommandExecutor();
        getCommand("perk").setExecutor(perksExecutor);
        getCommand("perk").setTabCompleter(new TabComplete());

        PerkManager.addPerk(new NameTagPerk());
        PerkManager.addPerk(new HorsePerk());
        PerkManager.addPerk(new SetOnFirePerk());
        PerkManager.addPerk(new WizardLightPerk());
        PerkManager.addPerk(new SitPerk());
        PerkManager.addPerk(new JockeyPerk());
        PerkManager.addPerk(new GallopPerk());
        PerkManager.addPerk(new ParrotPerk());
        PerkManager.addPerk(new BoatPerk());
        PerkManager.addPerk(new CompassPerk());
        PerkManager.addPerk(new PotionEffectPerk("speed",
                                                 new ItemStack(Material.DIAMOND_BOOTS),
                                                 "Boots of Speed",
                                                 new PotionEffectPerk.PotionEffectData("speed",PotionEffectType.SPEED,null)));
        PerkManager.addPerk(new PotionEffectPerk("jump",
                                                 new ItemStack(Material.DIAMOND_LEGGINGS),
                                                 "Leggings of Jumping",
                                                 new PotionEffectPerk.PotionEffectData("jump",PotionEffectType.JUMP,null)));
        PerkManager.addPerk(new PotionEffectPerk("ring",
                                                 new ItemStack(Material.GOLD_NUGGET),
                                                 "Ring of Power",
                                                 new PotionEffectPerk.PotionEffectData("ring",PotionEffectType.INVISIBILITY,
                                                         Sound.ENTITY_ENDERMAN_TELEPORT, Sound.ENTITY_WITHER_DEATH, Sound.ENTITY_ENDERMAN_STARE),
                                                 new PotionEffectPerk.PotionEffectData("ring",PotionEffectType.CONFUSION,null),
                                                 new PotionEffectPerk.PotionEffectData("ring",PotionEffectType.BLINDNESS,null)));
        PerkManager.addPerk(new ItemPerk("elytra", Material.ELYTRA,"fluffy wings",1));
        PerkManager.addPerk(new ItemPerk("firework", Material.FIREWORK_ROCKET,"splendid firework",64));

        this.updateConfigurationFile();
        
        for(Perk perk: PerkManager.getPerks()) {
            if(isPerkEnabled(perk)) {
                perk.enable();
            }
        }
    }
    
    @Override
    public void onDisable() {
        permissionUpdater.cancel();
        for(Perk perk: PerkManager.getPerks()) {
            try {
                perk.disable();
            } catch(Exception ex) {
                Logger.getLogger(SitPerk.class.getName()).log(Level.WARNING, "Error while disabling perks!",ex);
            }
        }
    }
    
    /**
     * Updates the config.yml in data folder of the plugin with perks
     * added since last reload
     */
    private void updateConfigurationFile() {
        this.saveDefaultConfig();
        ConfigurationSection section = getConfig().getConfigurationSection("perks");
        for(Perk perk:PerkManager.getPerks()) {
            if(!section.contains(perk.getName())) {
                ConfigurationSection perkSection = section.createSection(perk.getName());
                perk.writeDefaultConfig(perkSection);
                perkSection.set("enabled", true);
            }
        }
        this.saveConfig();
    }
    
    public boolean arePerksEnabled() {
        return getConfig().getConfigurationSection("settings").getBoolean("enabled");
    }
    
    public void enableAllPerks(boolean enable) {
        getConfig().getConfigurationSection("settings").set("enabled", enable);
        this.saveConfig();
    }
    
    public boolean isPerkEnabled(Perk perk) {
        return perk !=null && arePerksEnabled()
                && getConfig().getConfigurationSection("perks")
                              .getConfigurationSection(perk.getName())
                              .getBoolean("enabled");
    }
    
    public void enablePerk(Perk perk, boolean enable) {
        getConfig().getConfigurationSection("perks")
                   .getConfigurationSection(perk.getName())
                   .set("enabled", enable);
        this.saveConfig();
    }
    
    public static String getPerkString(String perk, String key, String def) {
        if(perkSettings.getConfigurationSection(perk)==null 
                || !perkSettings.getConfigurationSection(perk).contains(key)) {
            return def;
        }
        return perkSettings.getConfigurationSection(perk).getString(key);
    }
    
    public static int getPerkInt(String perk, String key, int def) {
        if(perkSettings.getConfigurationSection(perk)==null 
                || !perkSettings.getConfigurationSection(perk).contains(key)) {
            return def;
        }
        return perkSettings.getConfigurationSection(perk).getInt(key);
    }
    
    public static double getPerkDouble(String perk, String key, double def) {
        if(perkSettings.getConfigurationSection(perk)==null
                || !perkSettings.getConfigurationSection(perk).contains(key)) {
            return def;
        }
        return perkSettings.getConfigurationSection(perk).getDouble(key);
    }

    public static PerksPlugin getInstance() {
        return instance;
    }

    public static ConfigurationSection getPerkSettings() {
        return perkSettings;
    }

    public static MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public PerksCommandExecutor getPerksExecutor() {
        return perksExecutor;
    }
}
