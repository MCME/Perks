package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.supporter.PatreonClient;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class PatreonTestHandler extends PerksCommandHandler {

    public PatreonTestHandler(String... permissionNodes) {
        super(0, false, null, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return "Test";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        return "patreon";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        if (args.length == 0) {
            Bukkit.getScheduler().runTaskAsynchronously(PerksPlugin.getInstance(), () -> {
                try {
                    PatreonClient.fetchMembers(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(PerksPlugin.getInstance(), () -> {
                try {
                    PatreonClient.exchangeCodeForTokens(args[0]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
