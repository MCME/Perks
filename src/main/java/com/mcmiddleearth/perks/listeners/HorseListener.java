/*
 * Copyright (c) 2015 dags_, 2017 MCME
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 * 
 */
package com.mcmiddleearth.perks.listeners;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.BoatPerk;
import com.mcmiddleearth.perks.perks.HorsePerk;
import com.mcmiddleearth.perks.permissions.PermissionData;

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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

/**
 * 
 * @author dags_ <dags@dags.me>, Eriol_Eandur
 */

public class HorseListener implements Listener {

     
    // Checks if user getting on the horse has permission, and that the horse is
    // his. Otherwise removes horse.
    @EventHandler
    public void horseMount1(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (HorsePerk.isHorsePerk(event.getVehicle())) {
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
    public void HorseDismount(VehicleExitEvent event) {
//Logger.getGlobal().info("dismount1111111");
        if (HorsePerk.isHorsePerk(event.getVehicle())) {
//Logger.getGlobal().info("dismount21111111");
            event.getVehicle().remove();
        }
    }

    // Remove horse if rider dies
    @EventHandler
    public void riderDie(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().isInsideVehicle() 
                    && HorsePerk.isHorsePerk(event.getEntity().getVehicle())) {
                event.getEntity().getVehicle().remove();
            }
        }
    }

    // Remove horse if rider quits
    @EventHandler
    public void riderQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            if(HorsePerk.isHorsePerk(event.getPlayer().getVehicle())){
                event.getPlayer().getVehicle().remove();
            }
        }
    }

    // Remove horse if rider is kicked
    @EventHandler
    public void riderKick(PlayerKickEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            if(HorsePerk.isHorsePerk(event.getPlayer().getVehicle())){
                event.getPlayer().getVehicle().remove();
            }
        }
    }

    // Cancel damage to horses
    @EventHandler
    void horseDamage(EntityDamageEvent event) {
        if (HorsePerk.isHorsePerk(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // Block spawning of horses (bypassed temporarily by setting spawn=true [as
    // seen in CommandMethods.giveHorse()])
    // NEW: Allow spawning by plugins only.
    @EventHandler(priority = EventPriority.HIGH)
    public void mobSpawn(CreatureSpawnEvent event) {
        if((event.getEntityType().equals(EntityType.HORSE))
            && !event.getSpawnReason().equals(SpawnReason.CUSTOM)) {
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
