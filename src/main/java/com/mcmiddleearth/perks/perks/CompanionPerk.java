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

/**
 *
 * @author Fraspace5, Eriol_Eandur
 */
public class CompanionPerk extends Perk {

    private static Perk instance;

    private static final String companion_metadata_key = "MCME_Perks_Companion";

    private static boolean allowSpawn;

    private static final String perkName = "pet";

    public CompanionPerk() {
        super(perkName);
        setListener(new CompanionListener());
        setCommandHandler(new CompanionHandler(this, Permissions.USER.getPermissionNode()),perkName);
    }

    public static void spawnDog(Player owner, String name, DyeColor collarColor, Wolf.Variant variant) {
        Location location = owner.getLocation();
        Wolf companion = location.getWorld().spawn(location, Wolf.class);
        companion.setVariant(variant);
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
    }
        
    public static boolean isCompanionPerk(Entity entity) {
        return entity.hasMetadata(companion_metadata_key);
    }

    public static Data getCompanionData(Entity entity) {
        var metadata = entity.getMetadata(companion_metadata_key);
        if(metadata.isEmpty()) {
            return null;
        }
        Object metaData = metadata.get(0).value();
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
}

