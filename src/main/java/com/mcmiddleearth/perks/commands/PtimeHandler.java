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
import com.mcmiddleearth.perks.perks.PtimePerk;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Fraspace5
 */
public class PtimeHandler extends PerksCommandHandler {
    
     
    public PtimeHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"RealTime in Minecraft [RealTime,reset]";
                
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[RealTime,reset]: Set your time to the realtime of the server ";
    }
   
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        
        if (args.length>0 && args[0].equalsIgnoreCase("info")){
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Set your client time as the server time! ");
        return;
        }
        
        if  (cs instanceof Player ){
            if (args.length>0 && args[0].equalsIgnoreCase("realtime")){
        Server server = Bukkit.getServer();
        Long time = System.currentTimeMillis();
        Player player = (Player) cs;
        String name = player.getName();
        World w = player.getWorld();
        PerksPlugin.getMessageUtil().sendInfoMessage(player,"RealTime");
        
        player.setPlayerTime(time, false);
        } else if (args.length>0 && args[0].equalsIgnoreCase("reset")){
        
             Player player = (Player) cs;
             player.resetPlayerTime();
             PerksPlugin.getMessageUtil().sendInfoMessage(player,"Reset Done!");
        } else {
        
           Player player = (Player) cs;  
            PerksPlugin.getMessageUtil().sendInfoMessage(player,"Invalid Usage! /info");}}
        else 
        { System.out.println("You can't use this command in the console of the server");
        
        }
        
    }
    
    
    
    
    
}
