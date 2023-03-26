package org.teenkung.genshinshop.EventListener;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.Listener;
import org.teenkung.genshinshop.utils.ConfigLoader;

import static org.teenkung.genshinshop.GenshinShop.colorize;
@SuppressWarnings("unused")
public class ItemsAdderLoadEvent implements Listener {

    public void onItemsAdderLoad(ItemsAdderLoadDataEvent event) {
        System.out.println(colorize("&aItemsAdder completed loaded data reloading the plugin. . ."));
        ConfigLoader.reloadData();
    }

}
