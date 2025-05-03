package com.mcmiddleearth.perks.listeners;

import com.mcmiddleearth.perks.perks.EquipmentPerk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EquipmentListener implements Listener {

    private final EquipmentPerk perk;

    public EquipmentListener(EquipmentPerk perk) {
        this.perk = perk;
    }

    @EventHandler
    public void onEquipmentClick(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player && perk.isItem(event.getCurrentItem())) {
            event.setCancelled(true);
        }
    }
}
