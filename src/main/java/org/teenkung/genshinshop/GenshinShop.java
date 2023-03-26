package org.teenkung.genshinshop;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.ItemsAdder;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.teenkung.genshinshop.EventListener.ItemsAdderLoadEvent;
import org.teenkung.genshinshop.manager.MySQLManager;
import org.teenkung.genshinshop.utils.ConfigLoader;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
public final class GenshinShop extends JavaPlugin {

    private static GenshinShop instance;
    private static MySQLManager SQLManager;

    @Override
    public void onEnable() {

        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        //Connect to MySQL database
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                SQLManager = new MySQLManager();
                SQLManager.Connect();
                SQLManager.createTable();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        Bukkit.getPluginManager().registerEvents(new ItemsAdderLoadEvent(), this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (ItemsAdder.getAllItems() != null) {
                System.out.println(colorize("&aItemsAdder already loaded it's data. Forcing to load the plugin. . ."));
                ConfigLoader.loadData();
            }
            /* Later use
             for (Player player : Bukkit.getOnlinePlayers()) {
            }*/
        }, 20);

    }

    @Override
    public void onDisable() {
        SQLManager.Disconnect();
    }

    public static GenshinShop getInstance() { return instance; }
    public static Connection getConnection() { return SQLManager.getConnection(); }


    public static String colorize(String text) {
        return IridiumColorAPI.process(text);
    }

    public static ArrayList<String> colorize(List<String> text) {
        ArrayList<String> list = new ArrayList<>();
        for (String s : text) {
            list.add(colorize(s));
        }
        return list;
    }

    public Inventory createGUI(File file) {
        FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
        return createGUI(yamlConfig);
    }
    public Inventory createGUI(FileConfiguration yamlConfig) {
        String title = yamlConfig.getString("Shop.GUIs.Title", "Invalid Title");
        List<String> layoutLines = yamlConfig.getStringList("Shop.GUIs.Layout");
        Map<Character, ItemStack> itemMap = new HashMap<>();
        ConfigurationSection itemsSection = yamlConfig.getConfigurationSection("Shop.GUIs.Items");
        if (itemsSection != null) {
            for (String key : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
                String itemType = itemSection != null ? itemSection.getString("Type", "Vanilla") : null;
                ItemStack item = null;
                if (itemType != null) {
                    switch (itemType) {
                        case "Vanilla" -> item = getVanillaItems(itemSection);
                        case "MMOItems" -> item = getMMOItemStack(itemSection);
                        case "ItemsAdder" -> item = getItemsAdderItemStack(itemSection);
                    }
                }

                if (item != null) {
                    itemMap.put(key.charAt(0), item);
                }
            }
        }

        Inventory shopGUI = Bukkit.createInventory(null, layoutLines.size() * 9, title);
        for (int row = 0; row < layoutLines.size(); row++) {
            char[] layoutRow = layoutLines.get(row).toCharArray();
            for (int col = 0; col < layoutRow.length; col++) {
                ItemStack item = itemMap.get(layoutRow[col]);
                if (item != null) {
                    shopGUI.setItem(row * 9 + col, item);
                }
            }
        }
        return shopGUI;
    }

    public ItemStack getVanillaItems(ConfigurationSection itemSection) {
        ItemStack item = new ItemStack(Material.valueOf(itemSection.getString("Material", "STONE")), itemSection.getInt("Amount"));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorize(itemSection.getString("Name", "")));
            meta.setLore(colorize(itemSection.getStringList("Lore")));
            if (meta.hasCustomModelData()) {
                meta.setCustomModelData(itemSection.getInt("ModelData", 0));
            }
            if (itemSection.getBoolean("Glowing", false)) {

                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getMMOItemStack(ConfigurationSection itemSection) {
        ItemStack item = MMOItems.plugin.getItem(itemSection.getString("Category"), itemSection.getString("ID"));
        if (item != null) {
            item.setAmount(itemSection.getInt("Amount"));
        } else {
            item = new ItemStack(Material.STONE);
        }
        return item;
    }

    public ItemStack getMMOItemStack(String category, String id) {
        ItemStack item = MMOItems.plugin.getItem(category, id);
        return Objects.requireNonNullElseGet(item, () -> new ItemStack(Material.STONE));
    }

    public ItemStack getItemsAdderItemStack(String itemID) {
        CustomStack custom = CustomStack.getInstance(itemID);
        if (custom != null) {
            return custom.getItemStack();
        } else {
            return new ItemStack(Material.STONE);
        }
    }

    public static ItemStack getItemsAdderItemStack(ConfigurationSection itemSection) {
        CustomStack custom = CustomStack.getInstance(itemSection.getString("ID"));
        if (custom != null) {
            ItemStack item = custom.getItemStack();
            item.setAmount(itemSection.getInt("Amount"));
            return item;
        } else {
            return new ItemStack(Material.STONE);
        }
    }
}
