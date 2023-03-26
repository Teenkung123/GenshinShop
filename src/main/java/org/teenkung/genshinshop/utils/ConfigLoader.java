package org.teenkung.genshinshop.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.teenkung.genshinshop.GenshinShop;
import org.teenkung.genshinshop.manager.shop.ShopManager;

public class ConfigLoader {

    public static void reloadData() {
        GenshinShop instance = GenshinShop.getInstance();
        instance.reloadConfig();
        instance.saveDefaultConfig();
        instance.reloadConfig();
        loadData();
    }
    public static void loadData() {
        GenshinShop instance = GenshinShop.getInstance();
        ConfigurationSection section = instance.getConfig().getConfigurationSection("shops");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ShopManager.addShop(key);
            }
        }
    }

}
