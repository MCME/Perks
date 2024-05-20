/* Copyright (C) 2017 MCME
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

import com.mcmiddleearth.perks.commands.CompanionHandler;
import com.mcmiddleearth.perks.listeners.CompanionListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 *
 * @author Fraspace5, Eriol_Eandur
 */
public class CompanionPerk extends Perk {

    private static Perk instance;

    public static final String companion_perk_custom_Name = "'s Companion!";

    private static boolean allowSpawn;

    public CompanionPerk() {
        super("companion");
        setListener(new CompanionListener());
        setCommandHandler(new CompanionHandler(this, Permissions.USER.getPermissionNode()),"companion");
    }
  
    public void clearCompanion() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isParrotPerk(e)) {
                    e.remove();
                }
            }
        }
        for(Player player: Bukkit.getOnlinePlayers()) {
            player.setShoulderEntityLeft(null);
            player.setShoulderEntityRight(null);
        }
    }
    
    public void checkCompanion() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
               if (isParrotPerk(e)) {
Logger.getGlobal().info("Parrot? "+e.getCustomName()+" " + e.getVehicle());
                   if(e.getVehicle()==null
                        || (e.getVehicle() instanceof Player 
                             && !PermissionData.isAllowed((Player)e.getVehicle(),this))) {
                      e.remove();
                   }
                }
            }
        }
    }
        
    public static boolean isParrotPerk (Entity entity) {
        return entity.getCustomName()!=null
                && entity.getCustomName().contains(companion_perk_custom_Name)
                && entity.getType().equals(EntityType.PARROT);
    }

    public static void allowSpawn (boolean allow) {
        allowSpawn = allow;
    }
    
    @Override
    public void disable() {
        clearCompanion();
    }
    
    @Override
    public void check() {
        checkCompanion();
    }
}

    
    
    

