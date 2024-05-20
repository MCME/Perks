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
package com.mcmiddleearth.perks.perks;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.listeners.SitListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public class SitPerk extends Perk {
    
    private static final Map<Player, ArmorStand> armorStands = new HashMap<>();
    
    private static YamlConfiguration config = new YamlConfiguration();
    private static final File configFile = new File(PerksPlugin.getInstance().getDataFolder(),
                                                           "SitPerkConfiguration.yml");
    
    private static Material item;
    
    public SitPerk() {
        super("sit");
        try {
            config.load(configFile);
        } catch (IOException ex) {
            Logger.getLogger(SitPerk.class.getName()).log(Level.INFO, "SitPerkConfiguration.yml not found!");
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(SitPerk.class.getName()).log(Level.INFO, "SitPerkConfiguration.yml not valid!");
        }
        item = Material.valueOf(PerksPlugin.getPerkString(this.getName(),"item","GHAST_TEAR"));
        setListener(new SitListener());
    }
    
    public static void sitDown(Player player, Block clicked) {
        double xshift = 0.5;
        double zshift = 0.5;
        double yshift = -0.75;
        xshift = xshift + (hasSpecialXAdjust(clicked)?getSpecialXAdjust(clicked):getXBlockAdjust(clicked));
        yshift = yshift + (hasSpecialYAdjust(clicked)?getSpecialYAdjust(clicked):getYBlockAdjust(clicked));
        zshift = zshift + (hasSpecialZAdjust(clicked)?getSpecialZAdjust(clicked):getZBlockAdjust(clicked));
        Location pLoc = player.getLocation();
        Float yaw = (hasSpecialYaw(clicked)?getSpecialYaw(clicked):getTurnAround(pLoc,clicked));
        Location turnedAround = new Location(pLoc.getWorld(), pLoc.getX(), pLoc.getY(),pLoc.getZ(),
                                             yaw, pLoc.getPitch());
        Location loc = new Location(clicked.getWorld(),
                                    clicked.getX()+xshift,
                                    clicked.getY()+yshift,
                                    clicked.getZ()+zshift,
                                    turnedAround.getYaw(), 0);
        ArmorStand armor = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        player.teleport(turnedAround);
        armor.setGravity(false);
        armor.setVisible(false);
        armor.setPassenger(player);
        armorStands.put(player,armor);
        armor.setCustomName("Seat_Marker_remove_if found_without_a_player_mounted!");
    }
    
    public static void sitUp(final Player player) {
        ArmorStand marker = armorStands.get(player);
        if(marker!=null) {
            marker.remove();
            armorStands.remove(player);
            player.teleport(player.getLocation().getBlock()
                          .getRelative(BlockFace.UP, 1).getLocation());
        }
    }
    
    @Override
    public void disable() {
        for(Player player: armorStands.keySet()) {
            sitUp(player);
        }
    }

    @Override
    public void check() {
        for(Player player:armorStands.keySet()) {
            if(!PermissionData.isAllowed(player, this)) {
                sitUp(player);
            }
        }
    }
    
    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        config.set("item",item.name());
    }
    
    private static Float getTurnAround(Location pLoc, Block clicked) {
        if(isStairBlock(clicked)) {
            return(getStairYaw(clicked));
        } else {
            return pLoc.getYaw()+180;
        }
    }
    
    private static double getXBlockAdjust(Block clicked) {
        if(isStairBlock(clicked)) {
            return stairShiftX(clicked);
        }
        return 0;
    }
    
    private static double getZBlockAdjust(Block clicked) {
        if(isStairBlock(clicked)) {
            return stairShiftZ(clicked);
        }
        return 0;
    }
    
    private static double getYBlockAdjust(Block clicked) {
        if(clicked.getType().equals(Material.SNOW)) {
            return -0.875+(1/8.0)*clicked.getData();
        }
        if(isHalfBlock(clicked) || isStairBlock(clicked)) {
            return -0.44;
        }
        if(isQuarterBlock(clicked)) {
            return -0.68;
        }
        if(isCarpet(clicked)) {
            return -0.9;
        }
        if(isUnsolid(clicked)) {
            return -1;
        }
        if(isThreeQuarterBlock(clicked)){
            return -0.22;
        }
        return 0;
    }
    
    private static boolean isUnsolid(Block clicked) {
        String name = clicked.getType().name();
        if(name.contains("SAPLING")
                || name.contains("SAPLING")
                || name.contains("WALL_BANNER")
                || name.contains("BUTTON")) {
            return true;
        }
        switch(clicked.getType()) {
            case GRASS:
            case RED_DYE:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case WHEAT:
            case POTATO:
            case CARROT:
            case REDSTONE_WIRE:
            case TRIPWIRE_HOOK:
            case NETHER_WART:
            case MELON_STEM:
            case PUMPKIN_STEM:
            case STRING:
            case FLOWER_POT:
            case ACACIA_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case OAK_WALL_SIGN:
            case SPRUCE_WALL_SIGN:
            case COBWEB:
            case DEAD_BUSH:
            case TORCH:
            case REDSTONE_TORCH:
            case LEVER:
            case VINE:
            case TALL_GRASS:
            case LADDER:
            case RAIL:
            case POWERED_RAIL:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case CAKE:
            case BEETROOT:
                return true;
        }
        return false;
    }
    
    private static boolean isThreeQuarterBlock(Block clicked) {
        String name = clicked.getType().name();
        if(name.contains("WALL_HEAD")) {
            return true;
        }
        switch(clicked.getType()) {
            case ENCHANTING_TABLE:
            case END_PORTAL_FRAME:
                return true;
        }
        return false;
    }
    
    private static boolean isCarpet(Block clicked) {
        String name = clicked.getType().name();
        if(name.contains("CARPET")
                || name.contains("PLATE")
                || name.contains("WALL_BANNER")
                || name.contains("BUTTON")) {
            return true;
        }
        switch(clicked.getType()) {
            case REPEATER:
            case COMPARATOR:
                return true;
        }
        return false;
    }
    
    private static boolean isQuarterBlock(Block clicked) {
        switch(clicked.getType()) {
            case DAYLIGHT_DETECTOR:
                return true;
        }
        return false;
    }
        
    private static boolean isHalfBlock(Block clicked) {
        String name = clicked.getType().name();
        if((name.contains("HEAD") && !name.contains("WALL_HEAD"))
                || name.contains("BED")) {
            return true;
        }
        BlockData data = clicked.getBlockData();
        if(((data instanceof Cake) && (((Cake)data).getBites()<5))
            || (data instanceof Bisected) && (((Bisected)data).getHalf().equals(Bisected.Half.BOTTOM))) {
            return true;
        }
        return false;
    }
    private static boolean isStairBlock(Block clicked) {
        BlockData data = clicked.getBlockData();
        return (data instanceof Stairs) 
                && ((Stairs)data).getHalf().equals(Bisected.Half.BOTTOM);
        }

    private static float getStairYaw(Block clicked) {
        if(isStairBlock(clicked)) {
            Stairs data = (Stairs) clicked.getBlockData();
            switch(data.getFacing()) {
                case NORTH:
                    return 0;
                case EAST:
                    return 90;
                case SOUTH:
                    return 180;
                case WEST:
                    return 270;
            }
        }
        return 0;
    }
    
    private static double stairShiftX(Block clicked) {
        if(isStairBlock(clicked)) {
            Stairs data = (Stairs) clicked.getBlockData();
            switch(data.getFacing()) {
                case EAST:
                    return -0.1;
                case WEST:
                    return 0.1;
            }
        }
        return 0;
    }
    private static double stairShiftZ(Block clicked) {
        if(isStairBlock(clicked)) {
            Stairs data = (Stairs) clicked.getBlockData();
            switch(data.getFacing()) {
                case SOUTH:
                    return -0.1;
                case NORTH:
                    return 0.1;
            }
        }
        return 0;
    }
    
    private static ConfigurationSection getConfigSection(Block clicked) {
        String data = clicked.getBlockData()
                            .getAsString();
                            /*.replace(":", "_")
                            .replace("[","_")
                            .replace("]","")
                            .replace("=","");*/
        ConfigurationSection section = config.getConfigurationSection(data);
        return section;
    }
    
    private static boolean hasSpecialValue(Block clicked, String key) {
        ConfigurationSection section = getConfigSection(clicked);
        if(section != null) {
            return section.contains(key);
        }
        return false;
    }
    
    private static boolean hasSpecialYaw(Block clicked) {
        return hasSpecialValue(clicked, "Yaw");
    }
    
    private static Float getSpecialYaw(Block clicked) {
        ConfigurationSection section = getConfigSection(clicked);
        return Float.parseFloat(section.getString("Yaw"));
    }
    
    private static boolean hasSpecialXAdjust(Block clicked) {
        return hasSpecialValue(clicked, "X");
    }
    
    private static Double getSpecialXAdjust(Block clicked) {
        ConfigurationSection section = getConfigSection(clicked);
        return section.getDouble("X");
    }
    
    private static boolean hasSpecialYAdjust(Block clicked) {
        return hasSpecialValue(clicked, "Y");
    }
    
    private static Double getSpecialYAdjust(Block clicked) {
        ConfigurationSection section = getConfigSection(clicked);
        return section.getDouble("Y");
    }
    
    private static boolean hasSpecialZAdjust(Block clicked) {
        return hasSpecialValue(clicked, "Z");
    }
    
    private static Double getSpecialZAdjust(Block clicked) {
        ConfigurationSection section = getConfigSection(clicked);
        return section.getDouble("Z");
    }

    public static Material getItem() {
        return item;
    }
}
    