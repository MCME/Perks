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
package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.BoatPerk;
import com.mcmiddleearth.perks.perks.Perk;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.entity.boat.*;

import java.util.Map;


/**
 *
 * @author Fraspace5, Eriol_Eandur
 */
public class BoatHandler extends PerksCommandHandler {

    private static final Map<String, Class<? extends Boat>> BOAT_TYPES = Map.of(
        "oak", OakBoat.class,
        "acacia", AcaciaBoat.class,
        "birch", BirchBoat.class,
        "dark_oak", DarkOakBoat.class,
        "jungle", JungleBoat.class,
        "spruce", SpruceBoat.class,
        "mangrove", MangroveBoat.class,
        "cherry", CherryBoat.class,
        "bamboo", BambooRaft.class,
        "pale_oak", PaleOakBoat.class
    );

    public BoatHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Gives you a boat!";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[woodType]: Gives you a boat to navigate. Without arguments it will be an oak boat. "
                +"Possible wood types are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"oak, acacia, birch, dark_oak, jungle, spruce, mangrove, cherry, bamboo, pale_oak"+PerksPlugin.getMessageUtil().HIGHLIGHT;
    }
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player player = (Player)cs;
        if (player.isInsideVehicle()) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You are already in a boat!");
            return;
        }
        if(args.length>0 && args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Get a boat: /perk boat [woodType] ");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[woodType] -> "+ChatColor.GREEN+"oak, acacia, birch, dark_oak, jungle, spruce, mangrove, cherry, bamboo, pale_oak");
           return;
        }


        Location location = player.getLocation();
        World world = player.getWorld();
        Class<? extends Boat> boatClass = OakBoat.class;
        if(args.length>0) {
            Class<? extends Boat> resolved = BOAT_TYPES.get(args[0].toLowerCase());
            if(resolved != null) {
                boatClass = resolved;
            } else {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs, "Boat type not found! Using oak boat.");
            }
        }
        BoatPerk.allowSpawn(true);
        Boat boat = world.spawn(location,boatClass);
        boat.setCustomName(ChatColor.DARK_AQUA + player.getName()
                + BoatPerk.boat_perk_custom_Name);
        boat.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");

    }

}
