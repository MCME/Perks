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
import com.mcmiddleearth.perks.perks.Perk;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Fraspace5
 */
public class CompassHandler extends PerksCommandHandler {
    
      
    public CompassHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Set your compass target";
                
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "Set your compass target with /perk compass [north,south,west,east,reset] "
                +"To restore write /perk compass reset";
    }
  
    



    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        
        if (args.length>0 && args[0].equalsIgnoreCase("info")){
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Set your compass target! Without argument the default direction is North!");
        return;
        }
ItemStack item = new ItemStack(Material.COMPASS);
ItemMeta meta = item.getItemMeta();
meta.setDisplayName("Compass");
List<String> lore = new ArrayList<String>();
lore.add("You will never get lost again...");
meta.setLore(lore);
item.setItemMeta(meta);
        Player pl = (Player) cs;
       World w = pl.getWorld();
       Double y = pl.getLocation().getY();
       pl.getInventory().addItem(item);
        if  (cs instanceof Player ){
        PerksPlugin.getMessageUtil().sendInfoMessage(pl,"You will never get lost again!");
            String input = args[0].toLowerCase();
            switch (input){
                case "reset":
                    
                  pl.setCompassTarget(pl.getBedSpawnLocation());
                    
                    
                    
                    break;
                case "north":
              Double x = pl.getLocation().getX() ;
              
              Double z = -40000.00 ;
              Location loc = new Location(w,x,y,z);
                    
              pl.setCompassTarget(loc);
                    
                    
                    break;
                case "south":
                    Double x1 = pl.getLocation().getX() ;
              
              Double z1 = 40000.00 ;
              Location loc1 = new Location(w,x1,y,z1);
                    
              pl.setCompassTarget(loc1);
                    
                    break;
                case "west":
              Double x2 = - 40000.00;
              
              Double z2 = pl.getLocation().getZ() ;
              Location loc2 = new Location(w,x2,y,z2);
                    
              pl.setCompassTarget(loc2);
                    
                    
                    break;
                case "east":
              Double x3 = 40000.00 ;
              
              Double z3 = pl.getLocation().getZ();
              Location loc3 = new Location(w,x3,y,z3);
                    
              pl.setCompassTarget(loc3);
                    
                    
                    break;
                
                default:
              Double x4 = pl.getLocation().getX() ;
              
              Double z4 = -40000.00 ;
              Location loc4 = new Location(w,x4,y,z4);
                    
              pl.setCompassTarget(loc4);
                    
                    break;
                    
            }
            
        }
            
        else 
        { System.out.println("You can't use this command in the console of the server");
        
        }
        
    }
    
    
    
    
}
