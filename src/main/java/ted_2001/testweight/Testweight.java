package ted_2001.testweight;

import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import ted_2001.testweight.Commands.Tabcompleter;
import ted_2001.testweight.Commands.WeightCommand;
import ted_2001.testweight.Listeners.WeightCalculateListeners;

public final class Testweight extends JavaPlugin {

     private static Testweight plugin;


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

    public static Testweight getPlugin(){
        return plugin;
    }


}
