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
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;


/**
 *
 * @author Fraspace5
 */
public class BoatHandler extends PerksCommandHandler {
    
    public BoatHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Gives you a boat!";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[color] [pattern]: Gives you a boat to navigate. Without arguments it will have a default woodtype [Generic] "
                +"Possible wood types are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"acacia, birch, dark_oak, generic, jungle, redwood"+PerksPlugin.getMessageUtil().HIGHLIGHT;
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
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[woodType] -> "+ChatColor.GREEN+"acacia, birch, dark_oak, generic, jungle, redwood");
           return;
        }
        
        if (args.length>0 && args[0].equalsIgnoreCase("acacia")){
        
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.ACACIA);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        
        }
         
        else if (args.length>0 && args[0].equalsIgnoreCase("birch")){
        
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.BIRCH);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        
        }
         
        else if (args.length>0 && args[0].equalsIgnoreCase("dark_oak")){
        
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.DARK_OAK);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        
        }
         
        else if (args.length>0 && args[0].equalsIgnoreCase("generic")){
        
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.GENERIC);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        
        }
         
        else if (args.length>0 && args[0].equalsIgnoreCase("jungle")){
        
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.JUNGLE);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        
        }
         
        else if (args.length>0 && args[0].equalsIgnoreCase("redwood")){
        
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.REDWOOD);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        
        } else {
          
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.GENERIC);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
        }
        
        
        
        
        }

}
