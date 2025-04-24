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
import com.mcmiddleearth.perks.permissions.Permissions;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Eriol_Eandur
 */
public class PerksCommandExecutor implements CommandExecutor {

    private final Map <String, PerksCommandHandler> commands = new LinkedHashMap <>();
    
    public PerksCommandExecutor() {
        addCommandHandler("enable", new EnableHandler(Permissions.ADMIN.getPermissionNode()));
        addCommandHandler("disable", new EnableHandler(Permissions.ADMIN.getPermissionNode()));
        addCommandHandler("info", new EnableHandler(Permissions.ADMIN.getPermissionNode()));
        addCommandHandler("open", new OpenHandler(Permissions.ADMIN.getPermissionNode()));
        addCommandHandler("close", new OpenHandler(Permissions.ADMIN.getPermissionNode()));
        addCommandHandler("patreon", new PatreonTestHandler(Permissions.ADMIN.getPermissionNode()));
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(strings == null || strings.length == 0) {
            sendCommandList(cs);
            return true;
        }
        if(commands.containsKey(strings[0].toLowerCase())) {
            commands.get(strings[0].toLowerCase())
                    .handle(cs, strings[0], Arrays.copyOfRange(strings, 1, strings.length));
        } else {
            sendSubcommandNotFoundErrorMessage(cs);
        }
        return true;
    }
    
    private void sendCommandList(CommandSender cs) {
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Help for Perks plugin: ");
        for(String command: commands.keySet()) {
            PerksCommandHandler handler = commands.get(command);
            if(cs instanceof Player) {
                new FancyMessage(PerksPlugin.getMessageUtil())
                        .addFancy(PerksPlugin.getMessageUtil().STRESSED+"/perk "+command
                                +handler.getShortDescription(command),
                                "/perk "+command,
                                PerksPlugin.getMessageUtil()
                                        .hoverFormat("/perk "+command
                                                     +handler.getUsageDescription(command),":",true))
                        .send((Player)cs);
            } else {
                PerksPlugin.getMessageUtil().sendInfoMessage(cs, handler.getShortDescription(command));
            }
        }
    }
    
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        PerksPlugin.getMessageUtil().sendErrorMessage(cs, "Subcommand not found.");
    }
    
    public final void addCommandHandler(String name, PerksCommandHandler handler) {
        commands.put(name, handler);
    }

    public Map<String, PerksCommandHandler> getCommands() {
        return commands;
    }
}
