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

import static org.bukkit.Bukkit.getServer;


public final class WeightRPG extends JavaPlugin {


    private static WeightRPG plugin;

    public BukkitScheduler scheduler = this.getServer().getScheduler();
    public BukkitTask task;
    private String pluginPrefix;

    @Override
    public void onEnable() {
        plugin = this;
        pluginPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("plugin-prefix"));
        getServer().getPluginManager().registerEvents(new WeightCalculateListeners(), this);
        Objects.requireNonNull(getCommand("weight")).setExecutor(new WeightCommand());
        TabCompleter tc = new Tabcompleter();
        Objects.requireNonNull(getPlugin().getCommand("weight")).setTabCompleter(tc);
        JsonFile js = new JsonFile();
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Preparing config and weight files...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        String path = this.getDataFolder().getAbsolutePath();
        File Weight = new File(path + "\\Weights");
        if(!Weight.exists())
            Weight.mkdir();
        js.saveJsonFile();
        Messages.create();
        js.readJsonFile();
        if(js.successfullRead) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY+"Reading weight files completed" + ChatColor.GREEN + " SUCCESSFULLY.");
        }else{
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "ERROR" + ChatColor.GRAY+" Weight or Config files have ERROR(s).");
        }
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Done.");
        // Small check to make sure that PlaceholderAPI is installed
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "PlaceholderAPI" +ChatColor.GRAY + " found. Registering placeholders");
            new WeightExpasion().register();
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Done.");
        }
        scheduler();
        new UpdateChecker(this, 105513).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There is no new update available.");
            } else if (Double.parseDouble(this.getDescription().getVersion()) < Double.parseDouble(version)){
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There is a new update available.");
            } else {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "You are using a newer version compared to the version uploaded on spigot.");
            }
        });
        Metrics metrics = new Metrics(this,16524);

    }

    public void scheduler() {
        int timer;
        if(this.getConfig().getDouble("check-weight") <= 0)
            timer = 2;
        else
            timer = (int) this.getConfig().getDouble("check-weight");

        task = scheduler.runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
                CalculateWeight weightCalculator= new CalculateWeight();
                for (Player plist : players)
                    if(!plist.hasPermission("weight.bypass"))
                        weightCalculator.calculateWeight(plist);
            }
        },0, timer * 20L);
    }

    @Override
    public void onLoad(){
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                WorldGuardRegionHolder holder = new WorldGuardRegionHolder();
                holder.RegionHolder();
            } catch (NoClassDefFoundError e) {
                // Do something here
            }
        }
    }
    @Override
    public void onDisable() {
        saveDefaultConfig();
    }




    public static WeightRPG getPlugin(){
        return plugin;
    }

    public void reloadPluginPrefix(){
        this.pluginPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("plugin-prefix"));
    }

    public String getPluginPrefix(){return pluginPrefix;}


}
