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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;

/**
 *
 * @author Fraspace5
 */
public class ParrotHandler extends PerksCommandHandler {

    public ParrotHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Put a parrot on your shoulder";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[color] [pattern]: Put a parrot on your shoulder. Without arguments it will have a random color and default position(left). "
                +"Possible colors are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"blue, cyan, gray, green, red "+PerksPlugin.getMessageUtil().HIGHLIGHT
                +"Possible patterns are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED;
    }
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        
        Player player = (Player)cs;
        
             
        if(args.length>0 && args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Get a parrot: /perk parrot [remove] [color] [position] ");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[color] -> "+ChatColor.GREEN+"blue, cyan, gray, green, red,");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[position] -> "+ChatColor.GREEN+"left, right");
         
            return;
        }
        if (player.isFlying()) {
           
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "Parrots are not supported while flying!");
            return;
        } 
        Variant variant = Variant.values()[NumericUtil.getRandom(0, Variant.values().length-1)];
        boolean leftShoulder = NumericUtil.getRandom(0, 1)>0;
        boolean removal = false;
        boolean shoulderRandom = true;
        for(String argument: args) {
            try{
                variant = Variant.valueOf(argument.toUpperCase());
                continue;
            } catch(IllegalArgumentException ignored) {}
            switch(argument) {
                case "left":
                    leftShoulder = true; 
                    shoulderRandom = false; break;
                case "right":
                    leftShoulder = false; 
                    shoulderRandom = false; break;
                case "remove":
                    removal = true;
            }
        }
        if(removal) {
            Entity parrot;
            if(leftShoulder) {
                parrot = player.getShoulderEntityLeft();
            } else {
                parrot = player.getShoulderEntityRight();
            }
            if(parrot == null && shoulderRandom) {
                leftShoulder = !leftShoulder;
                if(leftShoulder) {
                    parrot = player.getShoulderEntityLeft();
                } else {
                    parrot = player.getShoulderEntityRight();
                }
            }
            if(parrot != null) {
                if(leftShoulder) {
                    player.setShoulderEntityLeft(null);
                } else {
                    player.setShoulderEntityRight(null);
                }
                PerksPlugin.getMessageUtil().sendInfoMessage(player, "Bye, bye parrot!");
            } else {
                PerksPlugin.getMessageUtil().sendErrorMessage(player, "You don't have a parrot!");
            }
        } else {
            boolean alreadyThere;
            if(leftShoulder) {
                alreadyThere = player.getShoulderEntityLeft() != null;
            } else {
                alreadyThere = player.getShoulderEntityRight() != null;
            }
            if(alreadyThere && shoulderRandom) {
                leftShoulder = !leftShoulder;
                if(leftShoulder) {
                    alreadyThere = player.getShoulderEntityLeft() != null;
                } else {
                    alreadyThere = player.getShoulderEntityRight() != null;
                }
            }
            if(alreadyThere) {
                PerksPlugin.getMessageUtil().sendErrorMessage(player, "You already have a parrot here!");
            } else {
                Location location = player.getLocation();
                World world = player.getWorld();
                ParrotPerk.allowSpawn(true);
                Parrot parrot = world.spawn(location, Parrot.class);
                parrot.setAdult();
                parrot.setVariant(variant);
                parrot.setTamed(true);
                parrot.setOwner(player);
                parrot.setCustomName(ChatColor.DARK_AQUA + player.getName()
                                + ParrotPerk.parrot_perk_custom_Name);
                if(leftShoulder) {
                    player.setShoulderEntityLeft(parrot);
                } else {
                    player.setShoulderEntityRight(parrot);
                }
                ParrotPerk.allowSpawn(false);
                PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your parrot!");
            }
        }
    }
    
}

