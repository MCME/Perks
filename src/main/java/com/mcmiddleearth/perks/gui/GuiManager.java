package com.mcmiddleearth.perks.gui;

import com.mcmiddleearth.perks.PerkManager;
import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.perks.EquipmentPerk;
import com.mcmiddleearth.perks.perks.Perk;
import com.mcmiddleearth.perks.permissions.PermissionData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GuiManager implements Listener {

    private static final Map<UUID, PerkGui> openGuis = new HashMap<>();

    private static final File guiItemFile = new File(PerksPlugin.getInstance().getDataFolder(),"guiItems.yml");

    private static final YamlConfiguration guiItemConfig = new YamlConfiguration();

    private static final File favoritesFile = new File(PerksPlugin.getInstance().getDataFolder(), "favorites.yml");

    private static final YamlConfiguration favoritesConfig = new YamlConfiguration();

    //private static final Map<UUID, Set<Perk>> favorites = new HashMap<>();

    public GuiManager() {
        try {
            guiItemConfig.load(guiItemFile);
            if(!favoritesFile.exists()) {
                favoritesFile.createNewFile();
            }
            favoritesConfig.load(favoritesFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GuiItem> getFavoritePerks(Player player) {
        if(favoritesConfig.contains(player.getUniqueId().toString())) {
            List<String> playerFavorites = favoritesConfig.getStringList(player.getUniqueId().toString());
            return PerkManager.getPerks().stream().filter(perk -> playerFavorites.contains(perk.getName()))
                    .map(Perk::getFavoriteGuiItem).toList();
        }
        return new ArrayList<>();
    }

    public static List<GuiItem> getHats(Player player) {
        return PerkManager.getPerks().stream().filter(perk -> perk instanceof EquipmentPerk && PermissionData.isAllowed(player,perk))
                .map(Perk::getGuiItem).toList();
    }

    public static boolean addFavorite(Player player, String perkName) {
        List<String> favorites = favoritesConfig.getStringList(player.getUniqueId().toString());
        Perk perk = PerkManager.forName(perkName);
//Logger.getGlobal().info("Trying to add: "+perkName);
//favorites.forEach(name -> Logger.getGlobal().info(name));
        if(PermissionData.isAllowed(player, perk) && !favorites.contains(perk.getName())) {
//Logger.getGlobal().info("adding Favorite: "+perkName);
            favorites.add(perk.getName());
            favoritesConfig.set(player.getUniqueId().toString(), favorites);
            saveFavoriteConfig();
            return true;
        }
        return false;
    }

    public static boolean removeFavorite(Player player, String perkName) {
        List<String> favorites = favoritesConfig.getStringList(player.getUniqueId().toString());
//Logger.getGlobal().info("Trying to remove: "+perkName);
//favorites.forEach(name -> Logger.getGlobal().info(name));
        if(favorites.contains(perkName)) {
//Logger.getGlobal().info("removing Favorite: "+perkName);
            favorites.remove(perkName);
            favoritesConfig.set(player.getUniqueId().toString(), favorites);
            saveFavoriteConfig();
            return true;
        }
        return false;
    }

    private static void saveFavoriteConfig() {
        try {
            favoritesConfig.save(favoritesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<GuiItem> getAllPerks() {
        return PerkManager.getPerks().stream().filter(perk -> ! (perk instanceof  EquipmentPerk)).map(Perk::getGuiItem).toList();
    }

    public static void updateGui(Player player) {
        PerkGui gui = openGuis.get(player.getUniqueId());
        gui.update();
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
//Logger.getGlobal().info("ClickEvent");
        PerkGui gui = openGuis.get(event.getWhoClicked().getUniqueId());
        if(gui!=null && event.getWhoClicked() instanceof Player player) {
//Logger.getGlobal().info("Handle");
            event.setCancelled(gui.handleClick(player, event.getRawSlot(), event.getClick()));
        }
    }

    @EventHandler
    public void onGuiClose(InventoryCloseEvent event) {
        removeGui(event.getPlayer().getUniqueId());
    }

    private static void removeGui(UUID player) {
        openGuis.remove(player);
    }

    public static void openGui(Player player) {
        PerkGui gui = new PerkGui(player);
        openGuis.put(player.getUniqueId(), gui);
    }

    public static void closeGui(Inventory inventory) {
        UUID removal = null;
        PerkGui gui = null;
        for(Map.Entry<UUID, PerkGui> entry: openGuis.entrySet()) {
            if(entry.getValue().hasInventory(inventory)) {
                removal = entry.getKey();
                gui = entry.getValue();
                break;
            }
        }
        if(removal!=null) {
            removeGui(removal);
            Bukkit.getScheduler().runTask(PerksPlugin.getInstance(), gui::close);
        }
    }

    public static GuiItem getGuiItem(String key) {
        return GuiItem.load(guiItemConfig.getConfigurationSection(key));
    }
}
