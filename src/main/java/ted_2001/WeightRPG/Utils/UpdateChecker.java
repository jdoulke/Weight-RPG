package ted_2001.WeightRPG.Utils;

import org.bukkit.Bukkit;
import ted_2001.WeightRPG.WeightRPG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * UpdateChecker class responsible for checking plugin updates using Spigot API.
 */
public class UpdateChecker {

    private final WeightRPG plugin;
    private final int resourceId;

    public UpdateChecker(WeightRPG plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    /**
     * Get the latest version of the plugin from the Spigot API asynchronously.
     * The result will be passed to the provided consumer.
     *
     * @param consumer A consumer that accepts the latest version as a String.
     */
    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
                 Scanner scanner = new Scanner(inputStream)) {

                // Check if the response from the API contains version information
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                // Log an error if unable to check for updates
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            }
        });
    }
}
