package com.mcmiddleearth.perks.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

public class ListDisplay {

    private static final int inventoryColumns = 9;

    private final GuiItem previousItem;
    private final GuiItem nextItem;
    private final GuiItem previousItemDeactivated;
    private final GuiItem nextItemDeactivated;

    private final List<GuiItem> items = new ArrayList<>();
    private final Stack<Integer> previousPages = new Stack<>();

    private final int firstSlot;
    private final int rows, columns;
    private final boolean alwaysShoArrows;

    private int firstVisibleItemIndex;

    private final Inventory inventory;

    public ListDisplay(Inventory inventory, List<GuiItem> items, int firstSlot, int rows, int columns,
                       boolean alwaysShowArrows, GuiItem previousItem, GuiItem nextItem, int firstVisibleItemIndex) {
        this(inventory, items, firstSlot, rows, columns, alwaysShowArrows, previousItem, nextItem, previousItem, nextItem, firstVisibleItemIndex);
    }

    public ListDisplay(Inventory inventory, List<GuiItem> items, int firstSlot, int rows, int columns,
                        GuiItem previousItem, GuiItem nextItem, GuiItem previousItemDeactivated, GuiItem nextItemDeactivated, int firstVisibleItemIndex) {
        this(inventory, items, firstSlot, rows, columns, true,
                previousItem, nextItem, previousItemDeactivated, nextItemDeactivated, firstVisibleItemIndex);
    }

    private ListDisplay(Inventory inventory, List<GuiItem> items, int firstSlot, int rows, int columns, boolean alwaysShowArrows,
                        GuiItem previousItem, GuiItem nextItem, GuiItem previousItemDeactivated, GuiItem nextItemDeactivated, int firstVisibleItemIndex) {
        if(items!=null) this.items.addAll(items);
        this.firstSlot = firstSlot;
        this.rows = rows;
        this.columns = columns;
        this.firstVisibleItemIndex = firstVisibleItemIndex;
        this.alwaysShoArrows = alwaysShowArrows;
        this.inventory = inventory;
        this.previousItem = previousItem;
        this.nextItem = nextItem;
        this.previousItemDeactivated = previousItemDeactivated;
        this.nextItemDeactivated = nextItemDeactivated;
    }

    public void display() {
        int currentItemIndex = firstVisibleItemIndex;
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
Logger.getGlobal().info("currentItemIndex: "+currentItemIndex + " fistVisibleItemIndex: "+firstVisibleItemIndex);
                if(hasPreviousItem() && row==0 && column==0) {
                    if(isPreviousActive()) {
                        inventory.setItem(firstSlot, previousItem.getItemStack());
                    } else {
                        inventory.setItem(firstSlot, previousItemDeactivated.getItemStack());
                    }
Logger.getGlobal().info("previous");
                    //currentItemIndex++;
                } else if(hasNextItem() && row==rows-1 && column==columns-1) {
                    if(isNextActive()) {
                        inventory.setItem(firstSlot + row * inventoryColumns + column, nextItem.getItemStack());
                    } else {
                        inventory.setItem(firstSlot + row * inventoryColumns + column, nextItemDeactivated.getItemStack());
                    }
Logger.getGlobal().info("next");
                    //currentItemIndex++;
                } else {
                    if(currentItemIndex < items.size()) {
                        Logger.getGlobal().info("Set list display item [" + currentItemIndex + "]: " + items.get(currentItemIndex).getItemStack().getType());
                        inventory.setItem(firstSlot + row * inventoryColumns + column, items.get(currentItemIndex).getItemStack());
                        currentItemIndex++;
                    } else {
                        inventory.setItem(firstSlot + row * inventoryColumns + column, new ItemStack(Material.AIR));
                    }
                }
            }
Logger.getGlobal().info("next row");

        }
Logger.getGlobal().info("done");


    }

    public void nextPage() {
        int visibleItems = columns;
        if(hasPreviousItem()) visibleItems--;
        if(hasNextItem()) visibleItems--;
        firstVisibleItemIndex+=visibleItems;
        previousPages.push(visibleItems);
        /*int lastVisible = calculateLastVisibleItemIndex();
        if(lastVisible >= items.size()) {
            firstVisibleItemIndex-=lastVisible-items.size()+1;
        }*/
        display();
    }

    public void previousPage() {
        if(!previousPages.empty()) {
            firstVisibleItemIndex -= previousPages.pop();
        } else {
            firstVisibleItemIndex -= columns - 2;
        }
        if(firstVisibleItemIndex < 0) {
            firstVisibleItemIndex = 0;
        }
        display();
    }

    public boolean hasPreviousItem() {
        return alwaysShoArrows || isPreviousActive();
    }

    public boolean hasNextItem() {
        return alwaysShoArrows || isNextActive();
    }

    public boolean isPreviousActive() {
        return firstVisibleItemIndex > 0;
    }

    public boolean isNextActive() {
Logger.getGlobal().info("isNextActive: last visible: "+calculateLastVisibleItemIndex()+" size: "+items.size());
        return calculateLastVisibleItemIndex() < items.size();
    }

    public int calculateLastVisibleItemIndex() {
        int lastVisibleItemIndex = firstVisibleItemIndex+rows*columns-1;
        if(hasPreviousItem()) lastVisibleItemIndex--;
        if(lastVisibleItemIndex<items.size()) lastVisibleItemIndex--;
        return lastVisibleItemIndex;
    }

    public boolean isSlotInside(int slot) {
        if(slot<firstSlot) return false;
        int row = 0;
        int index = slot - firstSlot;
        while(index/9 > 0) {
            index = index - 9;
            row++;
        }
        return row < rows && index < columns;
    }

    public List<GuiItem> getItems() {
        return items;
    }

    public boolean handleClick(Player player, int slot, @NotNull ClickType click) {
        if(isSlotInside(slot)) {
            if (isPreviousActive() && slot == firstSlot) {
                previousPage();
            } else if (isNextActive() && slot == firstSlot + (rows-1) * inventoryColumns + (columns-1)) {
                nextPage();
            } else {
                GuiItem guiItem = getItem(slot);
                if(guiItem!= null) {
                    guiItem.handleClick(player, click);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public GuiItem getItem(int slot) {
        int row = 0;
        int index = slot - firstSlot;
        while(index/9 > 0) {
            index = index - 9;
            row++;
        }
        int finalIndex = row*columns+index+firstVisibleItemIndex;
        if(hasPreviousItem()) finalIndex--;
        int maxIndex = items.size()-1;
        if(hasNextItem()) maxIndex--;
Logger.getGlobal().info("has previous: "+hasPreviousItem());
Logger.getGlobal().info("slot "+slot+" final index: "+finalIndex);
        return finalIndex >= 0 && finalIndex <= maxIndex ? items.get(finalIndex) : null;
    }

    public int getFirstVisibleItemIndex() {
        return firstVisibleItemIndex;
    }
}
