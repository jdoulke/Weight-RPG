package ted_2001.WeightRPG.Utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class Messages {

    private static File file;
    private static FileConfiguration configuration;



    //create the messages.yml file
    public static void create(){

        file = new File(getPlugin().getDataFolder(), "messages.yml");
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            getPlugin().saveResource("messages.yml", false);

        }
        configuration = YamlConfiguration.loadConfiguration(file);
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public static FileConfiguration getMessages(){
        return configuration;
    }

    public static void reloadMessagesConfig(){
        configuration = YamlConfiguration.loadConfiguration(file);
    }




}
