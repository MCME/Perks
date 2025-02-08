package com.mcmiddleearth.perks.tabCompleter;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.permissions.PermissionData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jubo
 */
public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> completions = new ArrayList<>();

        if(PermissionData.isAllowed((Player)commandSender, PerkManager.forName("ring"))) completions.add("ring");
        if(PermissionData.isAllowed((Player)commandSender, PerkManager.forName("light"))) completions.add("light");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("boat"))) completions.add("boat");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("pet"))) completions.add("pet");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("horse"))) completions.add("horse");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("firework"))) completions.add("firework");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("elytra"))) completions.add("elytra");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("parrot"))) completions.add("parrot");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("fire"))) completions.add("fire");
        if(PermissionData.isAllowed((Player)commandSender,PerkManager.forName("compass"))) completions.add("compass");

        java.util.Collections.sort(completions);

        return completions;
    }
}
