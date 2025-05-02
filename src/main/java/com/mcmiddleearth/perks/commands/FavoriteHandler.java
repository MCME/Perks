package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.gui.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FavoriteHandler extends PerksCommandHandler {

    public FavoriteHandler(String... permissionNodes) {
        super(1, true, null, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return ": "+ PerksPlugin.getMessageUtil().INFO+"Choose your favorite perks.";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        if(subcommand.equals("favor")) {
            return "<perk>: Select a perk to be displayed in your list of favorite perks.";
        } else {
            return "<perk>: Remove a perk from your list of favorite perks.";
        }
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        if(cmd.equals("favor")) {
            if(GuiManager.addFavorite((Player)cs, args[0])) {
                PerksPlugin.getMessageUtil().sendInfoMessage(cs,"Perk "+args[0]+" added to your favorites.");
            } else {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs,"Perk "+args[0]+" could not be added to your favorites.");
            }
        } else if(cmd.equals("unfavor")) {
            if(GuiManager.removeFavorite((Player)cs, args[0])) {
                PerksPlugin.getMessageUtil().sendInfoMessage(cs,"Perk "+args[0]+" removed from your favorites.");
            } else {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs,"Perk "+args[0]+" could not be removed from your favorites.");
            }
        }

    }


}
