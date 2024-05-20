/*
 * Copyright (C) 2017 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.perks.permissions;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.utils.HttpTextInputHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemoryConfiguration;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author Eriol_Eandur
 */
public class DonorDataInputHandler extends HttpTextInputHandler{

    private final boolean identifyPlayerByName = PerksPlugin.getInstance()
                           .getConfig().getBoolean("identifyPlayerByNameAllowed", true);
    public DonorDataInputHandler(String url, int timeout) {
        super(url, timeout);
    }
    
    @Override
    protected synchronized void handleTextInput(BufferedReader reader) {
        MemoryConfiguration config = new MemoryConfiguration();
        try(Scanner scanner = new Scanner(reader)) {
            scanner.useDelimiter("</td>");
            scanner.next();
            scanner.next();
            scanner.next();
            scanner.next();
            scanner.next();
            while(scanner.hasNext()) {
                String name = scanner.next();
                name = name.substring(name.indexOf("<td>")+4);
                String uuid = scanner.next();
                uuid = uuid.substring(uuid.indexOf("<td>")+4);
                if(uuid.equals("") && name.equals("")) {
                    scanner.next();
                    scanner.next();
                    continue;
                }
                if(uuid.equals("")) {
                    uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
                } else {
                    uuid = uuidFromString(uuid).toString();
                }
                if(!uuid.equals("")) {
                    String donation = scanner.next();
                    donation = donation.substring(donation.indexOf("<td>")+4);
                    donation = "Donor_"+donation.substring(0,donation.indexOf("."));
                    List<String> donationList = new ArrayList<>();
                    donationList.add(donation);
                    config.set(uuid, donationList);
                    scanner.next();
                }
            }
        }
        PermissionData.updateCredits(config);
    }

    @Override
    protected void sendIOException() {
    }

    @Override
    protected void sendBadResponseError() {
    }
    
    private static UUID uuidFromString(String data) {
        String low = data.substring(0,8);
        String mid = data.substring(8,12);
        String high = data.substring(12,16);
        String var = data.substring(16,20);
        String node = data.substring(20,32);
        return UUID.fromString(low+"-"+mid+"-"+high+"-"+var+"-"+node);
    }
    
}
