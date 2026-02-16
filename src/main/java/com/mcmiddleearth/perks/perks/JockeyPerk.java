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

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.listeners.JockeyListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class JockeyPerk extends Perk {
    
    private static final String antiJockeyPermission
            = PerksPlugin.getPerkString("jockey","antiJockeyPermission","perks.antijockey");
    
    private static Material itemMaterial;
    
    public JockeyPerk() {
        super("jockey");
        itemMaterial = Material.valueOf(PerksPlugin.getPerkString(this.getName(),"item","LEASH"));
        setListener(new JockeyListener());
    }
    
    @Override
    public void disable() {
        for(Player player: Bukkit.getOnlinePlayers()) {
            unjockey(player);
        }
    }

    @Override
    public void check() {
        for(Player player:Bukkit.getOnlinePlayers()) {
            if(!PermissionData.isAllowed(player, this)) {
                unjockey(player);
            }
        }
    }
    
    public static void jockey(Player jockey) {
        Entity ride = null;
        for (Entity e : jockey.getNearbyEntities(3, 3, 3)) {
                if (e instanceof Player) {
                        if (!e.isInsideVehicle()) {
                                ride = e;
                                break;
                        }
                }
        }
        if (ride != null) {
                Player q = (Player) ride;
                if(!q.hasPermission(antiJockeyPermission)){
                        ride.addPassenger(jockey);
                        PerksPlugin.getMessageUtil().sendInfoMessage(jockey,"Now riding "
                                + PerksPlugin.getMessageUtil().STRESSED + q.getName() 
                                + PerksPlugin.getMessageUtil().INFO + "!");
                } else {
                        PerksPlugin.getMessageUtil().sendErrorMessage(jockey,"You cannot ride that player!");
                }
        } else {
                PerksPlugin.getMessageUtil().sendInfoMessage(jockey,"There are no nearby players to ride!");
        }
    }
    
    public static void unjockey(Player jockey) {
        if(jockey.isInsideVehicle()) {
            jockey.leaveVehicle();
        }
    }
    
    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        config.set("antiJockeyPermission",antiJockeyPermission);
        config.set("item", itemMaterial.name());
    }

    public static String getAntiJockeyPermission() {
        return antiJockeyPermission;
    }

    public static Material getItemMaterial() {
        return itemMaterial;
    }
}
    