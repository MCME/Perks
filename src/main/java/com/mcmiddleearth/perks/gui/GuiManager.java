package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.EquipmentPerk;
import com.mcmiddleearth.perks.perks.Perk;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GuiManager implements Listener {

    private static final Map<UUID, PerkGui> openGuis = new HashMap<>();

    private static final File guiItemFile = new File(PerksPlugin.getInstance().getDataFolder(),"guiItems.yml");

    private static final YamlConfiguration guiItemConfig = new YamlConfiguration();

    private static final File favoritesFile = new File(PerksPlugin.getInstance().getDataFolder(), "favorites.yml");

    private static final Map<UUID, Set<Perk>> favorites = new HashMap<>();

    public GuiManager() {
        try {
            guiItemConfig.load(guiItemFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GuiItem> getFavoritePerks(Player player) {
        return null;
    }

    public static List<GuiItem> getHats(Player player) {
        return PerkManager.getPerks().stream().filter(perk -> perk instanceof EquipmentPerk && player.hasPermission(perk.getPermissionNode()))
                .map(Perk::getGuiItem).toList();
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        PerkGui gui = openGuis.get(event.getWhoClicked().getUniqueId());
        if(gui!=null && event.getWhoClicked() instanceof Player player) {
            event.setCancelled(gui.handleClick(player, event.getRawSlot(), event.getClick()));
        }
    }

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        removeGui(event.getPlayer().getUniqueId());
    }

    private static void removeGui(UUID player) {
        openGuis.remove(player);
    }

    public static void openGui(Player player) {
        PerkGui gui = new PerkGui(player);
        openGuis.put(player.getUniqueId(), gui);
    }

    public static void closeGui(Inventory inventory) {
        UUID removal = null;
        PerkGui gui = null;
        for(Map.Entry<UUID, PerkGui> entry: openGuis.entrySet()) {
            if(entry.getValue().hasInventory(inventory)) {
                removal = entry.getKey();
                gui = entry.getValue();
                break;
            }
        }
        if(removal!=null) {
            removeGui(removal);
            Bukkit.getScheduler().runTask(PerksPlugin.getInstance(), gui::close);
        }
    }

    public static GuiItem getGuiItem(String key) {
        return GuiItem.load(guiItemConfig.getConfigurationSection(key));
    }
}
