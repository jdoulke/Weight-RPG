package ted_2001.WeightRPG.Utils.WorldGuard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.ChatColor;

import static org.bukkit.Bukkit.getServer;

public class WorldGuardRegionHolder {

    // The custom flag used by this plugin
    public static StateFlag WEIGHT-RPG-FLAG;

    // The plugin's prefix used in console messages
    private final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Weight-RPG" + ChatColor.GRAY + "] ";

    // Constructor (Note: the constructor should have the same name as the class)
    public void RegionHolder() {
        // Get the WorldGuard flag registry
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        // Inform the console that WorldGuard has been found
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "WorldGuard" + ChatColor.GRAY + " found.");

        try {
            // Create a new StateFlag with the name "weight-rpg" and default value true
            StateFlag flag = new StateFlag("weight-rpg", true);

            // Register the flag with the WorldGuard flag registry
            registry.register(flag);

            // Set the plugin's custom flag to the newly created flag
            WEIGHT-RPG-FLAG = flag;

            // Inform the console that the "weight-rpg" flag has been enabled
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "weight-rpg" + ChatColor.GRAY + " flag enabled.");
        } catch (FlagConflictException e) {
            // If there is a flag conflict (another plugin registered a flag with the same name),
            // attempt to use the existing flag, but be cautious of potential conflicts
            Flag<?> existing = registry.get("weight-rpg");
            if (existing instanceof StateFlag) {
                WEIGHT-RPG-FLAG = (StateFlag) existing;
            }
        } catch (IllegalStateException ignored) {
            // Catch any other illegal state exceptions that might occur during flag registration
        }
    }
}
