package com.mcmiddleearth.perks.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListDisplay {

    private static final int inventoryColumns = 9;

    private final GuiItem previousItem;
    private final GuiItem nextItem;

    private final List<GuiItem> items = new ArrayList<>();

    private final int firstSlot;
    private final int rows, columns;
    private final boolean alwaysShoArrows;

    private int firstVisibleItemIndex;

    private final Inventory inventory;

    public ListDisplay(Inventory inventory, List<GuiItem> items, int firstSlot, int rows, int columns,
                       boolean alwaysShowArrows, GuiItem previousItem, GuiItem nextItem) {
        if(items!=null) this.items.addAll(items);
        this.firstSlot = firstSlot;
        this.rows = rows;
        this.columns = columns;
        firstVisibleItemIndex = 0;
        this.alwaysShoArrows = alwaysShowArrows;
        this.inventory = inventory;
        this.previousItem = previousItem;
        this.nextItem = nextItem;
    }

    public void display() {
        int currentItemIndex = firstVisibleItemIndex;
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                if(hasPreviousItem() && row==0 && column==0) {
                    inventory.setItem(firstSlot, previousItem.getItemStack());
                    currentItemIndex++;
                } else if(hasNextItem() && row==rows-1 && column==columns-1) {
                    inventory.setItem(firstSlot+row*inventoryColumns+column, nextItem.getItemStack());
                    currentItemIndex++;
                } else {
                    inventory.setItem(firstSlot+row*inventoryColumns+column, items.get(currentItemIndex).getItemStack());
                    currentItemIndex++;
                }
            }
        }

    }

    public void nextPage() {
        firstVisibleItemIndex+=columns;
        int lastVisible = calculateLastVisibleItemIndex();
        if(lastVisible >= items.size()) {
            firstVisibleItemIndex-=lastVisible-items.size()+1;
        }
        display();
    }

    public void previousPage() {
        firstVisibleItemIndex-=columns;
        if(firstVisibleItemIndex < 0) {
            firstVisibleItemIndex = 0;
        }
        display();
    }

    public boolean hasPreviousItem() {
        return alwaysShoArrows || firstVisibleItemIndex != 0;
    }

    public boolean hasNextItem() {
        return alwaysShoArrows || calculateLastVisibleItemIndex() >= items.size();
    }

    public int calculateLastVisibleItemIndex() {
        int lastVisibleItemIndex = firstVisibleItemIndex+rows*columns-1;
        if(hasPreviousItem()) lastVisibleItemIndex--;
        if(lastVisibleItemIndex<items.size()-1) lastVisibleItemIndex--;
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
            if (isPreviousItem(slot)) {
                previousPage();
            } else if (isNextItem(slot)) {
                nextPage();
            } else {
                getItem(slot).handleClick(player, click);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isPreviousItem(int slot) {
        return previousItem.getItemStack().equals(inventory.getItem(slot));
    }

    public boolean isNextItem(int slot) {
        return nextItem.getItemStack().equals(inventory.getItem(slot));
    }

    public GuiItem getItem(int slot) {
        int row = 0;
        int index = slot - firstSlot;
        while(index/9 > 0) {
            index = index - 9;
            row++;
        }
        return items.get(row*columns+index);
    }
}
