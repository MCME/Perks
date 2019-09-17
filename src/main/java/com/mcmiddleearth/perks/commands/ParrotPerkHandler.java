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
package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.ParrotPerk;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Parrot.Variant;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Parrot;

/**
 *
 * @author Fraspace5
 */
public class ParrotPerkHandler extends PerksCommandHandler {

    public ParrotPerkHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Put a parrot on your shoulder";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[color] [pattern]: Put a parrot on your shoulder. Without arguments it will have a random color and default position(left)"
                +"Possible colors are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"blue, cyan, gray, green, red "+PerksPlugin.getMessageUtil().HIGHLIGHT;
                
    }
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        
        Player player = (Player)cs;
        
             
        if (player.getPassengers().isEmpty() == false) {
           
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You already have a parrot!");
            return;
        } 
        if(args.length>0 && args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Get a parrot: /perk parrot [color] [position] ");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[color] -> "+ChatColor.GREEN+"blue, cyan, gray, green, red,");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[position] -> "+ChatColor.GREEN+"left, right");
         
            return;
        }
    
         if (player.isInsideVehicle() == false){
        
        if (args.length>0 && args[0].equalsIgnoreCase("red")){
          
            switch(args[1]) {
            
                case "left":
        Location l = player.getLocation();
        World w = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horsey = w.spawn(l, Parrot.class);
        horsey.setAdult();
        horsey.setVariant(Variant.RED);
        horsey.setTamed(true);
        horsey.setOwner(player);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horsey);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                case "right":
        Location la = player.getLocation();
        World wa = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseya = wa.spawn(la, Parrot.class);
        horseya.setAdult();
        horseya.setVariant(Variant.RED);
        horseya.setTamed(true);
        horseya.setOwner(player);
        horseya.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityRight(horseya);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                    
                default:
        
        Location ls = player.getLocation();
        World ws = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseys = ws.spawn(ls, Parrot.class);
        horseys.setAdult();
        horseys.setVariant(Variant.RED);
        horseys.setTamed(true);
        horseys.setOwner(player);
        horseys.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horseys);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    
                    return;
                    
        
            
            }
          return;
        }
        if (args.length>0 && args[0].equalsIgnoreCase("blue")){
          
            
            switch(args[1]) {
            
                case "left":
        Location l = player.getLocation();
        World w = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horsey = w.spawn(l, Parrot.class);
        horsey.setAdult();
        horsey.setVariant(Variant.BLUE);
        horsey.setTamed(true);
        horsey.setOwner(player);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horsey);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                case "right":
        Location la = player.getLocation();
        World wa = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseya = wa.spawn(la, Parrot.class);
        horseya.setAdult();
        horseya.setVariant(Variant.BLUE);
        horseya.setTamed(true);
        horseya.setOwner(player);
        horseya.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityRight(horseya);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                    
                default:
        
        Location ls = player.getLocation();
        World ws = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseys = ws.spawn(ls, Parrot.class);
        horseys.setAdult();
        horseys.setVariant(Variant.BLUE);
        horseys.setTamed(true);
        horseys.setOwner(player);
        horseys.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horseys);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    
                    return;
                    
        
        }return;}
        if (args.length>0 && args[0].equalsIgnoreCase("cyan")){
          

            switch(args[1]) {
            
                case "left":
        Location l = player.getLocation();
        World w = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horsey = w.spawn(l, Parrot.class);
        horsey.setAdult();
        horsey.setVariant(Variant.CYAN);
        horsey.setTamed(true);
        horsey.setOwner(player);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horsey);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                case "right":
        Location la = player.getLocation();
        World wa = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseya = wa.spawn(la, Parrot.class);
        horseya.setAdult();
        horseya.setVariant(Variant.CYAN);
        horseya.setTamed(true);
        horseya.setOwner(player);
        horseya.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityRight(horseya);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                    
                default:
        
        Location ls = player.getLocation();
        World ws = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseys = ws.spawn(ls, Parrot.class);
        horseys.setAdult();
        horseys.setVariant(Variant.CYAN);
        horseys.setTamed(true);
        horseys.setOwner(player);
        horseys.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horseys);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    
                    return;
                    
            
        
        }return;}
        if (args.length>0 && args[0].equalsIgnoreCase("green")){
          
           
            switch(args[1]) {
            
                case "left":
        Location l = player.getLocation();
        World w = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horsey = w.spawn(l, Parrot.class);
        horsey.setAdult();
        horsey.setVariant(Variant.GREEN);
        horsey.setTamed(true);
        horsey.setOwner(player);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horsey);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                case "right":
        Location la = player.getLocation();
        World wa = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseya = wa.spawn(la, Parrot.class);
        horseya.setAdult();
        horseya.setVariant(Variant.GREEN);
        horseya.setTamed(true);
        horseya.setOwner(player);
        horseya.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityRight(horseya);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                    
                default:
        
        Location ls = player.getLocation();
        World ws = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseys = ws.spawn(ls, Parrot.class);
        horseys.setAdult();
        horseys.setVariant(Variant.GREEN);
        horseys.setTamed(true);
        horseys.setOwner(player);
        horseys.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horseys);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    
                    return;
                    
        
        }return;}
        if (args.length>0 && args[0].equalsIgnoreCase("gray")){
          

            switch(args[1]) {
            
                case "left":
        Location l = player.getLocation();
        World w = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horsey = w.spawn(l, Parrot.class);
        horsey.setAdult();
        horsey.setVariant(Variant.GRAY);
        horsey.setTamed(true);
        horsey.setOwner(player);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horsey);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                case "right":
        Location la = player.getLocation();
        World wa = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseya = wa.spawn(la, Parrot.class);
        horseya.setAdult();
        horseya.setVariant(Variant.GRAY);
        horseya.setTamed(true);
        horseya.setOwner(player);
        horseya.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityRight(horseya);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    break;
                    
                default:
        
        Location ls = player.getLocation();
        World ws = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horseys = ws.spawn(ls, Parrot.class);
        horseys.setAdult();
        horseys.setVariant(Variant.GRAY);
        horseys.setTamed(true);
        horseys.setOwner(player);
        horseys.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        player.setShoulderEntityLeft(horseys);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
                    
                    return;
                    
            
        
        }return;
        }
        
        Parrot.Variant color = null;
        int numberc = NumericUtil.getRandom(0, Parrot.Variant.values().length-1);
//Logger.getGlobal().info("color "+Horse.Style.values().length);
//Logger.getGlobal().info("Randomc "+numberc);
        if(color==null) {
            color = Parrot.Variant.values()[numberc];
        }

        Location l = player.getLocation();
        World w = player.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot horsey = w.spawn(l, Parrot.class);
        horsey.setAdult();
        horsey.setVariant(color);
        horsey.setTamed(true);
        horsey.setOwner(player);
        player.setShoulderEntityLeft(horsey);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + ParrotPerk.parrot_perk_custom_Name);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
    }else { PerksPlugin.getMessageUtil().sendInfoMessage(player, "Sorry, you can't spawn a parrot when you are in a vehicle");}
    }
    
}

  
    
    
    
       

    
        

