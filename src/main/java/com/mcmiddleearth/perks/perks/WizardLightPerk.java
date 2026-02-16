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
import com.mcmiddleearth.perks.commands.LightHandler;
import com.mcmiddleearth.perks.listeners.LightListener;
import com.mcmiddleearth.perks.permissions.PermissionData;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eriol_Eandur
 */
public class WizardLightPerk extends Perk {

    private static double speedFactor = PerksPlugin.getPerkDouble("light", "speedFactor", 0.1);
    private static double maxSpeed = PerksPlugin.getPerkDouble("light", "maxSpeed", 1);
    private final static int startDistance = PerksPlugin.getPerkInt("light", "startDistance", 6);
    private final static int maxIntensity = PerksPlugin.getPerkInt("light", "maxIntensity", 1);
    private final static int maxDuration = PerksPlugin.getPerkInt("light", "maxDuration", 12000); //ticks
    private final static int teleportDistance = PerksPlugin.getPerkInt("light", "teleportDistance", 50);
    private static final double goldenRatio = (Math.sqrt(5) + 1) / 2;

    private static final Material item = Material.valueOf(PerksPlugin.getPerkString("light", "item", "STICK"));
    private static final Map<Player, WizardLight> lights = new HashMap<>();
    private static BukkitTask lightController;

    public WizardLightPerk() {
        super("light");
        setCommandHandler(new LightHandler(this, Permissions.USER.getPermissionNode()), "light");
        setListener(new LightListener());
    }

    public static void summonLight(final Player player) {
        WizardLight light = new WizardLight(player.getEyeLocation().toVector()
                .add(player.getLocation().getDirection())
                .toLocation(player.getWorld()));
        lights.put(player, light);
        new BukkitRunnable() {
            @Override
            public void run() {
                unsummonLight(player);
            }
        }.runTaskLater(PerksPlugin.getInstance(), maxDuration);
    }

    public static void unsummonLight(Player player) {
        WizardLight light = lights.get(player);
        if (light != null) {
            light.unsummon();
            lights.remove(player);
        }
    }

    public static boolean hasLight(Player player) {
        return lights.containsKey(player);
    }

    public static void increaseDistance(Player player) {
        if (lights.containsKey(player)) {
            WizardLight light = lights.get(player);
            light.setDistance(light.getDistance() + 1);
        }
    }

    public static void decreaseDistance(Player player) {
        if (lights.containsKey(player)) {
            WizardLight light = lights.get(player);
            light.setDistance(Math.max(light.getDistance() - 1, 2));
        }
    }

    public static void setIntensity(Player player, int intensity) {
        if (lights.containsKey(player)) {
            WizardLight light = lights.get(player);
            light.setIntensity(intensity);
        }
    }

    @Override
    public void disable() {
        for (Player player : lights.keySet()) {
            unsummonLight(player);
        }
        if (lightController != null) {
            lightController.cancel();
        }
        lightController = null;
    }

