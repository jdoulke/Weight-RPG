package ted_2001.WeightRPG.Utils;

import org.bukkit.Bukkit;
import ted_2001.WeightRPG.WeightRPG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Checks Weight-RPG's current Spigot resource version asynchronously.
 */
public class UpdateChecker {

    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;

    private final WeightRPG plugin;
    private final int resourceId;

    public UpdateChecker(WeightRPG plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            HttpURLConnection connection = null;
            try {
                URI uri = URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
                connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);
                connection.setUseCaches(false);
                connection.setRequestProperty("User-Agent", "Weight-RPG/" + plugin.getDescription().getVersion());

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String version = reader.readLine();
                    if (version != null && !version.isBlank()) {
                        consumer.accept(version.trim());
                    }
                }
            } catch (IOException | IllegalArgumentException exception) {
                plugin.getLogger().info("Unable to check for updates: " + exception.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}
