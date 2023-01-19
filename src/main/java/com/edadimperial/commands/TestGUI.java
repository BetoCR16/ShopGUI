package com.edadimperial.commands;

import com.edadimperial.utils.ItemsAdderWrapper;
import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TestGUI implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            ItemsAdderWrapper title = new ItemsAdderWrapper();
            TexturedInventoryWrapper gui = new TexturedInventoryWrapper(player, 54, "솎", title.getFontImage("edadimperial:menu_base"),16, -8);


            gui.showInventory(player);

        }

        return true;
    }
}
