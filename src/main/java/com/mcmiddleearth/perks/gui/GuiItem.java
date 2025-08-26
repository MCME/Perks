package com.mcmiddleearth.perks.gui;

import com.google.common.base.Joiner;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.utils.ItemStackUtil;
import com.mcmiddleearth.perks.utils.PlaceholderData;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GuiItem {

    private ItemStack itemStack;

    private String leftCommand;
    private String rightCommand;
    private String middleCommand;

    private int order;

    public static final GuiItem errorItem;

    static {
        errorItem = new GuiItem(new ItemStack(Material.STONE), "","","", 1);
        ItemMeta meta = errorItem.getItemStack().getItemMeta();
        meta.setItemName("Error: Something went wrong.");
        errorItem.getItemStack().setItemMeta(meta);
    }

    public GuiItem(ItemStack itemStack, String leftCommand, String rightCommand, String middleCommand, int order) {
        this.itemStack = itemStack;
        this.leftCommand = leftCommand;
        this.rightCommand = rightCommand;
        this.middleCommand = middleCommand;
        this.order = order;
    }

    public static GuiItem load(ConfigurationSection config, PlaceholderData data) {
        if(config == null) return GuiItem.errorItem;
//config.getKeys(false).stream().forEach(key -> Logger.getGlobal().info(key));
//        ConfigurationSection itemConfig = config.getConfigurationSection("itemStack");
//        if(itemConfig==null) {
//Logger.getGlobal().info("missing itemConfig for gui item: "+config.getCurrentPath());
//            return GuiItem.errorItem;
//        }
        ItemStack item = ItemStackUtil.loadItem(config, data);
        String leftCommand = config.getString("leftCommand","");
        String rightCommand = config.getString("rightCommand","");
        String middleCommand = config.getString("MiddleCommand","");
        int order = config.getInt("order",1);
        return new GuiItem(item, leftCommand, rightCommand, middleCommand, order);
    }

    public static int compare(GuiItem one, GuiItem two) {
        return Integer.compare(one.getOrder(), two.getOrder());
    }

    public void handleClick(Player player, ClickType clickType) {
//Logger.getGlobal().info("Right command: "+rightCommand);
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
            if (split[0].equalsIgnoreCase("closegui")) {
                player.closeInventory();
            }
            if(split[1].equalsIgnoreCase("/perk")) {
                PerksPlugin.getInstance().getPerksExecutor().onCommand(player, null, "perk",
                        Arrays.copyOfRange(split, 2, split.length));
//Logger.getGlobal().info("Execute perk command: "+command);
            } else if (split[1].equalsIgnoreCase("/message")) {
                player.sendMessage(JSONComponentSerializer.json()
                        .deserialize(Joiner.on(" ").join(Arrays.copyOfRange(split, 2, split.length))));
//Logger.getGlobal().info("Sending Message: "+command);
            }
            if(split[0].equalsIgnoreCase("updategui")) {
//Logger.getGlobal().info("update gui");
                GuiManager.updateGui(player);
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

    public void setRightCommand(String rightCommand) {
        this.rightCommand = rightCommand;
    }

    public void setLeftCommand(String leftCommand) {
        this.leftCommand = leftCommand;
    }

    public int getOrder() {
        return order;
    }
}
