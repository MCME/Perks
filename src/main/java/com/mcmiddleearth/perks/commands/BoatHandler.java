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
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


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
        final Player player = (Player)cs;
        if (player.isInsideVehicle()) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You are already in a boat!");
            return;
        }
        if(args.length>0 && args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Get a boat: /perk boat [woodType] ");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[woodType] -> "+ChatColor.GREEN+"acacia, birch, dark_oak, generic, jungle, redwood");
           return;
        }
        if (player.getShoulderEntityLeft().isEmpty() == true && player.getShoulderEntityRight().isEmpty()== true){
             
          
          
             Double x = player.getLocation().getX();
             Double y = player.getLocation().getY()+1; 
             Double z = player.getLocation().getZ();
             World w = player.getWorld();


             
             Location block1 = new Location(w,x,y,z);
             
             Block block = w.getBlockAt(block1);
             Block north = block.getRelative(BlockFace.NORTH);
             Block south = block.getRelative(BlockFace.SOUTH);
             Block east = block.getRelative(BlockFace.EAST);
             Block west = block.getRelative(BlockFace.WEST);
             Block northe = block.getRelative(BlockFace.NORTH_EAST);
             Block northw = block.getRelative(BlockFace.NORTH_WEST);
             Block southe = block.getRelative(BlockFace.SOUTH_EAST);
             Block southw = block.getRelative(BlockFace.SOUTH_WEST);
             Block north2 = north.getRelative(BlockFace.NORTH);
             Block northe2 = northe.getRelative(BlockFace.NORTH);
             Block northe3 = northe.getRelative(BlockFace.EAST);
             Block northw2 = northw.getRelative(BlockFace.NORTH);
             Block northw3 = northw.getRelative(BlockFace.WEST);
             Block east2 = east.getRelative(BlockFace.EAST);
             Block west2 = west.getRelative(BlockFace.WEST);
             Block south2 = south.getRelative(BlockFace.SOUTH);
             Block southw2 = southw.getRelative(BlockFace.SOUTH);
             Block southw3 = southw.getRelative(BlockFace.WEST);
             Block southe2 = southe.getRelative(BlockFace.SOUTH);
             Block southe3 = southe.getRelative(BlockFace.EAST);
             
             
             
             if (block.getType().equals(Material.WATER_LILY) == false && north.getType().equals(Material.WATER_LILY) == false && 
                     south.getType().equals(Material.WATER_LILY) == false && 
                     east.getType().equals(Material.WATER_LILY) == false &&
                     west.getType().equals(Material.WATER_LILY) == false &&
                     northw.getType().equals(Material.WATER_LILY) == false &&
                     northe.getType().equals(Material.WATER_LILY) == false &&
                     southe.getType().equals(Material.WATER_LILY) == false &&
                     southw.getType().equals(Material.WATER_LILY) == false &&
                     north2.getType().equals(Material.WATER_LILY) == false &&
                     northe2.getType().equals(Material.WATER_LILY) == false &&
                     northe3.getType().equals(Material.WATER_LILY) == false &&
                     northw2.getType().equals(Material.WATER_LILY) == false &&
                     northw3.getType().equals(Material.WATER_LILY) == false &&
                     east2.getType().equals(Material.WATER_LILY) == false &&
                     west2.getType().equals(Material.WATER_LILY) == false &&
                     south2.getType().equals(Material.WATER_LILY) == false &&
                     southw2.getType().equals(Material.WATER_LILY) == false &&
                     southw3.getType().equals(Material.WATER_LILY) == false &&
                     southe2.getType().equals(Material.WATER_LILY) == false &&
                     southe3.getType().equals(Material.WATER_LILY) == false ){
             }else {
             PerksPlugin.getMessageUtil().sendInfoMessage(player, "Get away from the water lilies, you can't destroy them");
             return;
             }
             
            
            if (args.length>0 && args[0].equalsIgnoreCase("acacia")){
        
        Location l = player.getLocation();

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
        

        BoatPerk.allowSpawn(true);
        Boat horsey = w.spawn(l, Boat.class);
        horsey.setWoodType(TreeSpecies.GENERIC);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + BoatPerk.boat_perk_custom_Name);
        horsey.addPassenger(player);
        BoatPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your boat!");
       
          
        }
            new BukkitRunnable(){
              
          @Override 
          public void run(){
             if (player.isInsideVehicle() == false){
             cancel();
             }
              
              
             Double x = player.getVehicle().getLocation().getX();
             Double y = player.getVehicle().getLocation().getY()+1; 
             Double z = player.getVehicle().getLocation().getZ(); 
             Double x1 = player.getVehicle().getLocation().getX()+2;
             Double x2 = player.getVehicle().getLocation().getX()-2;
             Double z1 = player.getVehicle().getLocation().getZ()+2;
             Double z2 = player.getVehicle().getLocation().getZ()-2; 
             Double x3 = player.getVehicle().getLocation().getX()+1;
             Double x4 = player.getVehicle().getLocation().getX()-1;
             Double z3 = player.getVehicle().getLocation().getZ()+1;
             Double z4 = player.getVehicle().getLocation().getZ()-1;
             World w = player.getWorld();
             Location loc = new Location(w,x1,y,z);
             Location loc2 = new Location(w,x2,y,z);
             Location loc3 = new Location(w,x,y,z1);
             Location loc4 = new Location(w,x,y,z2);
             Location loc5 = new Location(w,x1,y,z1);
             Location loc6 = new Location(w,x2,y,z2);
             Location loc7 = new Location(w,x1,y,z2);
             Location loc8 = new Location(w,x2,y,z1);
              Location loc9 = new Location(w,x3,y,z);
             Location loc10 = new Location(w,x4,y,z);
             Location loc11 = new Location(w,x,y,z3);
             Location loc12 = new Location(w,x,y,z4);
             Location loc13 = new Location(w,x3,y,z3);
             Location loc14 = new Location(w,x4,y,z4);
             Location loc15 = new Location(w,x3,y,z4);
             Location loc16 = new Location(w,x4,y,z3);
             if (loc.getBlock().getType().equals(Material.WATER_LILY) == false && 
                     loc2.getBlock().getType().equals(Material.WATER_LILY) == false
                    && loc3.getBlock().getType().equals(Material.WATER_LILY) == false
                    && loc4.getBlock().getType().equals(Material.WATER_LILY) == false
                           &&  loc5.getBlock().getType().equals(Material.WATER_LILY) == false
                                   &&  loc6.getBlock().getType().equals(Material.WATER_LILY) == false
                                          &&   loc7.getBlock().getType().equals(Material.WATER_LILY) == false
               &&  loc8.getBlock().getType().equals(Material.WATER_LILY) == false && loc9.getBlock().getType().equals(Material.WATER_LILY) == false && 
                     loc10.getBlock().getType().equals(Material.WATER_LILY) == false
                    && loc11.getBlock().getType().equals(Material.WATER_LILY) == false
                    && loc12.getBlock().getType().equals(Material.WATER_LILY) == false
                           &&  loc13.getBlock().getType().equals(Material.WATER_LILY) == false
                                   &&  loc14.getBlock().getType().equals(Material.WATER_LILY) == false
                                          &&   loc15.getBlock().getType().equals(Material.WATER_LILY) == false
               &&  loc16.getBlock().getType().equals(Material.WATER_LILY) == false){
             
             
             } else {
             PerksPlugin.getMessageUtil().sendInfoMessage(player, "Oh no! Don't destroy water lilies...");
             player.getVehicle().remove();
             cancel();
             
             
             
             }
             
              
          
          }
          
          }.runTaskTimer(PerksPlugin.getInstance(),1L,1L);
             
        }
        else {PerksPlugin.getMessageUtil().sendInfoMessage(player, "You can't spawn a boat when you have a parrot on your shoulder");}
        
        
        }

}
