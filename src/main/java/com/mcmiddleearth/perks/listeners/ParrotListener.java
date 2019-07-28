/*
 *Copyright (C) 2017 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.perks.listeners;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.ParrotPerk;
import com.mcmiddleearth.perks.permissions.PermissionData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.event.Listener;
public class ParrotListener implements Listener {
    
    
    // Checks if user getting on the horse has permission, and that the horse is
    // his. Otherwise removes horse.
    @EventHandler
    public void ParrotMount(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Entity) {
            if (ParrotPerk.isParrotPerk(event.getEntered())) {
                Player p = (Player) event.getVehicle();
                Entity h = event.getEntered();
                if ((!h.getCustomName().contains(p.getName()))
                            && PermissionData.isAllowed(p, PerkManager.forName("parrot"))) {
                    event.setCancelled(true);
                    h.remove();
                    PerksPlugin.getMessageUtil().sendErrorMessage(p, "Sorry, this is not your parrot!");
                }
            }
        }
    }

    // Remove horse when rider dismounts
    @EventHandler
    public void ParrotDismount(VehicleExitEvent event) {
//Logger.getGlobal().info("dismount1111111");
        if (ParrotPerk.isParrotPerk(event.getVehicle())) {
//Logger.getGlobal().info("dismount21111111");
            event.getVehicle().remove();
        }
    }

    // Remove horse if rider dies
    @EventHandler
    public void riderDie(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().isDead()
                    && ParrotPerk.isParrotPerk(event.getEntity().getVehicle())) {
                event.getEntity().getVehicle().remove();
            }
        }
    }

    // Remove horse if rider quits
    @EventHandler
    public void riderQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            if(ParrotPerk.isParrotPerk(event.getPlayer().getVehicle())){
                event.getPlayer().getVehicle().remove();
            }
        }
    }

    // Remove horse if rider is kicked
    @EventHandler
    public void riderKick(PlayerKickEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            if(ParrotPerk.isParrotPerk(event.getPlayer().getVehicle())){
                event.getPlayer().getVehicle().remove();
            }
        }
    }

    // Cancel damage to horses
    @EventHandler
    void horseDamage(EntityDamageEvent event) {
        if (ParrotPerk.isParrotPerk(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // Block spawning of horses (bypassed temporarily by setting spawn=true [as
    // seen in CommandMethods.giveHorse()])
    // NEW: Allow spawning by plugins only.
    @EventHandler(priority = EventPriority.HIGH)
    public void mobSpawn(CreatureSpawnEvent event) {
        if((event.getEntityType().equals(EntityType.PARROT))
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
