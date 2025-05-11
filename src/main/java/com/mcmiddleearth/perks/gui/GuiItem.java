package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.utils.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.logging.Logger;

public class GuiItem {

    private ItemStack itemStack;

    private String leftCommand;
    private String rightCommand;
    private String middleCommand;

    public static final GuiItem errorItem;

    static {
        errorItem = new GuiItem(new ItemStack(Material.STONE), "","","");
        ItemMeta meta = errorItem.getItemStack().getItemMeta();
        meta.setItemName("Error: Something went wrong.");
        errorItem.getItemStack().setItemMeta(meta);
    }

    public GuiItem(ItemStack itemStack, String leftCommand, String rightCommand, String middleCommand) {
        this.itemStack = itemStack;
        this.leftCommand = leftCommand;
        this.rightCommand = rightCommand;
        this.middleCommand = middleCommand;
    }

    public static GuiItem load(ConfigurationSection config) {
        if(config == null) return GuiItem.errorItem;
        ConfigurationSection itemConfig = config.getConfigurationSection("itemStack");
        if(itemConfig==null) {
Logger.getGlobal().info("missing itemConfig for gui item: "+config.getCurrentPath());
            return GuiItem.errorItem;
        }
        ItemStack item = ItemStackUtil.loadItem(itemConfig);
        String leftCommand = config.getString("leftCommand","");
        String rightCommand = config.getString("rightCommand","");
        String middleCommand = config.getString("MiddleCommand","");
        return new GuiItem(item, leftCommand, rightCommand, middleCommand);
    }

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

    public String getLeftCommand() {
        return leftCommand;
    }

    public String getRightCommand() {
        return rightCommand;
    }

    public String getMiddleCommand() {
        return middleCommand;
    }
}
