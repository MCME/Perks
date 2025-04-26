package com.mcmiddleearth.perks.gui;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListDisplay {

    private final List<GuiItem> items = new ArrayList<>();

    private final int firstSlot;
    private final int rows, columns;
    private final int firstVisible;
    private final boolean alwaysShoArrows;

    private final Inventory inventory;

    public ListDisplay(Inventory inventory, List<GuiItem> items, int firstSlot, int rows, int columns, boolean alwaysShowArrows) {
        if(items!=null) this.items.addAll(items);
        this.firstSlot = firstSlot;
        this.rows = rows;
        this.columns = columns;
        firstVisible = 0;
        this.alwaysShoArrows = alwaysShowArrows;
        this.inventory = inventory;
    }

    public void display() {

    }

    public void nextPage() {

    }

    public void PreviousPage() {

    }

    public boolean isSlotInside(int slot) {
        return false;
    }

    public List<GuiItem> getItems() {
        return items;
    }

    public boolean handleClick(int slot, @NotNull ClickType click) {
        return false;
    }
}
