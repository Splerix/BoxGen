package com.splerix.boxgen;

import org.bukkit.plugin.java.JavaPlugin;

public final class BoxGen extends JavaPlugin {
    private BoxGenCommand boxGenCommand;
    @Override
    public void onEnable() {
        this.boxGenCommand = new BoxGenCommand(this);
        boxGenCommand.manager.data.saveDefaultConfig();
        this.saveDefaultConfig();

        this.getCommand("boxgen").setExecutor(boxGenCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
