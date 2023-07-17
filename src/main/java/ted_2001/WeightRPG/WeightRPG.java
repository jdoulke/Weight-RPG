package ted_2001.WeightRPG;


import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import ted_2001.WeightRPG.Commands.Tabcompleter;
import ted_2001.WeightRPG.Commands.WeightCommand;
import ted_2001.WeightRPG.Listeners.WeightCalculateListeners;
import ted_2001.WeightRPG.Utils.*;
import ted_2001.WeightRPG.Utils.PlaceholderAPI.WeightExpasion;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegionHolder;

import java.io.File;
import java.util.List;
import java.util.Objects;



public final class WeightRPG extends JavaPlugin {

    private static WeightRPG plugin;
    private BukkitScheduler scheduler = this.getServer().getScheduler();
    private BukkitTask task;
    private String pluginPrefix;

    @Override
    public void onEnable() {

        plugin = this;
        pluginPrefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("plugin-prefix")));

        // Register listeners and commands
        getServer().getPluginManager().registerEvents(new WeightCalculateListeners(), this);
        Objects.requireNonNull(getCommand("weight")).setExecutor(new WeightCommand());
        TabCompleter tc = new Tabcompleter();
        Objects.requireNonNull(getPlugin().getCommand("weight")).setTabCompleter(tc);

        // Initialize JSON files and configuration
        JsonFile js = new JsonFile();
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Preparing config and weight files...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        String path = this.getDataFolder().getAbsolutePath();
        File weightsDir = new File(path + File.separator + "Weights");
        if (!weightsDir.exists()) {
            weightsDir.mkdir();
        }
        js.saveJsonFile();
        Messages.create();
        js.readJsonFile();
        if (js.successfullyRead) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Reading weight files completed" + ChatColor.GREEN + " SUCCESSFULLY.");
        } else {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "ERROR" + ChatColor.GRAY + " Weight or Config files have ERROR(s).");
        }
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Done.");

        // Check for PlaceholderAPI and register placeholders
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "PlaceholderAPI" + ChatColor.GRAY + " found. Registering placeholders");
            new WeightExpasion().register();
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Done.");
        }

        // Schedule weight calculation task
        scheduler();

        // Check for plugin updates
        new UpdateChecker(this, 105513).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There is no new update available.");
            } else if (Double.parseDouble(this.getDescription().getVersion()) < Double.parseDouble(version)) {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There is a new update available.");
            } else {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "You are using a newer version compared to the version uploaded on Spigot.");
            }
        });

        // Initialize metrics
        Metrics metrics = new Metrics(this, 16524);
    }

    /**
     * Schedules the weight calculation task to run at a configurable interval.
     */
    public void scheduler() {
        int timer;
        if (this.getConfig().getDouble("check-weight") <= 0) 
            timer = 2;
        else 
            timer = (int) this.getConfig().getDouble("check-weight");
        

        task = scheduler.runTaskTimer(this, () -> {
            List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
            CalculateWeight weightCalculator = new CalculateWeight();
            for (Player player : players) {
                if (!player.hasPermission("weight.bypass")) {
                    weightCalculator.calculateWeight(player);
                }
            }
        }, 0, timer * 20L);
    }

    @Override
    public void onLoad() {
        // Check for WorldGuard and initialize WorldGuardRegionHolder if found
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                WorldGuardRegionHolder holder = new WorldGuardRegionHolder();
                holder.RegionHolder();
            } catch (NoClassDefFoundError e) {}
        }
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    public static WeightRPG getPlugin() {
        return plugin;
    }


    public void reloadPluginPrefix() {
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("plugin-prefix")));
    }

    public String getPluginPrefix() {
        return pluginPrefix;
    }
}