    @Override
    public void enable() {
        lightController = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : lights.keySet()) {
                    WizardLight light = lights.get(player);
                    if (light.isValid()) {
                        light.refresh();
                        Vector aim = player.getEyeLocation().toVector().add(new Vector(0, 1, 0));
                        Vector direction = player.getLocation().getDirection();
                        for (int i = 0; i < light.getDistance(); i++) {
                            Vector newAim = aim.clone().add(direction);
                            if (!newAim.toLocation(player.getWorld()).getBlock().isEmpty()) {
                                break;
                            }
                            aim = newAim;
                        }
                        light.setAim(aim);
                    } else {
                        unsummonLight(player);
                    }
                }
            }
        }.runTaskTimer(PerksPlugin.getInstance(), 1, 5);
    }

    @Override
    public void check() {
        for (Player player : lights.keySet()) {
            if (!PermissionData.isAllowed(player, this)) {
                unsummonLight(player);
            }
        }
    }

    @Override
    public void writeDefaultConfig(ConfigurationSection config) {
        config.set("startDistance", startDistance);
        config.set("teleportDistance", teleportDistance);
        config.set("maxDuration", maxDuration);
        config.set("maxIntensity", maxIntensity);
        config.set("maxSpeed", maxSpeed);
        config.set("speedFactor", speedFactor);
        config.set("item", item.name());
    }

    private static class WizardLight {

        private int distance = startDistance;
        private int intensity = 1;

        private List<Location> blockChanges = new ArrayList<>();
        private List<Vector> relativeLocations = new ArrayList<>();
        private List<Player> players = new ArrayList<>();
        private Location aim;

        private final Map<Entity, Vector> entities;

        public WizardLight(Location loc) {
            entities = new HashMap<>();
            this.aim = loc;
            summonEntity(loc, 0, 0, 0, Material.BEACON);
        }

        private void summonEntity(Location loc, double x, double y, double z, Material mat) {
            Vector relativeLoc = new Vector(x, y, z);
            Location locC = loc.clone();
            entities.put(spawnItem(locC.add(relativeLoc), mat), relativeLoc);
        }

        private Entity spawnItem(Location loc, Material mat) {
            Item entity = loc.getWorld().dropItemNaturally(loc, new ItemStack(mat));
            entity.setItemStack(new ItemStack(mat));
            entity.setPickupDelay(600 * 20);
            entity.setSilent(true);
            entity.setGravity(false);
            return entity;
        }

        private Entity spawnMob(Location loc, Material mat) {
            Entity entity = loc.getWorld().spawn(loc, Bat.class);
            entity.setSilent(true);
            entity.setGravity(false);
            entity.setFireTicks(Integer.MAX_VALUE);
            entity.setInvulnerable(true);
            return entity;
        }

        public void unsummon() {
            for (Entity entity : entities.keySet()) {
                entity.remove();
            }
            entities.clear();
        }

        private void clearBlockChanges() {
            for (Location loc : blockChanges) {
                for (Player player : players) {
                    if (player != null && player.isOnline()) {
                        player.sendBlockChange(loc, loc.getBlock().getBlockData());
                    }
                }
            }
            players.clear();
            blockChanges.clear();
        }

        public void update() {
            clearBlockChanges();
            List<Player> all = aim.getWorld().getPlayers();
            for (Player player : all) {
                if (player.getLocation().distance(aim) < 250) {
                    players.add(player);
                }
            }
            Location current = entities.keySet().iterator().next().getLocation();
            for (Vector relLoc : relativeLocations) {
                blockChanges.add(current.clone().add(relLoc));
            }
            for (Location loc : blockChanges) {
                for (Player player : players) {
                    if (player != null && player.isOnline()) {
                        player.sendBlockChange(loc, Bukkit.createBlockData(Material.BEACON));
                    }
                }
            }
        }

        public void setAim(Vector aim) {
            this.aim = aim.toLocation(this.aim.getWorld());
            for (Entity entity : entities.keySet()) {
                Vector temp = aim.clone();
                double dist = temp.subtract(entity.getLocation().toVector()).length();
                if (dist > teleportDistance) {
                    entity.teleport(this.aim.toVector().add(entities.get(entity)).toLocation(this.aim.getWorld()));
                } else {
                    temp = aim.clone();
                    Vector vel = temp.add(entities.get(entity)).subtract(entity.getLocation().toVector());
                    vel = vel.multiply(speedFactor);
                    double length = vel.length();
                    entity.setVelocity((vel.length() < Math.min(maxSpeed, 1) ? vel
                            : vel.normalize().multiply(Math.min(maxSpeed, 1))));
                }
            }
        }

        public void setDistance(int distance) {
            if (distance < 0) {
                distance = 0;
            }
            if (distance > 100) {
                distance = 100;
            }
            this.distance = distance;
        }

        public void setIntensity(int intensity) {
            if (intensity > maxIntensity) {
                return;
            }
            if (this.intensity != intensity) {
                unsummon();
                intensity = (intensity <= 10 ? intensity : 10);
                this.intensity = intensity;
                summonEntity(aim, 0, 0, 0, Material.BEACON);
                if (intensity > 1) {
                    if (intensity < 4) {
                        createIkosaeder(intensity);
                    } else if (intensity < 6) {
                        createIkosaeder(intensity);
                        createDodecaeder(intensity);
                    } else if (intensity < 8) {
                        createIkosaeder(intensity / 2);
                        createIkosaeder(intensity);
                        createDodecaeder(intensity);
                    } else {
                        createIkosaeder(intensity / 2);
                        createIkosaeder(intensity);
                        createDodecaeder(intensity);
                    }
                }

            }
        }

        private void createDodecaederLoc(double size) {
            double f = 2.6;
            double a = size / 2;
            double b = a * goldenRatio;
            double c = (a + 2 * b) / f;
            double d = b / f;
            double e = (a + b) / f;

            relativeLocations.add(new Vector(0, c, d));
            relativeLocations.add(new Vector(0, c, -d));
            relativeLocations.add(new Vector(0, -c, d));
            relativeLocations.add(new Vector(0, -c, -d));

            relativeLocations.add(new Vector(-d, 0, c));
            relativeLocations.add(new Vector(d, 0, c));
            relativeLocations.add(new Vector(-d, 0, -c));
            relativeLocations.add(new Vector(d, 0, -c));

            relativeLocations.add(new Vector(c, -d, 0));
            relativeLocations.add(new Vector(c, d, 0));
            relativeLocations.add(new Vector(-c, -d, 0));
            relativeLocations.add(new Vector(-c, d, 0));

            relativeLocations.add(new Vector(e, e, e));
            relativeLocations.add(new Vector(e, e, -e));
            relativeLocations.add(new Vector(e, -e, e));
            relativeLocations.add(new Vector(-e, e, e));
            relativeLocations.add(new Vector(e, -e, -e));
            relativeLocations.add(new Vector(-e, -e, e));
            relativeLocations.add(new Vector(-e, e, -e));
            relativeLocations.add(new Vector(-e, -e, -e));
        }

        private void createIkosaederLoc(double size) {
            double a = size / 2;
            double b = a * goldenRatio;

            relativeLocations.add(new Vector(-a, -b, 0));
            relativeLocations.add(new Vector(a, -b, 0));
            relativeLocations.add(new Vector(-a, b, 0));
            relativeLocations.add(new Vector(a, b, 0));

            relativeLocations.add(new Vector(0, -a, b));
            relativeLocations.add(new Vector(0, a, b));
            relativeLocations.add(new Vector(0, -a, -b));
            relativeLocations.add(new Vector(0, a, -b));

            relativeLocations.add(new Vector(-b, 0, -a));
            relativeLocations.add(new Vector(-b, 0, a));
            relativeLocations.add(new Vector(b, 0, -a));
            relativeLocations.add(new Vector(b, 0, a));
        }

        private void createDodecaeder(double size) {
            double f = 2.6;
            double a = size / 2;
            double b = a * goldenRatio;
            double c = (a + 2 * b) / f;
            double d = b / f;
            double e = (a + b) / f;

            summonEntity(aim, 0, c, d, Material.SEA_LANTERN);
            summonEntity(aim, 0, c, -d, Material.SEA_LANTERN);
            summonEntity(aim, 0, -c, d, Material.SEA_LANTERN);
            summonEntity(aim, 0, -c, -d, Material.SEA_LANTERN);

            summonEntity(aim, -d, 0, c, Material.SEA_LANTERN);
            summonEntity(aim, d, 0, c, Material.SEA_LANTERN);
            summonEntity(aim, -d, 0, -c, Material.SEA_LANTERN);
            summonEntity(aim, d, 0, -c, Material.SEA_LANTERN);

            summonEntity(aim, c, -d, 0, Material.SEA_LANTERN);
            summonEntity(aim, c, d, 0, Material.SEA_LANTERN);
            summonEntity(aim, -c, -d, 0, Material.SEA_LANTERN);
            summonEntity(aim, -c, d, 0, Material.SEA_LANTERN);

            summonEntity(aim, e, e, e, Material.SEA_LANTERN);
            summonEntity(aim, e, e, -e, Material.SEA_LANTERN);
            summonEntity(aim, e, -e, e, Material.SEA_LANTERN);
            summonEntity(aim, -e, e, e, Material.SEA_LANTERN);
            summonEntity(aim, e, -e, -e, Material.SEA_LANTERN);
            summonEntity(aim, -e, -e, e, Material.SEA_LANTERN);
            summonEntity(aim, -e, e, -e, Material.SEA_LANTERN);
            summonEntity(aim, -e, -e, -e, Material.SEA_LANTERN);
        }

        private void createIkosaeder(double size) {
            double a = size / 2;
            double b = a * goldenRatio;

            summonEntity(aim, -a, -b, 0, Material.SEA_LANTERN);
            summonEntity(aim, a, -b, 0, Material.SEA_LANTERN);
            summonEntity(aim, -a, b, 0, Material.SEA_LANTERN);
            summonEntity(aim, a, b, 0, Material.SEA_LANTERN);

            summonEntity(aim, 0, -a, b, Material.SEA_LANTERN);
            summonEntity(aim, 0, a, b, Material.SEA_LANTERN);
            summonEntity(aim, 0, -a, -b, Material.SEA_LANTERN);
            summonEntity(aim, 0, a, -b, Material.SEA_LANTERN);

            summonEntity(aim, -b, 0, -a, Material.SEA_LANTERN);
            summonEntity(aim, -b, 0, a, Material.SEA_LANTERN);
            summonEntity(aim, b, 0, -a, Material.SEA_LANTERN);
            summonEntity(aim, b, 0, a, Material.SEA_LANTERN);
        }

        public boolean isValid() {
            for (Entity entity : entities.keySet()) {
                if (entity.isValid()) {
                    return true;
                }
            }
            return false;
        }

        public void refresh() {
            List<Entity> notValid = new ArrayList<>();
            for (Entity entity : entities.keySet()) {
                if (entity.isValid()) {
                    entity.setTicksLived(1);
                } else {
                    notValid.add(entity);
                }
            }
            for (Entity entity : notValid) {
                entities.remove(entity);
            }
        }

        public int getDistance() {
            return distance;
        }
    }

    public static void setSpeedFactor(double speedFactor) {
        WizardLightPerk.speedFactor = speedFactor;
    }

    public static void setMaxSpeed(double maxSpeed) {
        WizardLightPerk.maxSpeed = maxSpeed;
    }

    public static int getMaxIntensity() {
        return maxIntensity;
    }

    public static Material getItem() {
        return item;
    }

    public static Map<Player, WizardLight> getLights() {
        return lights;
    }
}
    