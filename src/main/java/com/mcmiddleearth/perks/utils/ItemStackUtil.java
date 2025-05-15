package com.mcmiddleearth.perks.utils;

import com.google.common.base.Joiner;
import com.google.gson.JsonParseException;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.logging.Logger;

public class ItemStackUtil {

    public static ItemStack loadItem(@Nullable ConfigurationSection itemConfig) {
        if(itemConfig == null) {
            return new ItemStack(Material.STONE);
        }
        ItemStack item = new ItemStack(Material.STONE);
        String[] itemData = itemConfig.getString("material", Material.STONE.name()).toUpperCase().split(":");
Logger.getGlobal().info("Loaditem from: "+ Joiner.on(" ").join(itemConfig.getKeys(false)));
Logger.getGlobal().info("Materialdata: "+itemConfig.getString("material"));
Logger.getGlobal().info("Material: "+itemData[itemData.length-1]);
        try {
            item = new ItemStack(Material.valueOf(itemData[itemData.length-1]));
        } catch (IllegalArgumentException ex) {
            Logger.getGlobal().warning("Invalid item stack material: "+itemData[itemData.length-1]);
        }
Logger.getGlobal().info("Item mat: "+item.getType());
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(itemConfig.getInt("cmd",0));
        try {
            meta.lore(itemConfig.getStringList("lore").stream().map(line -> JSONComponentSerializer.json().deserialize(line)).toList());
            meta.displayName(JSONComponentSerializer.json().deserialize(itemConfig.getString("name", "{\"text\":\" \"}")));
        } catch(JsonParseException ex) {
            meta.setDisplayName("Json Parse Error: "+itemConfig.getString("name", ""));
            Logger.getGlobal().warning("Json Parse Error: "+itemConfig.getString("name", ""));
        }
        item.setItemMeta(meta);
        return item;
    }
}
