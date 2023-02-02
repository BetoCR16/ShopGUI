package com.edadimperial.commands;

import com.edadimperial.CustomHUD;
import com.edadimperial.utils.Shop;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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

import static org.bukkit.Bukkit.getServer;


public class CustomGUI2 implements CommandExecutor, Listener {
    private static String invName = ChatColor.WHITE + "\uF002\uC18B\uF003";
    private static Plugin plugin = null;
    private static Economy money = null;
    private static String[] tradesAllowSymbols = {"\uC18D", "\uC18F", "\uC191", "\uC193", "\uC195", "\uC197"};
    private static String[] tradesDenySymbols = {"\uC18E", "\uC190", "\uC192", "\uC194", "\uC196", "\uC198"};

    private static ArrayList<Shop> shopItems = new ArrayList<>(){};
    private static Player currentPlayer = null;
    static FileConfiguration config = null;
    static ConfigurationSection section = null;

    public CustomGUI2(Plugin instance, Economy economy) {
        plugin = instance;
        money = economy;
        config = plugin.getConfig();
        section = config.getConfigurationSection("items");

        int i = 0;
        for (String key : section.getKeys(false)) {
            Material material = Material.getMaterial(config.getString("items." + key + ".material"));
            double price = config.getDouble("items." + key + ".price");

            Shop itemShop = new Shop(i,material,price);
            shopItems.add(itemShop);
        }
        getServer().getPluginManager().registerEvents(this,plugin);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        currentPlayer = player;

        double playerBalance = money.getBalance(player);

        int tradeIndex = 0;
        for (String key : section.getKeys(false)) {

            double price = config.getDouble("items." + key + ".price");

            if (playerBalance >= price) {
                invName += "\uF004" + tradesAllowSymbols[tradeIndex];
            } else {
                invName += "\uF004" + tradesDenySymbols[tradeIndex];
            }

            tradeIndex++;
        }

        Inventory inventory = Bukkit.createInventory(player, 54, invName);

        int itemIndex = 1;
        for (int j = 0; j < shopItems.size(); j++) {
            ItemStack item = new ItemStack(shopItems.get(j).getItem());
            ItemMeta itemMeta = item.getItemMeta();
            List<String> lore = new ArrayList<String>();
            if (playerBalance >= shopItems.get(j).getPrice()) {
                List<String> loreList = config.getStringList("messages.lore-allow");
                for (String l : loreList) {
                    lore.add(ChatColor.translateAlternateColorCodes('&',l));
                }
            }
            else {
                List<String> loreList = config.getStringList("messages.lore-deny");
                for (String l : loreList) {
                    lore.add(ChatColor.translateAlternateColorCodes('&',l));
                }
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
            ItemStack clickedItem = event.getCurrentItem();
            double playerBalance = money.getBalance(currentPlayer);

            if (clickedItem != null) {
                for (Shop item : shopItems) {
                    if (clickedItem.getType() == item.getItem()) {
                        if (playerBalance >= item.getPrice()) {
                            currentPlayer.getInventory().addItem(clickedItem);
                            money.withdrawPlayer(currentPlayer, item.getPrice());
                            currentPlayer.playSound(currentPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,1);
                        }
                        else {
                            currentPlayer.sendMessage("No tienes: " + item.getPrice() + ".Tienes: " + playerBalance);
                        }
                    }
                }
            }
            event.setCancelled(true);
        }

    }
}