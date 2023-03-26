package org.teenkung.genshinshop.manager.shop;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.teenkung.genshinshop.GenshinShop;
import org.teenkung.genshinshop.utils.MMOStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ShopDataManager {

    private final String id;
    private final String fileName;
    private final YamlConfiguration config;
    private final ArrayList<Objects> items = new ArrayList<>();
    private final ArrayList<Integer> shopSlots = new ArrayList<>();
    private final ArrayList<Integer> nextPageSlots = new ArrayList<>();
    private final ArrayList<Integer> previousPageSlots = new ArrayList<>();

    public ShopDataManager(String fileName) {

        this.fileName = fileName;
        this.config = YamlConfiguration.loadConfiguration(new File(GenshinShop.getInstance().getDataFolder(), fileName));
        this.id = config.getString("Shop.ID");
    }

    public ArrayList<Integer> getNextPageSlots() {
        return nextPageSlots;
    }

    public ArrayList<Integer> getPreviousPageSlots() {
        return previousPageSlots;
    }

    public ArrayList<Integer> getShopSlots() {
        return shopSlots;
    }

    public String getFileName() {
        return fileName;
    }

    public CompletableFuture<Inventory> getInventory(int targetPage) {
        CompletableFuture<Inventory> future = new CompletableFuture<>();
        final int tempPage = targetPage;
        Bukkit.getScheduler().runTaskAsynchronously(GenshinShop.getInstance(), () -> {
            int page = tempPage;
            Inventory inventory = GenshinShop.getInstance().createGUI(config);
            int startAt = (page - 1) * shopSlots.size();
            int numItems = shopSlots.size();

            if (startAt >= numItems) {
                page = (numItems / shopSlots.size()) + 1;
                startAt = (page - 1) * shopSlots.size();
            }
            for (Object slot : shopSlots) {
                ItemStack stack = new ItemStack(Material.STONE);
                if (slot instanceof ItemStack) {
                    stack = (ItemStack) slot;
                } else if (slot instanceof MMOStorage) {
                    stack = ((MMOStorage) slot).getItemStack();
                }
                NBTItem nbt = new NBTItem(stack);
                nbt.setString("GS_ID", id);
                nbt.setInteger("GS_Page", page);
                inventory.setItem(startAt, stack);
                startAt++;
            }
            future.complete(inventory);
        });
        return future;
    }

}
