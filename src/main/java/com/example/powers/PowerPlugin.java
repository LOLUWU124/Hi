package com.example.powers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PowerPlugin extends JavaPlugin {
    private PowerManager powerManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.powerManager = new PowerManager(this);
        powerManager.load();

        PowerCommand command = new PowerCommand(powerManager);
        if (getCommand("power") != null) {
            getCommand("power").setExecutor(command);
            getCommand("power").setTabCompleter(command);
        }

        Bukkit.getPluginManager().registerEvents(new PowerListener(powerManager), this);
        powerManager.startPassiveTask(this);
        getLogger().info("PowerPlugin enabled with " + PowerType.values().length + " powers.");
    }

    @Override
    public void onDisable() {
        powerManager.save();
    }
}
