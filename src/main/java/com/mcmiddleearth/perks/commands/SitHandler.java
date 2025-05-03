package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.perks.SitPerk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SitHandler extends PerksCommandHandler {

    public SitHandler(Perk perk, String... permissionNodes) {
        super(0, true, perk, permissionNodes);
    }

    @Override
    public String getShortDescription(String cmd) {
        return ": "+ PerksPlugin.getMessageUtil().INFO+"Gives you a tool item to sit down.";
    }

    @Override
    public String getUsageDescription(String cmd) {
        return getShortDescription(cmd)+" There is nothing special about that item except of the name. "
                +"You can also get it from creative inventory.";
    }

    @Override
    protected void execute(CommandSender cs, String cmd, String... args) {
        Player player = (Player)cs;
        ItemStack item = new ItemStack(SitPerk.getItem(),1);
        ItemMeta meta = item.getItemMeta();
        meta.setItemName("Sitting Tool");
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        PerksPlugin.getMessageUtil().sendInfoMessage(cs, "Have fun with your sitting tool.");
    }

}
