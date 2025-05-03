package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.EquipmentPerk;
import com.mcmiddleearth.perks.perks.ItemPerk;
import com.mcmiddleearth.perks.perks.Perk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EquipmentHandler extends PerksCommandHandler {

    public EquipmentHandler(Perk perk, String... permissionNodes) {
        super(0, true, perk, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return "Get decorative equipment.";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        return "Get decorative equipment.";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player player = (Player)cs;
        if (((EquipmentPerk)getPerk()).hasItem(player)) {
            PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You already have this item: "+getPerk().getName());
            return;
        }
        ((EquipmentPerk)getPerk()).giveItem(player);
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Have fun with your "
                +((EquipmentPerk)this.getPerk()).getItemName());
    }
}
