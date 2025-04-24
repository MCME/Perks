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
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Eriol_Eandur
 */
public class Perk {
    
    private final String name;

    private final ItemStack guiItem;
    
    private PerksCommandHandler handler;
    private Listener listener;
    
    private String[] commands;
    
    public Perk(String name) {
        this.name = name;
        ItemStack itemStack = null;
        ConfigurationSection config = PerksPlugin.getPerkSettings().getConfigurationSection(name);
        if(config != null) {
            ConfigurationSection itemSection = config.getConfigurationSection("guiItem");
            if(itemSection != null) {
                try {
                    itemStack = new ItemStack(Material.valueOf(itemSection.getString("type", Material.STONE.name())));
                    ItemMeta meta = itemStack.getItemMeta();
                    meta.setCustomModelData(itemSection.getInt("cmd", 0));
                    meta.setItemName(name);
                    meta.setLore(itemSection.getStringList("lore"));
                    itemStack.setItemMeta(meta);
                } catch(IllegalArgumentException ignore) {}
            }
        }
        guiItem = itemStack;
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

    public ItemStack getGuiItem() {
        return guiItem;
    }
}
