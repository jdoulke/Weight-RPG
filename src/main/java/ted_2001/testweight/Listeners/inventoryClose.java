package ted_2001.testweight.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;


public class inventoryClose implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Bukkit.broadcastMessage("GEIA1");
        Player p = (Player) e.getPlayer();
        Inventory inv = p.getInventory();
    }


}
