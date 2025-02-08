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
package com.mcmiddleearth.perks.perks;

import com.mcmiddleearth.perks.commands.BoatHandler;
import com.mcmiddleearth.perks.listeners.BoatListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

/**
 *
 * @author Fraspace5
 */
public class BoatPerk extends Perk {
    
    private static Perk instance;
    
    public static final String boat_perk_custom_Name = "'s Boat!";
    
    private static boolean allowSpawn;
    
    public BoatPerk() {
        super("boat");
        setListener(new BoatListener());
        setCommandHandler(new BoatHandler(this, Permissions.USER.getPermissionNode()),"boat");
    }
  
    public void clearBoat() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isBoatPerk(e)) {
                    e.remove();
                }
            }
        }
    }
    
    public void checkBoat() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isBoatPerk(e) && (((Vehicle)e).getPassenger()==null
                                       || !PermissionData.isAllowed((Player)((Vehicle)e).getPassenger(),
                                                                   this))) {
                    e.remove();
                }
            }
        }
    }
        
    public static boolean isBoatPerk(Entity entity) {
        return entity.getCustomName()!=null
                && entity.getCustomName().contains(boat_perk_custom_Name)
                && entity instanceof Boat;
    }

    public static void allowSpawn(boolean allow) {
        allowSpawn = allow;
    }
    
    @Override
    public void disable() {
        clearBoat();
    }
    
    @Override
    public void check() {
        checkBoat();
    }

    public boolean isBoatPlacementAllowed() {
        return allowSpawn;
    }


}
