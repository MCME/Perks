package com.mcmiddleearth.perks.perks;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.commands.EquipmentHandler;
import com.mcmiddleearth.perks.listeners.EquipmentListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import com.mcmiddleearth.perks.utils.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.logging.Logger;


/**
 *
 * @author Eriol_Eandur
 */
public class EquipmentPerk extends Perk {

    private EquipmentSlot itemSlot;

    private final ItemStack item;

    public EquipmentPerk(String name) {
        super(name);
        getGuiItem().setRightCommand("closegui /perk "+name+" unequip");
        getGuiItem().setLeftCommand("closegui /perk "+name+" equip");
        setListener(new EquipmentListener(this));

        ConfigurationSection config = PerksPlugin.getPerkSettings()
                                                 .getConfigurationSection(name);
        if(config != null) {
            item = ItemStackUtil.loadItem(config.getConfigurationSection("itemStack"), null);
        } else {
            item = new ItemStack(Material.STONE);
        }
        try {
            itemSlot = EquipmentSlot.valueOf(PerksPlugin.getPerkString(this.getName(),
                    "slot", "HEAD"));
        } catch (IllegalArgumentException ex) {
            itemSlot = EquipmentSlot.HEAD;
        }

        setCommandHandler(new EquipmentHandler(this, Permissions.USER.getPermissionNode()),name);
    }

    public boolean hasItem(Player player) {
        ItemStack equipmentItem = player.getEquipment().getItem(itemSlot);
        return isItem(equipmentItem);
    }

    public boolean isItem(@Nullable ItemStack other) {
        return other != null && item.getType().equals(other.getType())
            && (item.getItemMeta() == null && other.getItemMeta() == null
                || item.getItemMeta() != null && other.getItemMeta() != null
                    && (!item.getItemMeta().hasCustomModelData() && !other.getItemMeta().hasCustomModelData()
                        || item.getItemMeta().hasCustomModelData() && other.getItemMeta().hasCustomModelData()
                            && (item.getItemMeta().getCustomModelData() == other.getItemMeta().getCustomModelData())));
    }

    public void giveItem(Player player) {
        player.getEquipment().setItem(itemSlot, item);
    }

    @Override
    public void check() {
        //check items of all players
        for(Player p: Bukkit.getOnlinePlayers()) {
            check(p);
        }
    }

    @Override
    public void disable() {
        for(Player p:Bukkit.getOnlinePlayers()) {
            removeItems(p);
        }
    }

    public void check(Player p) {
        if(!PermissionData.isAllowed(p, this)) {
            removeItems(p);
        }
//Logger.getGlobal().info("Check equipment perk items: "+getName());
        ItemStack currentSlotItem = p.getInventory().getItem(itemSlot);
        //p.getInventory().removeItemAnySlot(currentSlotItem);
        for(int i = 0; i < p.getInventory().getSize(); i++) {
            if(item.isSimilar(p.getInventory().getItem(i))) {
                p.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
        p.getInventory().setItem(itemSlot, currentSlotItem);
    }

    public void removeItems(Player p) {
        if(hasItem(p)) {
            p.getEquipment().setItem(itemSlot, new ItemStack(Material.AIR));
        }
    }

    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        //do nothing, needs to be set up manually
    }

    public String getItemName() {
        return item.getItemMeta().getDisplayName();
    }

    public Material getItemMaterial() {
        return item.getType();
    }
}
