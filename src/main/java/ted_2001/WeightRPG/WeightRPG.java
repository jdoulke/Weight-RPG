package ted_2001.WeightRPG;

import org.bukkit.Material;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import ted_2001.WeightRPG.Commands.Tabcompleter;
import ted_2001.WeightRPG.Commands.WeightCommand;
import ted_2001.WeightRPG.Listeners.WeightCalculateListeners;
import ted_2001.WeightRPG.Utils.JsonFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;




public final class WeightRPG extends JavaPlugin {

     private static WeightRPG plugin;

    public HashMap<UUID,Float> weighthashmap;
    public HashMap<ItemStack, Float> itemweight;



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
        getServer().getLogger().info("[Weight-RPG] Done.");
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
    }

    public static WeightRPG getPlugin(){
        return plugin;
    }


}
