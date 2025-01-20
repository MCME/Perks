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

package com.mcmiddleearth.perks.listeners;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.SitPerk;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.pluginutil.EventUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.ArmorStand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.EntityDismountEvent;

import java.util.logging.Logger;

/**
 * 
 * @author Eriol_Eandur
 */

public class SitListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void playerSitDown(PlayerInteractEvent event) {
        if(!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && EventUtil.isMainHandEvent(event)
                && event.getPlayer().getInventory().getItemInMainHand().getType().equals(SitPerk.getItem()))) {
            return;
        }
        event.setCancelled(true);
        if(!PerkManager.isEnabled("sit")) {
            PerksPlugin.getMessageUtil().sendErrorMessage(event.getPlayer(), "Sitting on blocks perk is currently disabled.");
            return;
        }
        if(PermissionData.isAllowed(event.getPlayer(), PerkManager.forName("sit"))) {
            SitPerk.sitDown(event.getPlayer(), event.getClickedBlock());
            PerksPlugin.getMessageUtil().sendInfoMessage(event.getPlayer(), "Enjoy your break!");
        } else {
            PerksPlugin.getMessageUtil().sendErrorMessage(event.getPlayer(), "Sitting on blocks perk is not given to you!");
        }
    }
    
    @EventHandler
    public void playerDie(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            SitPerk.sitUp((Player)event.getEntity());
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        SitPerk.sitUp(event.getPlayer());
    }

    @EventHandler
    public void playerKick(PlayerKickEvent event) {
        SitPerk.sitUp(event.getPlayer());
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        SitPerk.sitUp(event.getPlayer());
    }

    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent event) {
        SitPerk.sitUp(event.getPlayer());
    }

    @EventHandler
    public void playerChangeGamemode(PlayerGameModeChangeEvent event) {
        if(event.getNewGameMode().equals(GameMode.SPECTATOR)) {
            SitPerk.sitUp(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void playerSitUp(EntityDismountEvent event) {
        if(event.getEntity() instanceof Player
                && event.getDismounted() instanceof ArmorStand) {
            SitPerk.sitUp((Player) event.getEntity());
        }
    }
}
