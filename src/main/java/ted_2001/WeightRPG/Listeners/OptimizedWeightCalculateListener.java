package ted_2001.WeightRPG.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Thin adapter around the legacy listener. It preserves every existing handler and priority,
 * while avoiding the expensive movement path for events that only changed yaw/pitch.
 */
public final class OptimizedWeightCalculateListener implements Listener {

    private final WeightCalculateListeners legacy = new WeightCalculateListeners();

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        legacy.onInventoryClose(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        legacy.onPlayerJoin(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        legacy.onPlayerRespawn(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        legacy.onPlayerChangedWorldEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        legacy.onGamemodeChange(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(EntityPickupItemEvent event) {
        legacy.onItemPickUp(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event) {
        legacy.onItemDrop(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        legacy.onPlayerBlockPlace(event);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null) {
            return;
        }

        Location from = event.getFrom();
        if (Double.compare(from.getX(), to.getX()) == 0
                && Double.compare(from.getY(), to.getY()) == 0
                && Double.compare(from.getZ(), to.getZ()) == 0) {
            return;
        }

        legacy.onPlayerJump(event);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        legacy.onPlayerQuit(event);
    }
}
