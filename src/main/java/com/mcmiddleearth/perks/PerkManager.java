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
package com.mcmiddleearth.perks;

import com.mcmiddleearth.perks.perks.Perk;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Eriol_Eandur
 */
public class PerkManager {
    
    private static final Set<Perk> perks = new HashSet<>();
    
    public static void addPerk(Perk perk) {
        perks.add(perk);
        PerksPlugin plugin = PerksPlugin.getInstance();
        PluginManager pluginManager = PerksPlugin.getInstance().getServer().getPluginManager();
        if(perk.getListener()!=null) {
            pluginManager.registerEvents(perk.getListener(),plugin);
        }
        if(perk.getHandler()!=null) {
            for(String command: perk.getCommands()) {
                plugin.getPerksExecutor().addCommandHandler(command, perk.getHandler());
            }
        }
    }
    
    public static Perk forName(String name) {
        for(Perk perk: perks) {
            if(perk.getName().equalsIgnoreCase(name)) {
                return perk;
            }
        }
        return null;
    }
    
    public static boolean isEnabled(String name) {
        Perk perk = forName(name);
        return perk!=null && PerksPlugin.getInstance().isPerkEnabled(perk);
    }

    public static Set<Perk> getPerks() {
        return Collections.unmodifiableSet(perks);
    }
}
