package com.splerix.boxgen;

import com.splerix.boxgen.Util.Box;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class BoxGenCommand implements CommandExecutor {
    private final Plugin plugin;
    public final Manager manager;

    private final BoxGenTabCompleter tabCompleter;

    public BoxGenCommand(Plugin plugin) {
        this.plugin = plugin;
        manager = new Manager(plugin);
        tabCompleter = new BoxGenTabCompleter(manager.getBoxList());

        Bukkit.getPluginCommand("boxgen").setTabCompleter(tabCompleter);
        plugin.getServer().getPluginManager().registerEvents(manager, plugin);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("boxgen.boxgen")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /boxgen <command>");
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("boxgen.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }

                plugin.reloadConfig();
                manager.reloadConfig();
                tabCompleter.reload(manager.getBoxList());

                sender.sendMessage(ChatColor.BLUE + "Reloading the Box Gen config");
                return true;
            case "regen-all":
                if (!sender.hasPermission("boxgen.regen-all")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
                    return false;
                }

                for (Box box : manager.getBoxList()) {
                    manager.generateGen(box,  (List<Player>) Bukkit.getOnlinePlayers());
                }
                //Save time resets
                manager.data.getConfig();

                sender.sendMessage(ChatColor.BLUE + "All of the boxes have been regenerated");
                return true;
            case "regen":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /boxgen regen <box name>");
                    return false;
                }
                return regenBox(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Usage: /boxgen <command> [additional arguments]");
                return false;
        }
    }

    private boolean regenBox(CommandSender sender, String[] args) {
        //Make sure box exists and get the boxes data
        String name = args[1];
        Box box = null;
        for (Box b : manager.getBoxList()) {
            if (name.equalsIgnoreCase(b.name)) {
                box = b;
                name = b.name;
                break;
            }
        }
        if (box == null) {
            sender.sendMessage(ChatColor.RED + "Usage: /boxgen regen <box name>");
            return false;
        }

        if (!sender.hasPermission("boxgen.regen."+name)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        manager.generateGen(box, (List<Player>) Bukkit.getOnlinePlayers());
        //Save the timer reset
        manager.data.saveConfig();
        sender.sendMessage(ChatColor.BLUE + "The box " + ChatColor.GOLD +  name + ChatColor.BLUE +
                " has been regenerated!");
        return true;
    }
}
