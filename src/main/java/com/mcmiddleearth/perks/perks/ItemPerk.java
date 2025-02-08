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
import com.mcmiddleearth.perks.commands.ItemHandler;
import com.mcmiddleearth.perks.listeners.ItemListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class ItemPerk extends Perk {

    private String itemName = PerksPlugin.getPerkString(this.getName(),
            "itemName", null);
    private int itemQuantity = PerksPlugin.getPerkInt(this.getName(),
            "itemQuantity", -1);

    private final Material itemMaterial;

    public ItemPerk(String name, Material itemMaterial, String itemName, int quantity) {
        super(name);
        if(this.itemName==null) {
            this.itemName = itemName;
        }
        if(itemQuantity == -1) {
            itemQuantity = quantity;
        }
        setListener(new ItemListener(this));
        String mat = PerksPlugin.getPerkString(this.getName(), "itemMaterial", null);
        if(mat==null) {
            this.itemMaterial = itemMaterial;
        } else {
            this.itemMaterial = Material.valueOf(mat);
        }

        setCommandHandler(new ItemHandler(this, Permissions.USER.getPermissionNode()),name);
    }

    public boolean hasItem(Player player) {
        return player.getInventory().contains(itemMaterial);
    }

    public void giveItem(Player player) {
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        String name = (itemName.equalsIgnoreCase("Ring of Power")?"prreciousss":itemName);
        meta.setDisplayName(player.getDisplayName()+"'s "+name);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        item.setAmount(itemQuantity);
        player.getInventory().addItem(item);
    }

    @Override
    public void check() {
        //check items of all players
        for(Player p:Bukkit.getOnlinePlayers()) {
            check(p);
        }
    }

    @Override
    public void disable() {
        //remove elytras of all players
        for(Player p:Bukkit.getOnlinePlayers()) {
            removeItems(p);
        }
    }

    public void check(Player p) {
        if(!PermissionData.isAllowed(p, this)) {
            removeItems(p);
        }
    }

    public void removeItems(Player p) {
        PlayerInventory inv = p.getInventory();
        inv.remove(itemMaterial);
        ItemStack[]armor = inv.getArmorContents();
        for(int i=0; i<armor.length;i++) {
            if(armor[i]!=null && armor[i].getType().equals(itemMaterial)) {
                armor[i] = new ItemStack(Material.AIR);
            }
        }
        inv.setArmorContents(armor);
        ItemStack[]extra = inv.getExtraContents();
        for(int i=0; i<extra.length;i++) {
            if(extra[i]!=null && extra[i].getType().equals(itemMaterial)) {
                extra[i] = new ItemStack(Material.AIR);
            }
        }
        inv.setExtraContents(extra);
        if(inv.getItemInOffHand()!=null && inv.getItemInOffHand().getType().equals(itemMaterial)){
            inv.setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        config.set("itemName", itemName);
        config.set("item", itemMaterial.name());
        config.set("itemQuantity", itemQuantity);
    }

    public String getItemName() {
        return itemName;
    }

    public Material getItemMaterial() {
        return itemMaterial;
    }
}
