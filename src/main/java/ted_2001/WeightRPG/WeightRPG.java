package ted_2001.WeightRPG;

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

import java.io.File;
import java.util.List;
import java.util.Objects;




public final class WeightRPG extends JavaPlugin {

     private static WeightRPG plugin;

    public BukkitScheduler scheduler = this.getServer().getScheduler();
    public BukkitTask task;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new WeightCalculateListeners(), this);
        Objects.requireNonNull(getCommand("weight")).setExecutor(new WeightCommand());
        TabCompleter tc = new Tabcompleter();
        Objects.requireNonNull(getPlugin().getCommand("weight")).setTabCompleter(tc);
        JsonFile js = new JsonFile();
        getServer().getLogger().info("[Weight-RPG] Getting ready config and items weight files.");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        String path = this.getDataFolder().getAbsolutePath();
        File Weight = new File(path + "\\Weights");
        if(!Weight.exists())
            Weight.mkdir();
        js.saveJsonFile();
        Messages.create();
        Messages.getDefaults();
        Messages.getMessages().options().copyDefaults(true);
        Messages.getDefaults();
        Messages.saveMessages();
        getServer().getLogger().info("[Weight-RPG] Done.");
        js.readJsonFile();
        getServer().getLogger().info("[Weight-RPG] Read weight files successfully.");
        CalculateWeight w= new CalculateWeight();
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
        for (Player plist : players)
            w.calculateWeight(plist);
        scheduler();
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
