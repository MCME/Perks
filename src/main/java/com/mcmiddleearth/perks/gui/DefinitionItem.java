package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.permissions.CreditData;
import com.mcmiddleearth.perks.permissions.PermissionData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DefinitionItem extends GuiItem {

    public DefinitionItem(ItemStack itemStack, String leftCommand, String rightCommand, String middleCommand) {
        super(itemStack, leftCommand, rightCommand, middleCommand);
        //CreditData creditData = PermissionData.getCredits(player);
    }

    public static DefinitionItem load(ConfigurationSection definition, String name, Player player, CreditData creditData) {
        GuiItem guiItem = GuiItem.load(definition);
        ItemStack definitionItem = guiItem.getItemStack();
        ItemMeta meta = definitionItem.getItemMeta();
        meta.customName(Component.text(name).color(NamedTextColor.YELLOW));
        List<Component> loreC = new ArrayList<>();
        loreC.add(Component.text("Requirements:"));
        if(definition.contains("total"))
            loreC.add(Component.text("  Total support: "+definition.getInt("total"))
                    .color(definition.getInt("total") > creditData.getTotal()
                            ? NamedTextColor.RED:NamedTextColor.GREEN));
        if(definition.contains("forum"))
            loreC.add(Component.text("  Forum support: "+definition.getInt("forum"))
                    .color(definition.getInt("forum")>creditData.getForum()
                            ? NamedTextColor.RED:NamedTextColor.GREEN));
        if(definition.contains("patreon"))
            loreC.add(Component.text("  Patreon support: "+definition.getInt("patreon"))
                    .color(definition.getInt("patreon")>creditData.getPatreon()
                            ? NamedTextColor.RED:NamedTextColor.GREEN));
        if(definition.contains("tiers")) {
            loreC.add(Component.text("  Tiers: "));
            for(String tier: definition.getStringList("tiers")) {
                loreC.add(Component.text("    - "+tier)
                        .color(!creditData.getTiers().contains(tier)
                                ? NamedTextColor.RED:NamedTextColor.GREEN));
            }
        }
        if(definition.contains("perks")) {
            loreC.add(Component.text("Awarded perks:"));
            for(String perk: definition.getStringList("perks")) {
                loreC.add(Component.text("  - "+perk)
                        .color(!PermissionData.isAllowed(player, PerkManager.forName(perk))
                                ?NamedTextColor.RED:NamedTextColor.GREEN));
            }
        }
        meta.lore(loreC);
        definitionItem.setItemMeta(meta);
        return new DefinitionItem(definitionItem, guiItem.getLeftCommand(), guiItem.getRightCommand(), guiItem.getMiddleCommand());
    }
}
