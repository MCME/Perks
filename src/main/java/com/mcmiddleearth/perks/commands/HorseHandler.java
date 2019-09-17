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
import com.mcmiddleearth.perks.perks.HorsePerk;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class HorseHandler extends PerksCommandHandler {

    public HorseHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Gives you a horse to ride on.";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[color] [pattern]: Gives you a horse to ride on. Without arguments it will have a random color and pattern. "
                +"Possible colors are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"white, black, brown, chestnut, creamy, dark_brown, gray"+PerksPlugin.getMessageUtil().HIGHLIGHT
                +"Possible patterns are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                +"none, black_dots, white, white_dots, whitefield ";
    }
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player player = (Player)cs;
        if (player.isInsideVehicle()) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You are already mounted on a horse or another entity!");
            return;
        }
        if(args.length>0 && args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Get a horse: /perk horse [color] [style]");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[color] -> "+ChatColor.GREEN+"white, black, brown, chestnut, creamy,");
            PerksPlugin.getMessageUtil().sendIndentedInfoMessage(cs, ChatColor.GREEN+"                 dark_brown, gray");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[style] -> "+ChatColor.GREEN+"none, black_dots, white, white_dots, whitefield");
        }
        if (player.getShoulderEntityLeft().isEmpty() == true && player.getShoulderEntityRight().isEmpty() == true ){
        Horse.Color color = null;
        Horse.Style style = null;
        for(String arg: args) {
            boolean found = false;
            if(color==null) {
                for(Horse.Color search:Horse.Color.values()) {
                    if(search.name().startsWith(arg.toUpperCase())) {
                        color = search;
                        found = true;
                        break;
                    }
                }
            }
            if(found) {
                continue;
            }
            if(style==null) {
                for(Horse.Style search:Horse.Style.values()) {
                    if(search.name().startsWith(arg.toUpperCase())) {
                        style = search;
                        break;
                    }
                }
            }
            if(style!=null && color !=null) {
                break;
            }
        }
        int numberc = NumericUtil.getRandom(0, Horse.Color.values().length-1);
//Logger.getGlobal().info("color "+Horse.Style.values().length);
//Logger.getGlobal().info("Randomc "+numberc);
        if(color==null) {
            color = Horse.Color.values()[numberc];
        }
        int number = NumericUtil.getRandom(0, Horse.Style.values().length-1);
//Logger.getGlobal().info("Syles "+Horse.Style.values().length);
//Logger.getGlobal().info("Randoms "+number);
        if(style==null) {
            style = Horse.Style.values()[number];
        }
        Location l = player.getLocation();
        World w = player.getWorld();

        BoatPerk.allowSpawn(true);
        Horse horsey = w.spawn(l, Horse.class);
        horsey.setAdult();
        horsey.setColor(color);
        horsey.setStyle(style);
        horsey.setTamed(true);
        horsey.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horsey.setOwner(player);
        horsey.setCustomName(ChatColor.DARK_AQUA + player.getName()
                        + HorsePerk.horse_perk_custom_Name);
        horsey.addPassenger(player);
        HorsePerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Enjoy your ride!");
        }else {PerksPlugin.getMessageUtil().sendInfoMessage(player, "You can't spawn an horse when you have a parrot on your shoulder!");}
        }
    
}
