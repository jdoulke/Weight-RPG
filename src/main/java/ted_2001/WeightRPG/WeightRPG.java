package ted_2001.WeightRPG;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ted_2001.WeightRPG.Commands.Tabcompleter;
import ted_2001.WeightRPG.Commands.WeightCommands;
import ted_2001.WeightRPG.Listeners.WeightCalculateListeners;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;
import ted_2001.WeightRPG.Utils.UpdateChecker;
import ted_2001.WeightRPG.Utils.PlaceholderAPI.WeightExpansion;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegionHolder;

import java.io.File;
import java.util.Objects;

public final class WeightRPG extends JavaPlugin {

    private static WeightRPG plugin;
    public BukkitTask task;
    private String pluginPrefix;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        reloadPluginPrefix();

        getServer().getPluginManager().registerEvents(new WeightCalculateListeners(), this);

        PluginCommand weightCommand = Objects.requireNonNull(getCommand("weight"), "Command 'weight' is missing from plugin.yml");
        weightCommand.setExecutor(new WeightCommands());
        weightCommand.setTabCompleter(new Tabcompleter());

        JsonFile jsonFile = new JsonFile();
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Preparing config and weight files...");

        File weightsDir = new File(getDataFolder(), "Weights");
        if (!weightsDir.exists() && !weightsDir.mkdirs()) {
            getLogger().warning("Could not create the Weights directory: " + weightsDir.getAbsolutePath());
        }

        jsonFile.saveJsonFile();
        Messages.create();
        try {
            jsonFile.readJsonFile();
        } catch (RuntimeException exception) {
            jsonFile.successfullyRead = false;
            getLogger().severe("Unable to read weight files: " + exception.getMessage());
        }

        if (jsonFile.successfullyRead) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Reading weight files completed" + ChatColor.GREEN + " SUCCESSFULLY.");
        } else {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "ERROR" + ChatColor.GRAY + " Weight or config files contain errors.");
        }
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Done.");

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "PlaceholderAPI" + ChatColor.GRAY + " found. Registering placeholders");
            new WeightExpansion().register();
        }

        scheduler();
        checkForUpdates();
        new Metrics(this, 16524);
    }

    public void scheduler() {
        int intervalSeconds = Math.max(2, getConfig().getInt("check-weight", 2));
        long intervalTicks = intervalSeconds * 20L;
        CalculateWeight weightCalculator = new CalculateWeight();

        task = getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                if (!player.hasPermission("weight.bypass")) {
                    weightCalculator.calculateWeight(player);
                }
            }
        }, intervalTicks, intervalTicks);
    }

    private void checkForUpdates() {
        new UpdateChecker(this, 105513).getVersion(latestVersion -> {
            int comparison = compareVersions(getDescription().getVersion(), latestVersion);
            if (comparison < 0) {
                getLogger().info("There is a new Weight-RPG update available: " + latestVersion);
            } else if (comparison == 0) {
                getLogger().info("Weight-RPG is up to date.");
            } else {
                getLogger().info("You are running a development version of Weight-RPG.");
            }
        });
    }

    private int compareVersions(String currentVersion, String latestVersion) {
        String[] currentParts = currentVersion.split("[.-]");
        String[] latestParts = latestVersion.split("[.-]");
        int length = Math.max(currentParts.length, latestParts.length);

        for (int i = 0; i < length; i++) {
            int current = i < currentParts.length ? parseVersionPart(currentParts[i]) : 0;
            int latest = i < latestParts.length ? parseVersionPart(latestParts[i]) : 0;
            int comparison = Integer.compare(current, latest);
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    private int parseVersionPart(String part) {
        String numericPart = part.replaceAll("\\D.*$", "");
        if (numericPart.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    @Override
    public void onLoad() {
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                new WorldGuardRegionHolder().RegionHolder();
            } catch (NoClassDefFoundError error) {
                getLogger().warning("WorldGuard was detected but its API could not be loaded: " + error.getMessage());
            }
        }
    }

    @Override
    public void onDisable() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        CalculateWeight.playerWeight.clear();
        CalculateWeight.playerBoostWeight.clear();
        CalculateWeight.cooldown.clear();
    }

    public static WeightRPG getPlugin() {
        return plugin;
    }

    public void reloadPluginPrefix() {
        String configuredPrefix = getConfig().getString("plugin-prefix", "&7[&eWeight-RPG&7] ");
        pluginPrefix = ChatColor.translateAlternateColorCodes('&', configuredPrefix);
    }

    public String getPluginPrefix() {
        return pluginPrefix;
    }
}
