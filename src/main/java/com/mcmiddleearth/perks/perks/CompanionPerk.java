/* Copyright (C) 2017 MCME
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
package com.mcmiddleearth.perks.perks;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.commands.CompanionHandler;
import com.mcmiddleearth.perks.listeners.CompanionListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.logging.Logger;

/**
 *
 * @author Fraspace5, Eriol_Eandur
 */
public class CompanionPerk extends Perk {

    private static Perk instance;

    private static final String companion_metadata_key = "MCME_Perks_Companion";

    //private static final File onlineFile = new File(PerksPlugin.getInstance().getDataFolder(), "onlineCompanions.dat");
    //private static final File offlineFile = new File(PerksPlugin.getInstance().getDataFolder(), "offlineCompanions.dat");

    //private static final Map<UUID, List<Data>> companionMap = new HashMap<>();

    private static boolean allowSpawn;

    private static final String perkName = "pet";

    public CompanionPerk() {
        super(perkName);
        setListener(new CompanionListener());
        setCommandHandler(new CompanionHandler(this, Permissions.USER.getPermissionNode()),perkName);
    }

    public static void spawnDog(Player owner, String name, DyeColor collarColor) {
        Location location = owner.getLocation();
        Wolf companion = location.getWorld().spawn(location, Wolf.class);
        //companion.setVariant(variant);
        companion.setCollarColor(collarColor);
        initializeCompanion(companion, owner, name);
    }

    public static void spawnCat(Player owner, String name, DyeColor collarColor, Cat.Type type) {
        Location location = owner.getLocation();
        Cat companion = location.getWorld().spawn(location, Cat.class);
        companion.setCatType(type);
        companion.setCollarColor(collarColor);
        initializeCompanion(companion, owner, name);
    }

    private static void initializeCompanion(Tameable companion, Player owner, String name) {
        companion.setAdult();
        companion.setTamed(true);
        companion.setOwner(owner);
        companion.customName(Component.text(name));
        companion.setMetadata(companion_metadata_key,
                              new FixedMetadataValue(PerksPlugin.getInstance(),
                                                     new Data(owner.getUniqueId(), System.currentTimeMillis())));
        /*List<Data> companionData;
        if(!companionMap.containsKey(owner.getUniqueId())) {
            companionData = new ArrayList<>();
            companionMap.put(owner.getUniqueId(), companionData);
        } else {
            companionData = companionMap.get(owner.getUniqueId());
        }
        companionData.add(new Data(owner.getLocation(), Status.ACTIVE));*/
    }

    public static void dismissCompanions(Player owner) {
        dismissCompanions(owner, null);
    }

    public static void dismissCompanions(Player owner, String name) {
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (isCompanionPerk(entity)) {
                    Data companionData = getCompanionData(entity);
                    if(companionData!=null && owner.getUniqueId().equals(companionData.getOwner())) {
                        Component customNameComponent = entity.customName();
                        String customName = null;
                        if(customNameComponent!=null) {
                            customName = PlainTextComponentSerializer.plainText().serialize(customNameComponent);
                        }
Logger.getGlobal().info("Custom name. "+customName);
                        if(name == null || name.equalsIgnoreCase(customName)) {
                            entity.remove();
                        }
                    }
                }
            }
        }
    }

    public static int countCompanions(Player owner) {
        int result = 0;
        for (World world : Bukkit.getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (isCompanionPerk(entity)) {
                    Data companionData = getCompanionData(entity);
                    if(companionData!=null && owner.getUniqueId().equals(companionData.getOwner())) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

    public void clearCompanion() {
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (isCompanionPerk(e)) {
                    e.remove();
                }
            }
        }
    }
    
    public void checkCompanion() {
        Map<UUID, List<Entity>> companions = new HashMap<>();
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity entity : w.getEntities()) {
               if (isCompanionPerk(entity)) {
//Logger.getGlobal().info("Parrot? "+e.getCustomName()+" " + e.getVehicle());
                   Data companionData = getCompanionData(entity);
                   if(companionData==null) {
                       entity.remove();
                       continue;
                   }
                   OfflinePlayer owner = Bukkit.getOfflinePlayer(companionData.getOwner());
                   if(owner.getPlayer()==null
                           || !PermissionData.isAllowed(owner.getPlayer(),this)
                           || owner.getPlayer().isFlying()) {
                      entity.remove();
                      continue;
                   }
                   List<Entity> entityList = companions.computeIfAbsent(owner.getUniqueId(), k -> new ArrayList<>());
                   entityList.add(entity);
                }
            }
        }
        for(List<Entity> entityList: companions.values()) {
            entityList.sort(Comparator.comparingLong(entity -> {
                Data data = getCompanionData(entity);
                if(data == null) {
                    return 0;
                } else {
                    return data.creationTime();
                }
            }));
            for(int i = 0; i < Math.max(0,entityList.size()-PerksPlugin.getPerkInt(perkName, "maxAllowedNumber",3)); i++) {
                entityList.get(i).remove();
            }
        }
        /*if(companions.containsKey(owner.getUniqueId())) {
            Entity otherCompanion = companions.get(owner.getUniqueId());
            Data otherCompanionData = getCompanionData(entity);
            if(otherCompanionData==null || otherCompanionData.getCreationTime()<companionData.getCreationTime()) {
                otherCompanion.remove();
            } else {
                entity.remove();
                continue;
            }
        }*/

    }
        
    public static boolean isCompanionPerk(Entity entity) {
        return entity.hasMetadata(companion_metadata_key);
    }

    public static Data getCompanionData(Entity entity) {
        Object metaData = entity.getMetadata(companion_metadata_key).get(0).value();
        if(metaData instanceof Data data) {
            return data;
        } else {
            return null;
        }
    }

    public static void allowSpawn (boolean allow) {
        allowSpawn = allow;
    }
    
    @Override
    public void disable() {
        clearCompanion();
    }
    
    @Override
    public void check() {
        checkCompanion();
    }

    public record Data(UUID owner, long creationTime) {

        public UUID getOwner() {
            return owner;
        }

        public long getCreationTime() {
            return creationTime;
        }
    }
    /*public static enum Status {
        ACTIVE, SITTING, STORED, UNLOADED, UNLOADED_SITTING;
    }

    public static class Data {
        private Location location;
        private Status status;

        public Data(Location location, Status status) {
            this.location = location;
            this.status = status;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }*/
}

    
    
    

