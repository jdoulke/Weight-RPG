package ted_2001.WeightRPG.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import ted_2001.WeightRPG.Utils.CalculateWeight;



public class WeightCalculateListeners implements Listener {

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        CalculateWeight w= new CalculateWeight();
        w.calculateWeight(p);

    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onGamemodeChange(PlayerGameModeChangeEvent e){
        Player p =e.getPlayer();
        if(e.getNewGameMode().toString().equalsIgnoreCase("CREATIVE") || e.getNewGameMode().toString().equalsIgnoreCase("SPECTATOR")){
            p.setWalkSpeed((float) 0.2);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onItemPickUp(EntityPickupItemEvent e){
        if(e.getEntity() instanceof Player){
           Player p = (Player) e.getEntity();
            CalculateWeight w= new CalculateWeight();
            w.calculateWeight(p);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){
            Player p = e.getPlayer();
            CalculateWeight w= new CalculateWeight();
            w.calculateWeight(p);
    }



}
