package ted_2001.WeightRPG.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Handles the messages.yml file.
 */
public final class Messages {

    private static File file;
    private static FileConfiguration configuration;

    private Messages() {
    }

    public static void create() {
        file = new File(getPlugin().getDataFolder(), "messages.yml");

        if (!file.exists()) {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                getPlugin().getLogger().warning("Could not create plugin data directory for messages.yml");
            }
            getPlugin().saveResource("messages.yml", false);
        }

        // loadConfiguration already loads and parses the file; loading it a second time is redundant I/O.
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration getMessages() {
        if (configuration == null) {
            create();
        }
        return configuration;
    }

    public static void reloadMessagesConfig() {
        if (file == null) {
            file = new File(getPlugin().getDataFolder(), "messages.yml");
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }
}
