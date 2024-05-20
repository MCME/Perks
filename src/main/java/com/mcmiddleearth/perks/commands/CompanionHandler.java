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
import com.mcmiddleearth.perks.perks.CompanionPerk;
import com.mcmiddleearth.perks.perks.ParrotPerk;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.entity.Parrot.Variant;

/**
 *
 * @author Fraspace5
 */
public class CompanionHandler extends PerksCommandHandler {

    public CompanionHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Spawns a tame animal.";
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
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Get a companion: /perk companion [remove] [color] [position] ");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[color] -> "+ChatColor.GREEN+"blue, cyan, gray, green, red,");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[position] -> "+ChatColor.GREEN+"left, right");
         
            return;
        }
        if (player.isFlying()) {
           
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "Parrots are not supported while flying!");
            return;
        } 
        Location location = player.getLocation();
        World world = player.getWorld();
        CompanionPerk.allowSpawn(true);
        /*Animals companion = switch (args[0]) {
            case "wolf" -> world.spawn(location, Wolf.class);
            case "cat" -> world.spawn(location, Cat.class);
            case "fox" -> world.spawn(location, Fox.class);
            case "ocelot" -> world.spawn(location, Ocelot.class);
            case "parrot" -> world.spawn(location, Parrot.class);
            default -> null;
                };
        companion.setAdult();
        //companion.setVariant(variant);
        if(companion instanceof Ocelot ocelot) {
            ocelot.setTrusting(true);
        } else if(companion instanceof Fox fox) {
            fox.setFirstTrustedPlayer(player);
        } else {
            Tameable tameable =  (Tameable) companion;
            tameable.setTamed(true);
            tameable.setOwner(player);
            if(args.length>1) {
                if(tameable instanceof Wolf wolf) {
                    wolf.setCollarColor(DyeColor.valueOf(args[1].toUpperCase()));
                    wolf.setInterested(true);
                } else if(tameable instanceof Cat cat) {
                    cat.setCollarColor(DyeColor.valueOf(args[1].toUpperCase()));
                    cat.setHeadUp(true);
                };
            }
            if(args.length>2) {
                if(tameable instanceof Cat cat) {
                    cat.setCatType(Cat.Type.valueOf(args[2].toUpperCase()));
                    cat.setLyingDown(true);
                }
            }
        }
        companion.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + CompanionPerk.companion_perk_custom_Name);
        CompanionPerk.allowSpawn(false);*/
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your companion!");
    }
    
}

  
    
    
    
       

    
        

