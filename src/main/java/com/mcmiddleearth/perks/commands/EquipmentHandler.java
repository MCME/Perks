package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.EquipmentPerk;
import com.mcmiddleearth.perks.perks.ItemPerk;
import com.mcmiddleearth.perks.perks.Perk;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EquipmentHandler extends PerksCommandHandler {

    public EquipmentHandler(Perk perk, String... permissionNodes) {
        super(0, true, perk, permissionNodes);
    }

    @Override
    public String getShortDescription(String subcommand) {
        return ": "+PerksPlugin.getMessageUtil().INFO+"Get decorative equipment.";
    }

    @Override
    public String getUsageDescription(String subcommand) {
        return " equip | unequip: Get decorative equipment.";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player player = (Player)cs;
        String itemName = ((EquipmentPerk) getPerk()).getItemName();
        String plainTextItemName = ChatColor.stripColor(itemName);
        if(args.length < 1 || args[0].equalsIgnoreCase("equip")) {
            if (((EquipmentPerk) getPerk()).hasItem(player)) {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You already have this item: " + plainTextItemName);
                return;
            }
            ((EquipmentPerk) getPerk()).giveItem(player);
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Have fun with " + itemName);
        } else {
            if (!((EquipmentPerk) getPerk()).hasItem(player)) {
                PerksPlugin.getMessageUtil().sendErrorMessage(cs, "You don't have this item: " + plainTextItemName);
                return;
            }
            ((EquipmentPerk) getPerk()).removeItems(player);
            PerksPlugin.getMessageUtil().sendInfoMessage(cs, itemName + " removed.");
        }
    }
}
