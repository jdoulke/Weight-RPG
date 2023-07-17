package ted_2001.WeightRPG.Utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;



import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Messages class responsible for handling the messages.yml file.
 */
public class Messages {

    private static File file;
    private static FileConfiguration configuration;

    /**
     * Create the messages.yml file and load its configuration.
     * If the file doesn't exist, it will be created from the plugin's resources.
     */
    public static void create() {
        file = new File(getPlugin().getDataFolder(), "messages.yml");
        
        // Create the messages.yml file if it doesn't exist
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            getPlugin().saveResource("messages.yml", false);
        }

        // Load the configuration from the messages.yml file
        configuration = YamlConfiguration.loadConfiguration(file);
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getMessages() {
        return configuration;
    }

    /**
     * Reload the messages.yml configuration from the file.
     * This method is useful when you want to update the messages without restarting the server.
     */
    public static void reloadMessagesConfig() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }
}
