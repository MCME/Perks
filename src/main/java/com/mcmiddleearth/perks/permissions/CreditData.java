package com.mcmiddleearth.perks.permissions;

import com.mcmiddleearth.pluginutil.NumericUtil;

import java.util.ArrayList;
import java.util.List;

public class CreditData {

    private final List<String> tiers;
    private double forum;
    private double patreon;

    public CreditData(List<String> creditEntries) {
        this.tiers = new ArrayList<>();
        for(String entry: creditEntries) {
            String[] split = entry.split("_");
            switch(split[0]) {
                case "forum":
                    forum = NumericUtil.getInt(split[1]);
                    break;
                case "patreon":
                    patreon = NumericUtil.getInt(split[1]);
                    break;
                case "tier":
                    tiers.add(split[1]);
            }
        }
    }

    public List<String> getTiers() {
        return tiers;
    }

    public double getForum() {
        return forum;
    }

    public double getPatreon() {
        return patreon;
    }

    public double getTotal() {
        return forum + patreon;
    }
}
