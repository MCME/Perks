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
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Claude
 */
public class HelpHandler extends PerksCommandHandler {

    private final Map<String, PerksCommandHandler> commands;

    private static final Set<String> ADMIN_COMMANDS = Set.of(
        "enable", "disable", "info", "open", "close"
    );

    public HelpHandler(Map<String, PerksCommandHandler> commands) {
        super(0, false, null);
        this.commands = commands;
    }

    @Override
    public String getShortDescription(String cmd) {
        return ": " + PerksPlugin.getMessageUtil().INFO + "Shows help for perks.";
    }

    @Override
    public String getUsageDescription(String cmd) {
        return "[perkname]: Shows help for the specified perk, "
             + "or lists all available perks.";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        if (args.length > 0) {
            showDetailedHelp(cs, args[0].toLowerCase());
        } else {
            showHelpMenu(cs);
        }
    }

    private void showHelpMenu(CommandSender cs) {
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "--- Perks Help ---");

        List<String> perkCommands = new ArrayList<>();
        List<String> adminCommands = new ArrayList<>();

        for (String command : commands.keySet()) {
            if (command.equals("help")) continue;

            if (ADMIN_COMMANDS.contains(command)) {
                if (cs instanceof Player player) {
                    if (player.hasPermission(Permissions.ADMIN.getPermissionNode())) {
                        adminCommands.add(command);
                    }
                } else {
                    adminCommands.add(command);
                }
            } else {
                PerksCommandHandler handler = commands.get(command);
                Perk perk = handler.getPerk();
                if (cs instanceof Player player) {
                    if (perk == null || PermissionData.isAllowed(player, perk)) {
                        perkCommands.add(command);
                    }
                } else {
                    perkCommands.add(command);
                }
            }
        }

        if (!perkCommands.isEmpty()) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Your Perks:");
            for (String command : perkCommands) {
                sendHelpLine(cs, command);
            }
        }

        if (!adminCommands.isEmpty()) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Admin:");
            for (String command : adminCommands) {
                sendHelpLine(cs, command);
            }
        }

        if (cs instanceof Player) {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs,
                "Click a command or type /perk help <name>");
        }
    }

    private void sendHelpLine(CommandSender cs, String command) {
        PerksCommandHandler handler = commands.get(command);
        if (cs instanceof Player player) {
            new FancyMessage(PerksPlugin.getMessageUtil())
                .addFancy(
                    PerksPlugin.getMessageUtil().STRESSED + "/perk " + command
                        + handler.getShortDescription(command),
                    "/perk " + command,
                    PerksPlugin.getMessageUtil()
                        .hoverFormat("/perk " + command
                            + handler.getUsageDescription(command), ":", true))
                .send(player);
        } else {
            PerksPlugin.getMessageUtil().sendInfoMessage(cs,
                "/perk " + command + handler.getShortDescription(command));
        }
    }

    private void showDetailedHelp(CommandSender cs, String perkName) {
        if (!commands.containsKey(perkName)) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs,
                "Unknown perk: " + perkName + ". Type /perk help for a list.");
            return;
        }

        PerksCommandHandler handler = commands.get(perkName);

        if (cs instanceof Player player) {
            Perk perk = handler.getPerk();
            if (perk != null && !PermissionData.isAllowed(player, perk)) {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs,
                    "You don't have access to this perk.");
                return;
            }
            if (ADMIN_COMMANDS.contains(perkName)
                    && !player.hasPermission(Permissions.ADMIN.getPermissionNode())) {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs,
                    "You don't have access to this command.");
                return;
            }
        }

        PerksPlugin.getMessageUtil().sendInfoMessage(cs,
            "--- Help: /perk " + perkName + " ---");
        PerksPlugin.getMessageUtil().sendInfoMessage(cs,
            handler.getShortDescription(perkName));
        PerksPlugin.getMessageUtil().sendInfoMessage(cs,
            "Usage: /perk " + perkName + " "
                + handler.getUsageDescription(perkName));

        if (cs instanceof Player player) {
            sendExamples(player, perkName);
        }
    }

    private void sendExamples(Player player, String perkName) {
        List<String> examples = getExamples(perkName);
        if (examples.isEmpty()) return;

        PerksPlugin.getMessageUtil().sendInfoMessage(player, "Examples (click to use):");
        for (String example : examples) {
            new FancyMessage(PerksPlugin.getMessageUtil())
                .addFancy(
                    PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED + "  " + example,
                    example,
                    PerksPlugin.getMessageUtil()
                        .hoverFormat("Click to suggest this command", ":", false))
                .send(player);
        }
    }

    private List<String> getExamples(String perkName) {
        return switch (perkName) {
            case "horse" -> List.of(
                "/perk horse",
                "/perk horse white",
                "/perk horse black white_dots"
            );
            case "boat" -> List.of(
                "/perk boat",
                "/perk boat oak",
                "/perk boat cherry",
                "/perk boat bamboo"
            );
            case "parrot" -> List.of(
                "/perk parrot",
                "/perk parrot blue left",
                "/perk parrot remove"
            );
            case "pet" -> List.of(
                "/perk pet cat",
                "/perk pet dog Fido",
                "/perk pet cat Mittens red tabby",
                "/perk pet dog Rex blue ashen",
                "/perk pet dismiss"
            );
            case "fire" -> List.of(
                "/perk fire",
                "/perk fire 10"
            );
            case "light" -> List.of(
                "/perk light",
                "/perk light 5"
            );
            case "compass" -> List.of(
                "/perk compass",
                "/perk compass north",
                "/perk compass 100 -200"
            );
            case "gallop" -> List.of(
                "/perk gallop",
                "/perk whoa"
            );
            case "whoa" -> List.of(
                "/perk whoa",
                "/perk gallop"
            );
            case "name" -> List.of(
                "/perk name",
                "/perk name info",
                "/perk name gold",
                "/perk name off"
            );
            case "speed", "jump", "ring", "elytra", "firework" -> List.of(
                "/perk " + perkName
            );
            case "enable" -> List.of(
                "/perk enable",
                "/perk enable horse"
            );
            case "disable" -> List.of(
                "/perk disable",
                "/perk disable horse"
            );
            case "open" -> List.of(
                "/perk open horse",
                "/perk open parrot 30"
            );
            case "close" -> List.of(
                "/perk close horse"
            );
            case "info" -> List.of(
                "/perk info"
            );
            default -> List.of();
        };
    }
}
