package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.gui.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiHandler extends PerksCommandHandler {

    public GuiHandler(String... permissionNodes) {
        super(0, true, null, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return ": "+ PerksPlugin.getMessageUtil().INFO+"Open perks menu.";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        return ": "+ PerksPlugin.getMessageUtil().INFO+"Open menu to interact with your perks..";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        if(cs instanceof Player player) {
            GuiManager.openGui(player);
        }
    }
}
