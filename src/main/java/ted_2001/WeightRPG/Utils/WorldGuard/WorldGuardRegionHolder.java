package ted_2001.WeightRPG.Utils.WorldGuard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.ChatColor;

import static org.bukkit.Bukkit.getServer;

public class WorldGuardRegionHolder {

    public static StateFlag MY_CUSTOM_FLAG;
    private final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Weight-RPG" +ChatColor.GRAY + "] ";

        public void RegionHolder() {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + "WorldGuard" + ChatColor.GRAY + " found.");
            try {
                StateFlag flag = new StateFlag("weight-rpg", true);
                registry.register(flag);
                MY_CUSTOM_FLAG = flag; // only set our field if there was no error
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED + "weight-rpg" + ChatColor.GRAY + " flag enabled.");
            } catch (FlagConflictException e ) {
                // some other plugin registered a flag by the same name already.
                // you can use the existing flag, but this may cause conflicts - be sure to check type
                Flag<?> existing = registry.get("weight-rpg");
                if (existing instanceof StateFlag) {
                    MY_CUSTOM_FLAG = (StateFlag) existing;
                }
            } catch (IllegalStateException ex) {}
        }
}
