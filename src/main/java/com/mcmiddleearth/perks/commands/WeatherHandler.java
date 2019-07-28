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
import com.mcmiddleearth.perks.perks.Perk;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author farspace5
 */
public class WeatherHandler extends PerksCommandHandler {
    
     
    public WeatherHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Change your Minecraft Weather";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "Set your weather with /perk weather [weathertype] | [info] "
                +"[WeatherType] "+"Clean | Downfall";
    }
   
    
    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        
        if (args.length>0 && args[0].equalsIgnoreCase("info")){
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Set your weather [Clean|Downfall] ");
        return;
        }
        
        if  (cs instanceof Player ){
            if (args.length>0 && args[0].equalsIgnoreCase("clean")){

        Player player = (Player) cs;
        String name = player.getName();
        player.setPlayerWeather(WeatherType.CLEAR);
        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Clean sky!");

        } else if (args.length>0 && args[0].equalsIgnoreCase("downfall")){
        
             Player player = (Player) cs;
             player.setPlayerWeather(WeatherType.DOWNFALL);
             PerksPlugin.getMessageUtil().sendInfoMessage(player,"Oh no!It's raining!");
        } else {
        
           Player player = (Player) cs;  
            PerksPlugin.getMessageUtil().sendInfoMessage(player,"Invalid Usage! /info");}}
        else 
        { System.out.println("You can't use this command in the console of the server");
        
        }
        
    }
    
    
    
    
}
