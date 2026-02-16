package com.mcmiddleearth.perks.tabCompleter;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.commands.PerksCommandHandler;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jubo
 */
public class TabComplete implements TabCompleter {

    private static final Set<String> ADMIN_COMMANDS = Set.of(
        "enable", "disable", "info", "open", "close"
    );

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command,
                                       String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            return Collections.emptyList();
        }

        Map<String, PerksCommandHandler> commands =
            PerksPlugin.getInstance().getPerksExecutor().getCommands();
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            String partial = strings[0].toLowerCase();
            for (String cmdName : commands.keySet()) {
                if (!cmdName.startsWith(partial)) continue;
                if (isAllowedForPlayer(player, cmdName, commands.get(cmdName))) {
                    completions.add(cmdName);
                }
            }
        } else if (strings.length == 2 && strings[0].equalsIgnoreCase("help")) {
            String partial = strings[1].toLowerCase();
            for (String cmdName : commands.keySet()) {
                if (cmdName.equals("help")) continue;
                if (!cmdName.startsWith(partial)) continue;
                if (isAllowedForPlayer(player, cmdName, commands.get(cmdName))) {
                    completions.add(cmdName);
                }
            }
        }

        Collections.sort(completions);
        return completions;
    }

    private boolean isAllowedForPlayer(Player player, String cmdName,
                                        PerksCommandHandler handler) {
        if (ADMIN_COMMANDS.contains(cmdName)) {
            return player.hasPermission(Permissions.ADMIN.getPermissionNode());
        }
        Perk perk = handler.getPerk();
        return perk == null || PermissionData.isAllowed(player, perk);
    }
}
