package ted_2001.WeightRPG.Utils.WorldGuard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegionHolder.WEIGHT_RPG_FLAG;

public class WorldGuardRegion {

    // Method to check if the player is in a WorldGuard region
    public boolean isInRegion(Player p) {
        try {
            // Get the WorldGuard region container for the player's world
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));

            if (regions != null) {
                // Get the player's current location
                Location location = p.getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                
                // Get the set of regions applicable to the player's current location
                ApplicableRegionSet reg = regions.getApplicableRegions(BlockVector3.at(x, y, z));

                if (reg.size() > 0) {
                    // Wrap the Bukkit player in a WorldGuard LocalPlayer instance
                    LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
                    
                    // Check if the custom flag is set in any of the applicable regions for the player
                    if (!reg.testState(localPlayer, WEIGHT_RPG_FLAG)) {
                        // Set the player's walk speed to 0.2f and return true if the flag is not set
                        p.setWalkSpeed(0.2f);
                        return true;
                    }
                }
            }
        } catch (NoClassDefFoundError ignored) {
            // Catch any errors that might occur if WorldGuard classes are not found
        }
        
        // Return false if the player is not in any WorldGuard region or an error occurred
        return false;
    }
}
