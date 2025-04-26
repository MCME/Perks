package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.permissions.CreditData;
import com.mcmiddleearth.perks.permissions.PermissionData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class PerkGui {

    //private static final List<PerkItem> hats = new ArrayList<>();
    //private static final List<PerkItem> favorites = new ArrayList<>();
    //private static final List<PerkItem> allPerks = new ArrayList<>();
    //private static final List<DefinitionItem> morePerks = new ArrayList<>();

    private final ListDisplay morePerks;
    private final ListDisplay favoritePerks;
    private final ListDisplay hats;
    private final ListDisplay allPerks;

    private static boolean displayAllPerks = false;

    private static final int guiSlots = 36;
    public Inventory inventory;

    private static final int allPerksSlot = 23;
    private static final int forumSlot = 14;
    private static final int patreonSlot = 5;
    private static final int returnSlot = 0;

    public PerkGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, guiSlots, Component.text("Perks").color(NamedTextColor.YELLOW));

        morePerks = new ListDisplay(inventory, null, 0,2,5, false);
        favoritePerks = new ListDisplay(inventory, null, 27, 1, 9, false);
        hats = new ListDisplay(inventory, null, 15, 1, 3, true);
        allPerks = new ListDisplay(inventory, null, 0, 4, 9, false);

        placeButtonsFirstPage();

        hats.display();
        favoritePerks.display();
        morePerks.display();
        player.openInventory(inventory);
    }

    public void onClick(Player player, int slot, ClickType clickType) {
        if(slot < guiSlots ) {
            if (displayAllPerks) {
                if (allPerks.handleClick(slot, clickType)) {
                    return;
                } else if(slot == allPerksSlot) {
                    displayAllPerks = true;
                    allPerks.display();
                    placeButtonsSecondPage();
                }
            } else {
                if (morePerks.handleClick(slot, clickType)) {
                    return;
                } else if (favoritePerks.handleClick(slot, clickType)) {
                    return;
                } else if (hats.handleClick(slot, clickType)) {
                    return;
                } else if(slot == returnSlot) {
                    morePerks.display();
                    favoritePerks.display();
                    hats.display();
                    placeButtonsFirstPage();
                }
            }
            /*if(event.getRawSlot()>17) {
                Player player = (Player)event.getWhoClicked();
                openInventories.remove(event.getInventory());
                event.getWhoClicked().closeInventory();
                String command = event.getInventory().getItem(event.getRawSlot()).getItemMeta().getItemName();
                Logger.getGlobal().info("command: "+command);
                PerksPlugin.getInstance().getPerksExecutor().onCommand(player, null, "perk", command.split(" "));
                //Bukkit.dispatchCommand(player, "/perk "+event.getInventory().getItem(event.getRawSlot()).getItemMeta().getItemName());
            } else {
                event.setCancelled(true);
            }*/
        }
    }

    public void close(){
        inventory.close();
    }

    private void placeButtonsFirstPage() {
        //todo: place patreon and forum button and deco buttons
    }

    private  void placeButtonsSecondPage() {
        //todo: return button
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

        ItemStack testItem = new ItemStack(Material.STONE);
        ItemMeta meta = testItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        meta.setCustomModelData(1);
        lore.add("Test cmd 1: ");
        meta.setLore(lore);
        testItem.setItemMeta(meta);
Logger.getGlobal().info("Set Test Item slot 22");
        inventory.setItem(22, testItem);


        player.openInventory(inventory);
        openInventories.add(inventory);
    }

    public boolean hasInventory(Inventory inventory) {
        return this.inventory == inventory;
    }
}
