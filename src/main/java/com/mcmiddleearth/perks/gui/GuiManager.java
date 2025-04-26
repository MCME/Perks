package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerksPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {

    private static final Map<UUID, PerkGui> openGuis = new HashMap<>();

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        PerkGui gui = openGuis.get(event.getWhoClicked().getUniqueId());
        if(gui!=null && event.getWhoClicked() instanceof Player player) {
            gui.onClick(player, event.getRawSlot(), event.getClick());
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
        PerkGui gui = new PerkGui();
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
}
