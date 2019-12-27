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
import com.mcmiddleearth.perks.perks.ParrotPerk;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Parrot;

/**
 *
 * @author Julius_the_Great
 */
public class ParrotHandler extends PerksCommandHandler { 

    public ParrotHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }

    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+ "Summons a parrot to perch on your shoulder.";
    }

    @Override
    public String getUsageDescription(String cmd) {
        return "[color] [pattern]: Summons a parrot to perch on your shoulder. Without arguments it will have a random color and perch. "
        +"Possible colors are: "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
        +"blue, cyan, gray, green, red"+PerksPlugin.getMessageUtil().HIGHLIGHT
        +"The parrot can perch on your "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
        +"left"+PerksPlugin.getMessageUtil().HIGHLIGHT+" or "+PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED+"right"+
        PerksPlugin.getMessageUtil().HIGHLIGHT+" shoulder.";
   }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player p = (Player)cs;
        if (p.getShoulderEntityRight() != null || p.getShoulderEntityRight() != null) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You already have a parrot on your shoulder!");
            return;
        }
        
        
        if(args.length>0 && args[0].equalsIgnoreCase("info")) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Summon a parrot: /perk parrot [color] [shoulder]");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[color] -> "+ChatColor.GREEN+"blue, cyan, gray, green, red");
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "[shoulder] -> "+ChatColor.GREEN+"left, right");
        }
        
        Parrot.Variant color = null;
        int leftShoulder = 0;
        for(String arg: args) {
            boolean found = false;
            if(color==null) {
                for(Parrot.Variant search:Parrot.Variant.values()) {
                    if(search.name().startsWith(arg.toUpperCase())) {
                        color = search;
                        found = true;
                        break;
                    }
                }
            }
            
        if(p.getShoulderEntityLeft()==null && p.getShoulderEntityRight() == null) {
            if(arg.equalsIgnoreCase("left")) 
                leftShoulder = 1;
            else if(arg.equalsIgnoreCase("right")) 
                leftShoulder = 2;
            
        }
           /* if(found) {
                continue;
            }

            if(style!=null && color !=null) {
                break;
            }*/
        }
        
        int numberc = NumericUtil.getRandom(0, Parrot.Variant.values().length-1);
                
//Logger.getGlobal().info("color "+Horse.Style.values().length);
//Logger.getGlobal().info("Randomc "+numberc);
        if(color==null) {
            color = Parrot.Variant.values()[numberc];
        }
        
        if(leftShoulder == 0) {
            int rand = NumericUtil.getRandom(0, 1);
            if (rand == 0)
                leftShoulder = 1;
            else if (rand == 1)
                leftShoulder = 2;
            
        }
        
        Location l = p.getLocation();
        World w = p.getWorld();

        ParrotPerk.allowSpawn(true);
        Parrot charlie = w.spawn(l, Parrot.class);
        charlie.setAdult();
        charlie.setVariant(color);
        charlie.setTamed(true);
        charlie.setOwner(p);
        charlie.setCustomName(ChatColor.DARK_AQUA + p.getName()
                        + ParrotPerk.parrot_perk_custom_name);

        if (leftShoulder == 1)
            p.setShoulderEntityLeft(charlie);
        else if (leftShoulder == 2)
            p.setShoulderEntityRight(charlie);
        ParrotPerk.allowSpawn(false);
        PerksPlugin.getMessageUtil().sendInfoMessage(p, "Enjoy your chatty, colorful friend!");
           
    }
    
}
