package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.permissions.CreditData;
import com.mcmiddleearth.perks.permissions.PermissionData;

public class DefinitionItem extends GuiItem {

    public DefinitionItem() {
        CreditData creditData = PermissionData.getCredits(player);

    }
}
