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

import static ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegionHolder.MY_CUSTOM_FLAG;

public class WorldGuardRegion {

    //check if the player is in a worldguard plugin's region
    public boolean isInRegion(Player p) {
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(p.getWorld()));
            if (regions != null) {
                Location location = p.getLocation();
                int x = location.getBlockX();
                int y = location.getBlockY();
                int z = location.getBlockZ();
                ApplicableRegionSet reg = regions.getApplicableRegions(BlockVector3.at(x, y, z));
                if (reg.size() > 0) {
                    LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(p);
                    if (!reg.testState(localPlayer, MY_CUSTOM_FLAG)) {
                        p.setWalkSpeed(0.2f);
                        return true;
                    }
                }
            }
        } catch (NoClassDefFoundError ignored ) {}
        return false;
    }
}
