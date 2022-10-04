package ted_2001.WeightRPG;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import ted_2001.WeightRPG.Commands.Tabcompleter;
import ted_2001.WeightRPG.Commands.WeightCommand;
import ted_2001.WeightRPG.Listeners.WeightCalculateListeners;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;
import ted_2001.WeightRPG.Utils.UpdateChecker;

import java.io.File;
import java.util.List;
import java.util.Objects;




public final class WeightRPG extends JavaPlugin {

    public static StateFlag MY_CUSTOM_FLAG;
    private static WeightRPG plugin;

    public BukkitScheduler scheduler = this.getServer().getScheduler();
    public BukkitTask task;
    private final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Weight-RPG" +ChatColor.GRAY + "] ";

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new WeightCalculateListeners(), this);
        Objects.requireNonNull(getCommand("weight")).setExecutor(new WeightCommand());
        TabCompleter tc = new Tabcompleter();
        Objects.requireNonNull(getPlugin().getCommand("weight")).setTabCompleter(tc);
        JsonFile js = new JsonFile();
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Getting ready config and items weight files.");
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
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY+"Read weight files" + ChatColor.GREEN + " SUCCESSFULLY.");
        }else{
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "ERROR" + ChatColor.GRAY+" Weight or Config files have ERROR(s).");
        }
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Done.");
        CalculateWeight w= new CalculateWeight();
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
        for (Player plist : players)
            w.calculateWeight(plist);
        scheduler();
        new UpdateChecker(this, 105513).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There is not a new update available.");
            } else if (Double.parseDouble(this.getDescription().getVersion()) < Double.parseDouble(version)){
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There is a new update available.");
            } else {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "You are using a version newer than spigot uploaded version.");
            }
        });
        Metrics metrics = new Metrics(this,16524);
        if(getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "WorldGuard " + ChatColor.GRAY + "found. Register flag. " );
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            try {
                StateFlag flag = new StateFlag("weight-rpg", true);
                registry.register(flag);
                MY_CUSTOM_FLAG = flag; // only set our field if there was no error
            } catch (FlagConflictException e) {
                // some other plugin registered a flag by the same name already.
                // you can use the existing flag, but this may cause conflicts - be sure to check type
                Flag<?> existing = registry.get("weight-rpg");
                if (existing instanceof StateFlag) {
                    MY_CUSTOM_FLAG = (StateFlag) existing;
                } else {
                    getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "WorldGuard " + ChatColor.GRAY + "Couldn't register flag. Contact the developer. " );
                }
            }
        }
    }

    public void scheduler() {
        int timer;
        if(this.getConfig().getDouble("check-weight") <= 0) {
            timer = 2;
        }else {
            timer = (int) this.getConfig().getDouble("check-weight");
        }
        task = scheduler.runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
                CalculateWeight w= new CalculateWeight();
                for (Player plist : players)
                    if(!plist.hasPermission("weight.bypass"))
                        w.calculateWeight(plist);
            }
        },0, timer * 20L);
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }




    public static WeightRPG getPlugin(){
        return plugin;
    }


}
