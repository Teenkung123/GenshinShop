package org.teenkung.genshinshop.manager.shop;

import java.util.HashMap;

@SuppressWarnings("unused")
public class ShopManager {

    private static final HashMap<String, ShopDataManager> manager = new HashMap<>();


    public static void addShop(String id) {
        manager.put(id, new ShopDataManager(id));
    }

    public static ShopDataManager getShop(String id) {
        return manager.get(id);
    }

    public static void removeShop(String id) {
        manager.remove(id);
    }

    public static HashMap<String, ShopDataManager> getManager() {
        return manager;
    }

}
