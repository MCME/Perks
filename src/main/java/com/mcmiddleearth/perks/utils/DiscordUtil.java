package com.mcmiddleearth.perks.utils;

import github.scarsz.discordsrv.DiscordSRV;

import java.util.UUID;

public class DiscordUtil {

    public static UUID getUniqueId(String discordId) {
        DiscordSRV discordSRV = DiscordSRV.getPlugin();
        if(discordSRV != null) {
            return discordSRV.getAccountLinkManager().getUuid(discordId);
        } else {
            return null;
        }
    }
}
