package ted_2001.WeightRPG;

import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import ted_2001.WeightRPG.Commands.Tabcompleter;
import ted_2001.WeightRPG.Commands.WeightCommand;
import ted_2001.WeightRPG.Listeners.WeightCalculateListeners;

import java.util.List;

public final class WeightRPG extends JavaPlugin {

     private static WeightRPG plugin;
     public List<String> disabledworlds;


    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new WeightCalculateListeners(), this);
        getCommand("weight").setExecutor(new WeightCommand());
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        TabCompleter tc = new Tabcompleter();
        this.getCommand("weight").setTabCompleter(tc);


    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    public static WeightRPG getPlugin(){
        return plugin;
    }


}
