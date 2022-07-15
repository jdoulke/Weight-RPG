package ted_2001.testweight.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import ted_2001.testweight.Utils.CalculateWeight;

import static ted_2001.testweight.Testweight.getPlugin;


public class WeightCalculateListeners implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        CalculateWeight w= new CalculateWeight();
        w.calculateWeight(p);
    }

    @EventHandler
    public void test(PlayerGameModeChangeEvent e){

    }



}
