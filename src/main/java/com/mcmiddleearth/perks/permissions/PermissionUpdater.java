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
package com.mcmiddleearth.perks.permissions;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.supporter.DonorDataInputHandler;
import com.mcmiddleearth.perks.supporter.PatreonClient;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class PermissionUpdater extends BukkitRunnable{
    
    private ConfigurationSection update = PerksPlugin.getInstance().getConfig().getConfigurationSection("update");
    private int sourcesFinished;

    @Override
    public void run() {
        sourcesFinished = 0;
        PermissionData.clearCredits();
        new DonorDataInputHandler(update.getString("donor"),2000).start(this);
        PatreonClient.updateCredits(this);
    }

    public synchronized void updatePermissions() {
Logger.getGlobal().info("updatePermissions "+sourcesFinished);
        sourcesFinished++;
        if(sourcesFinished<2) {
            return;
        }
        PermissionData.saveCreditData();
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player: Bukkit.getOnlinePlayers()) {
                    PermissionData.updatePerkPermissions(player);
                }
                for(Perk perk: PerkManager.getPerks()) {
                    perk.check();
                }
                /*HashMap<UUID, CreditData> creditData = new HashMap<>();
                for(Player player: Bukkit.getOnlinePlayers()) {
                    CreditData data = PermissionData.updatePerkPermissions(player);
                    if(data!=null) {
                        creditData.put(player.getUniqueId(), data);
                    }
                }
                PermissionData.setCredits(creditData);
                for(Perk perk: PerkManager.getPerks()) {
                    perk.check();
                }*/
            }
        }.runTask(PerksPlugin.getInstance());
    }
}
