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
 * @author Fraspace5, Eriol_Eandur
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
        return "cat|dog|dismiss [name] [collar color] [type]: Summons a pet. "
                +"Possible collar colors are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"black, blue, brown, cyan, gray, green, light_blue, light_gray, lime, magenta, orange, pink, purple, red, white and yellow"+PerksPlugin.getMessageUtil().HIGHLIGHT
                +"So far types are implemented for cats only: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"all_black, black, british_shorthair, calico, jellie, persian, ragdoll, red, siamese, tabby and white";
    }
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        
        Player player = (Player)cs;
        
             
        if(args.length<1 || args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Summon a pet: /perk pet cat|dog|dismiss [name] [collar color] [type] ");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[collar color] -> "+ChatColor.GREEN+"black, blue, brown, cyan, gray, green, light_blue, light_gray, lime, magenta, orange, pink, purple, red, white, yellow,");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[type] -> "+ChatColor.GREEN+"all_black, black, british_shorthair, calico, jellie, persian, ragdoll, red, siamese, tabby, white");
         
            return;
        }
        if (player.isFlying()) {
           
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "Companions are not supported while flying!");
            return;
        } 
        Location location = player.getLocation();
        World world = player.getWorld();
        CompanionPerk.allowSpawn(true);
        String name = player.getName()+"'s pet";
        if(args.length>1) {
            name = args[1];
        }
        DyeColor collarColor = DyeColor.values()[NumericUtil.getRandom(0,DyeColor.values().length-1)];
        if(args.length>2) {
            for(DyeColor color: DyeColor.values()) {
                if(args[2].toUpperCase().equals(color.name())) {
                    collarColor = color;
                    break;
                }
            }
        }
        Cat.Type catType = Cat.Type.values()[NumericUtil.getRandom(0,Cat.Type.values().length-1)];
        if(args.length>3) {
            for(Cat.Type type: Cat.Type.values()) {
                if(args[3].toUpperCase().equals(type.name())) {
                    catType = type;
                    break;
                }
            }
        }
        if(args[0].equalsIgnoreCase("cat")) {
            CompanionPerk.spawnCat(player, name, collarColor, catType);
            PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your cat!");
        } else if(args[0].equalsIgnoreCase("dog")) {
            CompanionPerk.spawnDog(player, name, collarColor);
            PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your dog!");
        } else if(args[0].equalsIgnoreCase("dismiss")) {
            if(args.length<2) {
                name = null;
            }
            CompanionPerk.dismissCompanions(player, name);
            PerksPlugin.getMessageUtil().sendInfoMessage(player, "You dismissed your pet!");
        } else {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "Valid subcommands: dog | cat | dismiss");
        }
        CompanionPerk.allowSpawn(false);
    }
    
}

  
    
    
    
       

    
        

