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

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.CompassPerk;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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
        if  (cs instanceof Player ){
            Player player = (Player) cs;
            World w = player.getWorld();
            Double y = player.getLocation().getY();
            if(!((Player)cs).getInventory().contains(Material.COMPASS)) {
                ItemStack item = new ItemStack(Material.COMPASS);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("Compass");
                List<String> lore = new ArrayList<String>();
                lore.add("You will never get lost again...");
                meta.setLore(lore);
                item.setItemMeta(meta);
                player.getInventory().addItem(item);
                PerksPlugin.getMessageUtil().sendInfoMessage(player,"You will never get lost again!");
            }
            CompassPerk perk = ((CompassPerk)PerkManager.forName("compass"));
            if(args.length>1 && NumericUtil.isInt(args[0]) && NumericUtil.isInt(args[1])) {
                perk.unsetCompassDirection(player);
                player.setCompassTarget(new Location(player.getWorld(),NumericUtil.getInt(args[0]),player.getLocation().getBlockY(),NumericUtil.getInt(args[1])));
                PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading to "+args[0]+", "+args[1]+".");
                return;
            } else if(args.length>0) {
                String input = args[0].toLowerCase();
                switch (input){
                    case "north":
                        perk.setCompassDirection(player, 0);
                        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading north.");
                        return;
                    case "south":
                        perk.setCompassDirection(player, 180);
                        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading south.");
                        return;
                    case "west":
                        perk.setCompassDirection(player, 270);
                        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading west.");
                        return;
                    case "east":
                        perk.setCompassDirection(player, 90);
                        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading east.");
                        return;
                    case "here":
                        perk.unsetCompassDirection(player);
                        player.setCompassTarget(player.getLocation());
                        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading here.");
                        return;
                    default:
                        try {
                            float direction = Float.parseFloat(input);
                            perk.setCompassDirection(player, direction);
                            PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading "+input+"°.");
                        } catch(NumberFormatException ex) {
                            PerksPlugin.getMessageUtil().sendErrorMessage(player, "Unknown heading.");
                        }
                        return;
                }
            }
            perk.unsetCompassDirection(player);
            player.setCompassTarget(player.getWorld().getSpawnLocation());
            PerksPlugin.getMessageUtil().sendInfoMessage(player,"Heading home.");
        } else {
            cs.sendMessage("This command can only be used by players.");
        }
        
    }
    
    
    
    
}
