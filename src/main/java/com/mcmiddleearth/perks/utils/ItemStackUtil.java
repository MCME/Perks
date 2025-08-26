package com.mcmiddleearth.perks.utils;

import com.google.common.base.Joiner;
import com.google.gson.JsonParseException;
import com.mcmiddleearth.perks.permissions.PermissionData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.logging.Logger;

public class ItemStackUtil {

    public static ItemStack loadItem(@Nullable ConfigurationSection itemConfig, PlaceholderData data) {
        if(itemConfig == null) {
            return new ItemStack(Material.STONE);
        }
        ItemStack item = new ItemStack(Material.STONE);
        String[] itemData = itemConfig.getString("material", Material.STONE.name()).toUpperCase().split(":");
//Logger.getGlobal().info("Loaditem from: "+ Joiner.on(" ").join(itemConfig.getKeys(false)));
//Logger.getGlobal().info("Materialdata: "+itemConfig.getString("material"));
//Logger.getGlobal().info("Material: "+itemData[itemData.length-1]);
        try {
            item = new ItemStack(Material.valueOf(itemData[itemData.length-1]));
        } catch (IllegalArgumentException ex) {
            Logger.getGlobal().warning("Invalid item stack material: "+itemData[itemData.length-1]);
        }
//Logger.getGlobal().info("Item mat: "+item.getType());
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(itemConfig.getInt("cmd",0));
        try {
            meta.lore(itemConfig.getStringList("lore").stream().map(line -> {
                line = replacePlaceholders(line, data);
                if(line.startsWith("{")) {
                    //Component comp = JSONComponentSerializer.json().deserialize(line);
                    //return LegacyComponentSerializer.legacySection().serialize(comp);
                    return JSONComponentSerializer.json().deserialize(line);
                } else {
                    return Component.text(line);
                    //return line;
                }
            }).toList());
            String nameJson = replacePlaceholders(itemConfig.getString("name", "{\"text\":\" \"}"), data);
            if(nameJson.startsWith("{")) {
                //meta.setDisplayName(LegacyComponentSerializer.legacySection().serialize(JSONComponentSerializer.json().deserialize(nameJson)));
                meta.displayName(JSONComponentSerializer.json().deserialize(nameJson));
            } else {
                meta.setDisplayName(nameJson);
            }
        } catch(JsonParseException ex) {
            Logger.getGlobal().warning("Json Parse Error: "+itemConfig.getString("name", ""));
        }
        item.setItemMeta(meta);
        return item;
    }

    private static String replacePlaceholders(String line, PlaceholderData data) {
        if(data != null) {
            String tiers = "None";
            if(data.getTiers() != null && !data.getTiers().isEmpty()) {
                tiers = Joiner.on(", ").join(data.getTiers());
            }
            line = line.replace("<<<CURRENT TIER>>>", tiers);
            line = line.replace("<<<TOTAL>>>", String.format("%.0f $",data.getTotal()));
            String requiredTiers = "None";
            if(data.getRequiredTiers() != null && !data.getRequiredTiers().isEmpty()) {
                requiredTiers = Joiner.on(", ").join(data.getRequiredTiers());
            }
            line = line.replace("<<<REQUIRED TIER>>>", requiredTiers);
            line = line.replace("<<<REQUIRED TOTAL>>>", String.format("%.0f $",data.getRequiredTotal()));
            line = line.replace("<<<ADD / REMOVE FAVORITE>>>", (data.isFavorite()?
                    "Right-click to remove from favorites":
                    "Right-click to add to favorites"));
        }
        return line;
    }
}
