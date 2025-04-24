package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.permissions.CreditData;
import com.mcmiddleearth.perks.permissions.PermissionData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;

public class PerkGui implements Listener {

    private static final int guiSlots = 36;
    public static Set<Inventory> openInventories = new HashSet<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(openInventories.contains(event.getInventory()) && event.getRawSlot() < guiSlots ) {
            if(event.getRawSlot()>17) {
                Player player = (Player)event.getWhoClicked();
                openInventories.remove(event.getInventory());
                event.getWhoClicked().closeInventory();
                Bukkit.dispatchCommand(player, "/perk "+event.getInventory().getItem(event.getRawSlot()).getItemMeta().getItemName());
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(openInventories.contains(event.getInventory())) {
            openInventories.remove(event.getInventory());
        }
    }

    public static void openPerkGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, guiSlots, Component.text("Perks").color(NamedTextColor.YELLOW));

        CreditData creditData = PermissionData.getCredits(player);

        if(creditData!=null) {
            ItemStack totalItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta meta = totalItem.getItemMeta();
            meta.customName(Component.text("Your total support sum:").color(NamedTextColor.YELLOW));
            meta.lore(Collections.singletonList(Component.text(creditData.getTotal())));
            totalItem.setItemMeta(meta);
            inventory.setItem(1, totalItem);

            ItemStack forumItem = new ItemStack(Material.GOLD_NUGGET);
            meta = forumItem.getItemMeta();
            meta.customName(Component.text("Your forum support: ").color(NamedTextColor.YELLOW));
            meta.lore(Collections.singletonList(Component.text(creditData.getForum())));
            forumItem.setItemMeta(meta);
            inventory.setItem(2, forumItem);

            ItemStack patreonItem = new ItemStack(Material.STONE);
            meta = patreonItem.getItemMeta();
            meta.setLore(Collections.singletonList("Patreon support: " + creditData.getPatreon()));
            patreonItem.setItemMeta(meta);
            inventory.setItem(3, patreonItem);

            ItemStack tierItem = new ItemStack(Material.STONE);
            meta = tierItem.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("Patreon tiers: ");
            lore.addAll(creditData.getTiers());
            meta.setLore(lore);
            tierItem.setItemMeta(meta);
            inventory.setItem(4, tierItem);

            ConfigurationSection config = PermissionData.getPerkDefinitions();

            int i = 9;
            for(String key: config.getKeys(false)) {
                ConfigurationSection definition = config.getConfigurationSection(key);
                ItemStack definitionItem = new ItemStack(Material.DIAMOND);
                meta = definitionItem.getItemMeta();
                meta.customName(Component.text(key).color(NamedTextColor.YELLOW));
                List<Component> loreC = new ArrayList<>();
                loreC.add(Component.text("Requirements:"));
                if(definition.contains("total"))
                    loreC.add(Component.text("  Total support: "+definition.getInt("total"))
                                      .color(definition.getInt("total") > creditData.getTotal()
                                                ? NamedTextColor.RED:NamedTextColor.GREEN));
                if(definition.contains("forum"))
                    loreC.add(Component.text("  Forum support: "+definition.getInt("forum"))
                                      .color(definition.getInt("forum")>creditData.getForum()
                                                ? NamedTextColor.RED:NamedTextColor.GREEN));
                if(definition.contains("patreon"))
                    loreC.add(Component.text("  Patreon support: "+definition.getInt("patreon"))
                                      .color(definition.getInt("patreon")>creditData.getPatreon()
                                                ? NamedTextColor.RED:NamedTextColor.GREEN));
                if(definition.contains("tiers")) {
                    loreC.add(Component.text("  Tiers: "));
                    for(String tier: definition.getStringList("tiers")) {
                        loreC.add(Component.text("    - "+tier)
                                .color(!creditData.getTiers().contains(tier)
                                        ? NamedTextColor.RED:NamedTextColor.GREEN));
                    }
                }
                if(definition.contains("perks")) {
                    loreC.add(Component.text("Awarded perks:"));
                    for(String perk: definition.getStringList("perks")) {
                        loreC.add(Component.text("  - "+perk)
                                .color(!PermissionData.isAllowed(player, PerkManager.forName(perk))
                                        ?NamedTextColor.RED:NamedTextColor.GREEN));
                    }
                }
                meta.lore(loreC);
                definitionItem.setItemMeta(meta);
                inventory.setItem(i, definitionItem);
                i++;
            }
        } else {
            ItemStack errorItem = new ItemStack(Material.STONE);
            ItemMeta meta = errorItem.getItemMeta();
            meta.setLore(Collections.singletonList("Not available, try again in a minute!"));
            errorItem.setItemMeta(meta);
            inventory.setItem(3, errorItem);
        }

        int i = 18;
        for(Perk perk: PerkManager.getPerks()) {
            ItemStack item = perk.getGuiItem();
            if(item != null) {
                inventory.setItem(i, item);
                i++;
            }
        }

        player.openInventory(inventory);
        openInventories.add(inventory);
    }
}
