package com.mcmiddleearth.perks.gui;

import org.bukkit.inventory.ItemStack;

public class PerkItem extends GuiItem {
    public PerkItem(ItemStack itemStack, String leftCommand, String rightCommand, String middleCommand) {
        super(itemStack, leftCommand, rightCommand, middleCommand,1);
    }
}
