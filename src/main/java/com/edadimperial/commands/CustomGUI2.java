package com.edadimperial.commands;

import com.edadimperial.CustomHUD;
import com.edadimperial.utils.Shop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;


public class CustomGUI2 implements CommandExecutor, Listener {
    private static String invName = "";
    private static Plugin plugin = null;
    private static Economy money = null;
    private static String[] tradesAllowSymbols = {"\uC18D", "\uC18F", "\uC191", "\uC193", "\uC195", "\uC197"};
    private static String[] tradesDenySymbols = {"\uC18E", "\uC190", "\uC192", "\uC194", "\uC196", "\uC198"};

    private static ArrayList<Shop> shopItems = new ArrayList<>(){};
    private static Player currentPlayer = null;

    public CustomGUI2(Plugin instance, Economy economy) {
        plugin = instance;
        money = economy;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        currentPlayer = player;
        double playerBalance = money.getBalance(player);

        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("items");

        invName = ChatColor.WHITE + "\uF002\uC18B\uF003";

        int tradeIndex = 0;
        int i = 0;

        List<Shop> shopItems = new ArrayList<>();
        for (String key : section.getKeys(false)) {

            Material material = Material.getMaterial(config.getString("items." + key + ".material"));
            double price = config.getDouble("items." + key + ".price");

            Shop itemShop = new Shop(i,material,price);
            shopItems.add(itemShop);

            if (playerBalance >= price) {
                invName += "\uF004" + tradesAllowSymbols[tradeIndex];
            } else {
                invName += "\uF004" + tradesDenySymbols[tradeIndex];
            }

            System.out.println(shopItems);
            tradeIndex++;
            i++;

        }

        Inventory inventory = Bukkit.createInventory(player, 54, invName);


        int itemIndex = 1;
        for (int j = 0; j < shopItems.size(); j++) {
            ItemStack item = new ItemStack(shopItems.get(j).getItem());
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore;
            if (playerBalance >= shopItems.get(j).getPrice()) {
                lore = config.getStringList("messages.lore-allow");
            }
            else {
                lore = config.getStringList("messages.lore-deny");

            }
            lore.add("Precio: " + shopItems.get(j).getPrice());
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            inventory.setItem(itemIndex, item);
            itemIndex += 9;
        }

        player.openInventory(inventory);
        return true;
    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(invName)) {
            Material clickedItem = event.getCurrentItem().getType();

            for (Shop item : shopItems) {
                System.out.println(clickedItem.equals(item.getItem()));
                if (clickedItem.equals(item.getItem())) {
                    money.withdrawPlayer(currentPlayer, item.getPrice());
                    currentPlayer.sendMessage("Te quite dinero: " + item.getPrice());
                }
            }
            //event.setCancelled(true);
        }

    }
}