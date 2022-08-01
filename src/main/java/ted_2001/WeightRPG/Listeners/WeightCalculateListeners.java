package ted_2001.WeightRPG.Listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.ItemsWeightLore;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static ted_2001.WeightRPG.Utils.CalculateWeight.playerweight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class WeightCalculateListeners implements Listener {


    CalculateWeight w= new CalculateWeight();
    public final HashMap<UUID, Long> jumpmessage = new HashMap<>();
    private List<String> disabledworlds;
    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        w.calculateWeight(p);
    }
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        w.calculateWeight(p);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
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
            Player p = ((Player) e.getEntity()).getPlayer();
            if(playerweight.get(p.getUniqueId()) == 0 || playerweight.get(p.getUniqueId()) == null){
                w.calculateWeight(p);
            }else {
                Material item = e.getItem().getItemStack().getType();
                int amount = e.getItem().getItemStack().getAmount();
                String s = "pick";
                putWeightValue(p, item, amount, s);
                w.getWeightsEffect(p);
            }
        }
    }



    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        if(playerweight.get(p.getUniqueId()) == 0 || playerweight.get(p.getUniqueId()) == null){
            w.calculateWeight(p);
        }else {
            Material item = e.getItemDrop().getItemStack().getType();
            int amount = e.getItemDrop().getItemStack().getAmount();
            String s = "place";
            putWeightValue(p, item, amount, s);
            w.getWeightsEffect(p);
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        if(playerweight.get(p.getUniqueId()) == 0 || playerweight.get(p.getUniqueId()) == null){
            w.calculateWeight(p);
        }else {
            Material block = e.getBlock().getType();
            String s = "place";
            putWeightValue(p, block, 1, s);
            w.getWeightsEffect(p);
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJump(PlayerMoveEvent e){
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        if(p.hasPermission("weight.bypass.jump"))
            return;
        if(checkblocks(loc))
            return;
        String y = String.valueOf(p.getLocation().getY());
        String[] ydigit = y.split("\\.");
        int digit = Integer.parseInt(String.valueOf(ydigit[1].charAt(0)));
        if(digit != 0)
            return;
        if(p.getLocation().getBlock().getType() == Material.LADDER || p.getLocation().getBlock().getType() == Material.WATER || p.getLocation().getBlock().getType() == Material.LAVA)
            return;
        if(e.getTo().getY() > e.getFrom().getY()) {
            float weight = playerweight.get(p.getUniqueId());
            boolean disablejump = false;
            double weight1 = getPlugin().getConfig().getDouble("weight-level-1.value");
            double weight2 = getPlugin().getConfig().getDouble("weight-level-2.value");
            double weight3 = getPlugin().getConfig().getDouble("weight-level-3.value");
            if(weight > weight1 && weight < weight2) {
                disablejump = getPlugin().getConfig().getBoolean("weight-level-1.disable-jump");
            }else if(weight > weight2 && weight < weight3) {
                disablejump = getPlugin().getConfig().getBoolean("weight-level-2.disable-jump");
            }else if(weight > weight3) {
                disablejump = getPlugin().getConfig().getBoolean("weight-level-3.disable-jump");
            }
            if(disablejump) {
                e.getTo().setY(e.getFrom().getY());
                if(!jumpmessage.containsKey(p.getUniqueId())) {
                    jumpmessage.put(p.getUniqueId(), System.currentTimeMillis());
                    String message = getPlugin().getConfig().getString("disable-jump-message");
                    if(getPlugin().getConfig().getBoolean("actionbar-messages"))
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p))));
                    else
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p)));
                }else{
                    long timeElapsed = System.currentTimeMillis() - jumpmessage.get(p.getUniqueId());
                    if(timeElapsed >= 4 * 1000){
                        jumpmessage.put(p.getUniqueId(), System.currentTimeMillis());
                        String message = getPlugin().getConfig().getString("disable-jump-message");
                        if(getPlugin().getConfig().getBoolean("actionbar-messages"))
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p))));
                        else
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p)));
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent e){
        Player p = (Player) e.getPlayer();
        p.sendMessage(String.valueOf(e.getInventory()));
        ItemsWeightLore item = new ItemsWeightLore();
        item.onInventoryOpen(p);
    }

    private boolean checkblocks(Location loc) {
        loc.setX(loc.getBlockX() +1);
        if(loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB"))
            return true;
        loc.setX(loc.getBlockX() -2);
        if(loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB"))
            return true;
        loc.setX(loc.getBlockX() +1);
        loc.setZ(loc.getBlockZ() +1);
        if(loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB"))
            return true;
        loc.setZ(loc.getBlockZ() -2);
        return loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB");
    }

    //@EventHandler
    //public void test(Inven)
    private void putWeightValue(Player p, Material item, int amount,String s) {
        String PlayerGamemode = p.getGameMode().toString();
        disabledworlds = getPlugin().getConfig().getStringList("disabled-worlds");
        if(PlayerGamemode.equalsIgnoreCase("CREATIVE") || PlayerGamemode.equalsIgnoreCase("SPECTATOR")) {
            p.setWalkSpeed((float) 0.2);
            return;
        }
        for (String disabledworld : disabledworlds) {
            if (disabledworld.equalsIgnoreCase((p.getWorld().getName()))) {
                p.setWalkSpeed((float) 0.2);
                return;
            }
        }
        float weight = 0;
        if (playerweight.get(p.getUniqueId()) != null && s.equalsIgnoreCase("place")) {
            weight = playerweight.get(p.getUniqueId());
            weight -= globalitemsweight.get(item) * amount;
        }else if (playerweight.get(p.getUniqueId()) != null && s.equalsIgnoreCase("pick")) {
            weight = playerweight.get(p.getUniqueId());
            weight += globalitemsweight.get(item) * amount;
        }
        weight = (float) (Math.round(weight * 1000.0) / 1000.0);
        playerweight.put(p.getUniqueId(), weight);

    }




}
