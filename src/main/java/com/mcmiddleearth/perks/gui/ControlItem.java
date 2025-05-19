package com.mcmiddleearth.perks.gui;

import org.bukkit.inventory.ItemStack;

public class ControlItem extends GuiItem {
    public ControlItem(ItemStack itemStack, String leftCommand, String rightCommand, String middleCommand) {
        super(itemStack, leftCommand, rightCommand, middleCommand,1);
    }
}
