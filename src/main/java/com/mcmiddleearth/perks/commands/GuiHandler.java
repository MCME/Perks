package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.gui.PerkGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiHandler extends PerksCommandHandler {

    public GuiHandler(String... permissionNodes) {
        super(0, true, null, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return "";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        return "";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        if(cs instanceof Player player) {
            PerkGui.openPerkGui(player);
        }
    }
}
