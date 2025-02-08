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

import com.mcmiddleearth.perks.perks.CompanionPerk;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class CompanionListener implements Listener {
    
    
    // Block spawning of horses (bypassed temporarily by setting spawn=true [as
    // seen in CommandMethods.giveHorse()])
    // NEW: Allow spawning by plugins only.
    @EventHandler(priority = EventPriority.HIGH)
    public void mobSpawn(CreatureSpawnEvent event) {
        if(((event.getEntityType().equals(EntityType.WOLF)) || (event.getEntityType().equals(EntityType.CAT)))
            && !event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerFly(PlayerToggleFlightEvent event) {
        if (event.isFlying()) {
            CompanionPerk.dismissCompanions(event.getPlayer());
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        CompanionPerk.dismissCompanions(event.getPlayer());
    }
}
