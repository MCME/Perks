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

import com.mcmiddleearth.perks.listeners.ParrotListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import com.mcmiddleearth.perks.commands.ParrotPerkHandler;
/**
 *
 * @author Fraspace5
 */
public class ParrotPerk extends Perk {
    
    @Getter
    private static Perk instance;
    
    public static final String parrot_perk_custom_Name = "'s Parrot!";
    
    @Getter
    private static boolean allowSpawn;
    
    public ParrotPerk() {
        super("parrot");
        setListener(new ParrotListener());
        setCommandHandler(new ParrotPerkHandler(this, Permissions.USER.getPermissionNode()),"parrot");
    }
  
    public void clearParrot() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isParrotPerk(e)) {
                    e.remove();
                }
            }
        }
    }
    
    public void checkParrot() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isParrotPerk(e) && (((Vehicle)e).getPassenger()==null
                                       || !PermissionData.isAllowed((Player)((Vehicle)e).getPassenger(),
                                                                   this))) {
                    e.remove();
                }
            }
        }
    }
        
    public static boolean isParrotPerk (Entity entity) {
        return entity.getCustomName()!=null
                && entity.getCustomName().contains(parrot_perk_custom_Name)
                && entity.getType().equals(EntityType.PARROT);
    }

    public static void allowSpawn (boolean allow) {
        allowSpawn = allow;
    }
    
    @Override
    public void disable() {
        clearParrot();
    }
    
    @Override
    public void check() {
        checkParrot();
    }
}

    
    
    

