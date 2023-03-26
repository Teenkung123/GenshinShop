package org.teenkung.genshinshop.utils;

import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class MMOStorage {

    private final String Category;
    private final String ID;

    public MMOStorage(String Category, String ID) {
        this.Category = Category;
        this.ID = ID;
    }

    public String getCategory() {
        return Category;
    }

    public String getID() {
        return ID;
    }

    public ItemStack getItemStack() {
        return MMOItems.plugin.getItem(Category, ID);
    }

}
