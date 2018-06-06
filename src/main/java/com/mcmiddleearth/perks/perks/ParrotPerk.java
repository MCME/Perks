/*
 * Copyright (C) 2017 MCME
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

import com.mcmiddleearth.perks.commands.ParrotHandler;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author Julius_the_great
 */
public class ParrotPerk extends Perk {
    
    @Getter
    private static Perk instance;
    
    public static final String parrot_perk_custom_name = "'s Colorful Companion";
    
    @Getter
    private static boolean allowSpawn;   
    
    public ParrotPerk() {
        super("parrot");
        setCommandHandler(new ParrotHandler(this, Permissions.USER.getPermissionNode()), "parrot");
    }
    
    public void clearParrots() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isParrotPerk(e)) {
                    e.remove();
                }
            }
        }
    }
    
    /* Not quite sure what to do with this method
    public void checkParrots() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isParrotPerk(e)) {
                    e.remove();
                }
            }
        }
    }
*/
    
    public boolean isParrotPerk(Entity e) {
                return e.getCustomName()!=null
                && e.getCustomName().contains(parrot_perk_custom_name)
                && e.getType().equals(EntityType.PARROT);
    }
    
    public static void allowSpawn(boolean allow) {
        allowSpawn = allow;
    }
    
    @Override
    public void disable() {
        clearParrots();
    }
    
    /*@Override
    public void check() {
        checkParrots();
    }
*/
    
}
