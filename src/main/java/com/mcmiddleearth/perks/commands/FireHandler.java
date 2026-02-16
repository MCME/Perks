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
import com.mcmiddleearth.perks.perks.SetOnFirePerk;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Eriol_Eandur
 */
public class FireHandler extends PerksCommandHandler {

    public FireHandler(Perk perk, String... permissionNodes) {
        super(0,true,perk,permissionNodes);
    }
    
    @Override
    public String getShortDescription(String cmd) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Set yourself on fire.";
    }
    
    @Override
    public String getUsageDescription(String cmd) {
        return "[duration]: Set yourself on fire for [duration] seconds. Without argumend you will be on "
                +"fire for "+((SetOnFirePerk)this.getPerk()).getDefaultTicks()+" seconds.";
    }
    @Override
    
    protected void execute(CommandSender cs, String cmd, String... args) {
        int ticks = ((SetOnFirePerk)this.getPerk()).getDefaultTicks();
        if(args.length>0 && NumericUtil.isInt(args[0])) {
            ticks = NumericUtil.getInt(args[0])*20;
            if(ticks>((SetOnFirePerk)this.getPerk()).getMaxTicks()) {
                ticks = ((SetOnFirePerk)this.getPerk()).getMaxTicks();
            }
        }
        Player player = (Player) cs;
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,ticks,5));
        player.setFireTicks(ticks);
        PerksPlugin.getMessageUtil().sendInfoMessage(player,"Is your name Denethor?");
    }
    
}
