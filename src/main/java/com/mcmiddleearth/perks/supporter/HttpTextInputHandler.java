/*
 * Copyright (C) 2016 MCME
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
package com.mcmiddleearth.perks.supporter;

import com.google.gson.JsonSyntaxException;
import com.mcmiddleearth.perks.PerksPlugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mcmiddleearth.perks.permissions.PermissionUpdater;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class HttpTextInputHandler {
    
    private HttpURLConnection connection = null;
    private boolean received = false;
    private boolean error = false;
    private final String httpURL;
    private final int httpTimeout;
    
    public HttpTextInputHandler(String url, int timeout) {
        if(!(url.startsWith("https://") || url.startsWith("http://"))) {
            url = url+"http://";
        }
        httpURL = url;
        httpTimeout = timeout;
    }
    
    public synchronized void start(PermissionUpdater updater) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(error) {
                    sendIOException();
                    cancel();
                    return;
                }
                if(received) {
                    try {
                        cancel();
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            handleTextInput(new BufferedReader(new InputStreamReader(connection.getInputStream())));
                            updater.updatePermissions();
                        } else {
                            sendBadResponseError();
                        }
                        return;
                    } catch (IOException | JsonSyntaxException ex) {
                        Logger.getLogger(HttpTextInputHandler.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        cancel();
                    }
                    sendIOException();
                }
            }

        }.runTaskTimerAsynchronously(PerksPlugin.getInstance(), 10, 10);
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    connection = (HttpURLConnection) new URL(httpURL).openConnection();
                    connection.setReadTimeout(httpTimeout);
                    connection.connect();
                } catch (IOException ex) {
                    error = true;
                    Logger.getLogger(HttpTextInputHandler.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    received = true;
                }
            }
        }.runTaskAsynchronously(PerksPlugin.getInstance());
    }
    
    protected abstract void handleTextInput(BufferedReader reader); 

    protected abstract void sendIOException();

    protected abstract void sendBadResponseError();

}
