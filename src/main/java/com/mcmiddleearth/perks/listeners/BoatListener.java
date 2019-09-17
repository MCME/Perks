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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 *
 * @author Fraspace5
 */
public class BoatListener implements Listener {
   
    
      
    // Checks if user getting on the horse has permission, and that the horse is
    // his. Otherwise removes horse.
    @EventHandler
    public void BoatMount1(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (BoatPerk.isBoatPerk(event.getVehicle())) {
                Player p = (Player) event.getEntered();
                Entity h = event.getVehicle();
                if ((!h.getCustomName().contains(p.getName()))
                            && PermissionData.isAllowed(p, PerkManager.forName("horse"))) {
                    event.setCancelled(true);
                    h.remove();
                    PerksPlugin.getMessageUtil().sendErrorMessage(p, "Sorry, this is not your horse!");
                }
            }
        }
    }
 
    // Remove horse when rider dismounts
    
      @EventHandler
    public void Boat1Dismount1(VehicleExitEvent event) {
//Logger.getGlobal().info("dismount1111111");
       EntityType vehicle = event.getVehicle().getType();
       EntityType boat = vehicle.BOAT;
        if (vehicle == boat) {
//Logger.getGlobal().info("dismount21111111");
            event.getVehicle().remove();
        }
    }

    // Remove horse if rider dies
    @EventHandler
    public void riderDie1(EntityDeathEvent event) {
        
        if (event.getEntity() instanceof Player) {
            event.getEntity().leaveVehicle();
        }
    }

    // Remove horse if rider quits
    @EventHandler
    public void riderQuit1(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
 if (player.isInsideVehicle()) {
           event.getPlayer().leaveVehicle();
            }
        }
    

    // Remove horse if rider is kicked
    @EventHandler
    public void riderKick1(PlayerKickEvent event) {
        Player player = event.getPlayer();
        
        if (player.isInsideVehicle()) {
            event.getPlayer().leaveVehicle();
            }
        }
    

    // Cancel damage to horses
    @EventHandler
    void BoatDamage1(EntityDamageEvent event) {
        if (BoatPerk.isBoatPerk(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // Block spawning of horses (bypassed temporarily by setting spawn=true [as
    // seen in CommandMethods.giveHorse()])
    // NEW: Allow spawning by plugins only.
    @EventHandler(priority = EventPriority.HIGH)
    public void mobSpawn1(CreatureSpawnEvent event) {
        if((event.getEntityType().equals(EntityType.BOAT))
            && !event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            event.setCancelled(true);
        }
        /*if(HorsePerk.isHorsePerk(event.getEntity())) {
            if (HorsePerk.isAllowSpawn()) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }*/
    }
        

    
}
