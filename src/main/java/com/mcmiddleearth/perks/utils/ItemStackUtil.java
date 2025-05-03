package com.mcmiddleearth.perks.utils;

import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class ItemStackUtil {

    public static ItemStack loadItem(@Nullable ConfigurationSection itemConfig) {
        if(itemConfig == null) {
            return new ItemStack(Material.STONE);
        }
        ItemStack item = new ItemStack(Material.valueOf(itemConfig.getString("material", Material.STONE.name())));
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(itemConfig.getInt("cmd",0));
        meta.lore(itemConfig.getStringList("lore").stream().map(line -> JSONComponentSerializer.json().deserialize(line)).toList());
        meta.displayName(JSONComponentSerializer.json().deserialize(itemConfig.getString("name","")));
        item.setItemMeta(meta);
        return item;
    }
}
