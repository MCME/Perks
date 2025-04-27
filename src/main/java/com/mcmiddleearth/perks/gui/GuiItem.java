package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerksPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GuiItem {

    private ItemStack itemStack;

    private String leftCommand;
    private String rightCommand;
    private String middleCommand;

    public void handleClick(Player player, ClickType clickType) {
        if(clickType.isLeftClick()) {
            executeCommand(player, leftCommand);
        } else if(clickType.equals(ClickType.MIDDLE)) {
            executeCommand(player, middleCommand);
        } else if(clickType.isRightClick()) {
            executeCommand(player, rightCommand);
        }
    }

    private void executeCommand(Player player, String command) {
        if(command != null && !command.isEmpty()) {
            String[] split = command.split(" ");
            if (split[0].equalsIgnoreCase("/perk")) {
                PerksPlugin.getInstance().getPerksExecutor().onCommand(player, null, "perk",
                        Arrays.copyOfRange(split, 1, split.length));
            }
        }
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
