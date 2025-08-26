package com.mcmiddleearth.perks.utils;

import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.permissions.PermissionData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlaceholderData {

    private boolean favorite;
    private List<String> tiers;
    private double total;
    private List<String> requiredTiers;
    private double requiredTotal;

    public PlaceholderData() {
    }

    public PlaceholderData(Player player) {
        tiers(PermissionData.getCredits(player).getTiers());
        total(PermissionData.getCredits(player).getTotal());
    }

    public PlaceholderData(Perk perk) {
        double minimum = Double.MAX_VALUE;
        List<String> minimumTiers = Collections.emptyList();
        for(String key: PermissionData.getPerkDefinitions().getKeys(false)) {
            ConfigurationSection config = PermissionData.getPerkDefinitions().getConfigurationSection(key);
            List<String> definitionPerks = config.getStringList("perks");
            if(definitionPerks.contains(perk.getName())) {
                double definitionTotal = config.getDouble("total");
                if(definitionTotal < minimum) {
                    minimum = definitionTotal;
                }
                List<String> definitionTiers = config.getStringList("tiers");
                if(definitionTiers.size() < minimumTiers.size() || minimumTiers.isEmpty()) {
                    minimumTiers = definitionTiers;
                }
            }
        }
        requiredTotal = minimum;
        requiredTiers = minimumTiers;
    }

    public PlaceholderData favorite(boolean isFavorite) {
        favorite = isFavorite;
        return this;
    }

    public PlaceholderData tiers(List<String> tiers) {
        this.tiers = tiers;
        return this;
    }

    public PlaceholderData total(double amount) {
        total = amount;
        return this;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public List<String> getTiers() {
        return tiers;
    }

    public double getTotal() {
        return total;
    }

    public List<String> getRequiredTiers() {
        return requiredTiers;
    }

    public double getRequiredTotal() {
        return requiredTotal;
    }
}
