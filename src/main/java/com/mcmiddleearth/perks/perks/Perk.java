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
import com.mcmiddleearth.perks.commands.PerksCommandHandler;
import com.mcmiddleearth.perks.gui.GuiItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class Perk {
    
    private final String name;

    private GuiItem guiItem, favoriteGuiItem;
    
    private PerksCommandHandler handler;
    private Listener listener;
    
    private String[] commands;
    
    public Perk(String name) {
        this.name = name;
        guiItem = new GuiItem(new ItemStack(Material.STONE),"", "", "",1);
        favoriteGuiItem = new GuiItem(new ItemStack(Material.STONE),"", "", "",1);
        ConfigurationSection config = PerksPlugin.getPerkSettings().getConfigurationSection(name);
Logger.getGlobal().info("New perk: "+name);
        if(config != null) {
Logger.getGlobal().info("Config found. Gui section: "+config.contains("guiItem"));
            ConfigurationSection itemSection = config.getConfigurationSection("guiItem");
            if(itemSection != null) {
                guiItem = GuiItem.load(itemSection);
                ItemMeta meta = guiItem.getItemStack().getItemMeta();
                if(meta != null && meta.getDisplayName().equalsIgnoreCase("")) {
                    meta.setDisplayName(name);
                    guiItem.getItemStack().setItemMeta(meta);
                }
                guiItem.setRightCommand("updategui /perk favor "+name);
                favoriteGuiItem = GuiItem.load(itemSection);
                meta = favoriteGuiItem.getItemStack().getItemMeta();
                if(meta != null && meta.getDisplayName().equalsIgnoreCase("")) {
                    meta.setDisplayName(name);
                    favoriteGuiItem.getItemStack().setItemMeta(meta);
                }
                favoriteGuiItem.setRightCommand("updategui /perk unfavor "+name);
Logger.getGlobal().info("GuiItem material: "+guiItem.getItemStack().getType());
            }
        }
    }
    
    public boolean isEnabled() {
        return PerksPlugin.getInstance().isPerkEnabled(this);
    }
    
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    public void setCommandHandler(PerksCommandHandler handler, String... commands) {
        this.handler = handler;
        this.commands = commands;
    }
  
    public String getPermissionNode() {
        return "perks."+getName();
    }

    public void disable() {}
    
    public void check() {}
    
    public void enable() {}
    
    public void writeDefaultConfig(ConfigurationSection config) {}

    public String getName() {
        return name;
    }

    public PerksCommandHandler getHandler() {
        return handler;
    }

    public Listener getListener() {
        return listener;
    }

    public String[] getCommands() {
        return commands;
    }

    public GuiItem getGuiItem() {
        return guiItem;
    }

    public GuiItem getFavoriteGuiItem() {
        return favoriteGuiItem;
    }

}
