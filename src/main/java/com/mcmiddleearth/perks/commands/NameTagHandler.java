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
import com.mcmiddleearth.perks.perks.NameTagPerk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 *
 * @author Eriol_Eandur
 */
public class NameTagHandler extends PerksCommandHandler {

    private final NameTagPerk nameTagPerk;

    public NameTagHandler(NameTagPerk perk, String... permissionNodes) {
        super(0, true, perk, permissionNodes);
        this.nameTagPerk = perk;
    }

    @Override
    public String getShortDescription(String cmd) {
        return ": " + PerksPlugin.getMessageUtil().INFO + "Customizes your name tag color and style.";
    }

    @Override
    public String getUsageDescription(String cmd) {
        return "[tier|off|info]: Changes your name tag style. "
                + "Use 'info' to see available tiers, 'off' to remove your name tag.";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player player = (Player) cs;

        if (args.length < 1 || args[0].equalsIgnoreCase("info")) {
            showInfo(player);
            return;
        }

        if (args[0].equalsIgnoreCase("off")) {
            nameTagPerk.removeTier(player);
            PerksPlugin.getMessageUtil().sendInfoMessage(player, "Your name tag has been removed.");
            return;
        }

        String tierName = args[0].toLowerCase();
        List<String> available = nameTagPerk.getAvailableTiers(player);

        if (!available.contains(tierName)) {
            PerksPlugin.getMessageUtil().sendErrorMessage(player,
                    "You don't have access to the '" + tierName + "' tier.");
            showInfo(player);
            return;
        }

        nameTagPerk.applyTier(player, tierName);
        PerksPlugin.getMessageUtil().sendInfoMessage(player,
                "Your name tag has been set to the '" + tierName + "' tier.");
    }

    private void showInfo(Player player) {
        List<String> available = nameTagPerk.getAvailableTiers(player);
        String currentTier = nameTagPerk.getPlayerTier(player);

        PerksPlugin.getMessageUtil().sendInfoMessage(player, "--- Name Tag ---");
        PerksPlugin.getMessageUtil().sendInfoMessage(player,
                "Current tier: " + PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED + currentTier);
        PerksPlugin.getMessageUtil().sendInfoMessage(player,
                "Available tiers: " + PerksPlugin.getMessageUtil().HIGHLIGHT_STRESSED
                        + String.join(", ", available));
        PerksPlugin.getMessageUtil().sendInfoMessage(player,
                "Usage: /perk name <tier> to change, /perk name off to remove.");
    }
}
