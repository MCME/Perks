package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.permissions.CreditData;
import com.mcmiddleearth.perks.permissions.PermissionData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class PerkGui {

    //private static final List<PerkItem> hats = new ArrayList<>();
    //private static final List<PerkItem> favorites = new ArrayList<>();
    //private static final List<PerkItem> allPerks = new ArrayList<>();
    //private static final List<DefinitionItem> morePerks = new ArrayList<>();
    private static final int guiSlots = 36;

    private static final int allPerksSlot = 23;
    private static final int forumSlot = 14;
    private static final int patreonSlot = 5;
    private static final int returnSlot = 0;
    private static final int backgroundSlot = 22;

    private final ListDisplay morePerks;
    private final ListDisplay favoritePerks;
    private final ListDisplay hats;
    private final ListDisplay allPerks;

    private boolean displayAllPerks = false;

    private final Inventory inventory;

    private final GuiItem forumItem, patreonItem, allPerksItem, returnItem, backgroundItem;

    public PerkGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, guiSlots, Component.text("Perks").color(NamedTextColor.YELLOW));

        morePerks = new ListDisplay(inventory, PermissionData.getPerkDefinitionItems(player),
                        0,2,5, false,
                        GuiManager.getGuiItem("morePerks.previous"), GuiManager.getGuiItem("morePerks.next"));
        favoritePerks = new ListDisplay(inventory, GuiManager.getFavoritePerks(player), 27, 1, 9,
                        GuiManager.getGuiItem("favoritePerks.previous.enabled"), GuiManager.getGuiItem("favoritePerks.next.enabled"),
                        GuiManager.getGuiItem("favoritePerks.previous.disabled"), GuiManager.getGuiItem("favoritePerks.next.disabled"));
        hats = new ListDisplay(inventory, GuiManager.getHats(player), 15, 1, 3,
                        GuiManager.getGuiItem("hats.previous.enabled"), GuiManager.getGuiItem("hats.next.enabled"),
                        GuiManager.getGuiItem("hats.previous.disabled"), GuiManager.getGuiItem("hats.next.disabled"));
        allPerks = new ListDisplay(inventory, PerkManager.getPerks().stream().map(Perk::getGuiItem).toList(),
                0, 4, 9, false,
                GuiManager.getGuiItem("allPerks.previous"), GuiManager.getGuiItem("allPerks.next"));

        hats.display();
        favoritePerks.display();
        morePerks.display();

        forumItem = GuiManager.getGuiItem("forum");
        patreonItem = GuiManager.getGuiItem("patreon");
        allPerksItem = GuiManager.getGuiItem("allPerks");
        returnItem = GuiManager.getGuiItem("return");
        backgroundItem = GuiManager.getGuiItem("label.background");

        this.inventory = inventory;

        placeButtonsFirstPage();
        player.openInventory(inventory);
    }

    public boolean handleClick(Player player, int slot, ClickType clickType) {
        if(slot < guiSlots ) {
            if (displayAllPerks) {
                if (!allPerks.handleClick(player, slot, clickType)) {
                    switch(slot) {
                        case allPerksSlot:
                            displayAllPerks = true;
                            allPerks.display();
                            placeButtonsSecondPage();
                            break;
                        case forumSlot:
                            forumItem.handleClick(player, clickType);
                            break;
                        case patreonSlot:
                            patreonItem.handleClick(player, clickType);
                            break;
                    }
                }
            } else {
                if(!morePerks.handleClick(player, slot, clickType)) {
                   if(!favoritePerks.handleClick(player, slot, clickType)) {
                       if (!hats.handleClick(player, slot, clickType)) {
                           if (slot == returnSlot) {
                               morePerks.display();
                               favoritePerks.display();
                               hats.display();
                               placeButtonsFirstPage();
                           }
                       }
                   }
                }
            }
            return true;
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
        } else {
            return false;
        }
    }

    public void close(){
        inventory.close();
    }

    private void placeButtonsFirstPage() {
        inventory.setItem(forumSlot, forumItem.getItemStack());
        inventory.setItem(patreonSlot, patreonItem.getItemStack());
        inventory.setItem(allPerksSlot, allPerksItem.getItemStack());
        inventory.setItem(backgroundSlot, backgroundItem.getItemStack());
    }

    private  void placeButtonsSecondPage() {
        inventory.setItem(returnSlot, returnItem.getItemStack());
    }

    public static void openPerkGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, guiSlots, Component.text("Perks").color(NamedTextColor.YELLOW));

        CreditData creditData = PermissionData.getCredits(player);

        ItemStack item0=null,item1=null,item2=null;
        if(creditData!=null) {
            ItemStack totalItem = new ItemStack(Material.GOLD_NUGGET);
            ItemMeta meta = totalItem.getItemMeta();
            meta.customName(Component.text("Your total support sum:").color(NamedTextColor.YELLOW));
            meta.lore(Collections.singletonList(Component.text(creditData.getTotal())));
            totalItem.setItemMeta(meta);
            inventory.setItem(1, totalItem);
            item0 = totalItem;

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
                item1 = definitionItem;
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
            GuiItem item = perk.getGuiItem();
            if(item != null) {
                inventory.setItem(i, item.getItemStack());
                i++;
            }
            item2=item.getItemStack();
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

        File testFile = new File(PerksPlugin.getInstance().getDataFolder(),"test.yml");
        if(!testFile.exists()) {
            try {
                testFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        config.set("item3", item0.serialize());
        config.set("item1", item1.serialize());
        config.set("item2", item2.serialize());

        try {
            config.save(testFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        player.openInventory(inventory);
        //openInventories.add(inventory);
    }

    public boolean hasInventory(Inventory inventory) {
        return this.inventory == inventory;
    }
}
