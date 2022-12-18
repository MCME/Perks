/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.perks.listeners;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.BoatPerk;
import com.mcmiddleearth.perks.permissions.PermissionData;
import java.util.logging.Logger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 *
 * @author Fraspace5
 */
public class BoatListener implements Listener {

    /*
    // Checks if user getting on the boat has permission, and that the boat is
    // his. Otherwise removes boat when empty.
    @EventHandler
    public void BoatMount(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (BoatPerk.isBoatPerk(event.getVehicle())) {
                Player p = (Player) event.getEntered();
                Entity h = event.getVehicle();
                if (!(//h.getCustomName().contains(p.getName()) &&
                            PermissionData.isAllowed(p, PerkManager.forName("boat")))) {
                    event.setCancelled(true);
                    if(event.getVehicle().isEmpty()) {
                        h.remove();
                    }
                    PerksPlugin.getMessageUtil().sendErrorMessage(p, "Sorry, this is not your boat!");
                }
            }
        }
    }
     */

    // Remove boat when rider dismounts
    @EventHandler
    public void BoatDismount(VehicleExitEvent event) {
//Logger.getGlobal().info("dismount1");
        if (BoatPerk.isBoatPerk(event.getVehicle()) && event.getVehicle().getPassengers().size()<=1) {
//Logger.getGlobal().info("dismount2");
            event.getVehicle().remove();
        }
    }

    // Remove boat if rider dies
    @EventHandler
    public void riderDie(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().isInsideVehicle() 
                    && BoatPerk.isBoatPerk(event.getEntity().getVehicle())
                    && event.getEntity().getVehicle().getPassengers().size()<=1) {
                event.getEntity().getVehicle().remove();
            }
        }
    }

    // Remove boat if rider quits
    @EventHandler
    public void riderQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            if(BoatPerk.isBoatPerk(event.getPlayer().getVehicle())
                    && event.getPlayer().getVehicle().getPassengers().size()<=1){
                event.getPlayer().getVehicle().remove();
            }
        }
    }

    // Remove boat if rider is kicked
    @EventHandler
    public void riderKick(PlayerKickEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            if(BoatPerk.isBoatPerk(event.getPlayer().getVehicle())
                    && event.getPlayer().getVehicle().getPassengers().size()<=1){
                event.getPlayer().getVehicle().remove();
            }
        }
    }

    // Block spawning of boats (bypassed temporarily by setting spawn=true [as
    // seen in CommandMethods.giveHorse()])
    // NEW: Allow spawning by plugins only.
    
    /*
    is not called
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void boatSpawn(EntitySpawnEvent event) {
        
        //Logger.getGlobal().info("EntitySpawn: "+event.getEntityType());
        if((event.getEntityType().equals(EntityType.BOAT))
            && ((BoatPerk)PerkManager.forName("boat")).isBoatPlacementAllowed()) {
            event.setCancelled(true);
        }
        /*if(HorsePerk.isHorsePerk(event.getEntity())) {
            if (HorsePerk.isAllowSpawn()) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }/
    }*/
        

    
}
