package ted_2001.WeightRPG.Listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import ted_2001.WeightRPG.Utils.CalculateWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ted_2001.WeightRPG.Utils.CalculateWeight.playerweight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class WeightCalculateListeners implements Listener {


    CalculateWeight w= new CalculateWeight();
    public final HashMap<UUID, Long> jumpmessage = new HashMap<>();
    public final HashMap<UUID, Long> pickordropmessage = new HashMap<>();
    private List<String> disabledworlds;
    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(!p.hasPermission("weight.bypass"))
            w.calculateWeight(p);
    }
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(!p.hasPermission("weight.bypass"))
            w.calculateWeight(p);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        if(!p.hasPermission("weight.bypass"))
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
            Material item = e.getItem().getItemStack().getType();
            int amount = e.getItem().getItemStack().getAmount();
            float weight = globalitemsweight.get(item);
            if(!p.hasPermission("weight.bypass")) {
                if (playerweight.get(p.getUniqueId()) == 0 || playerweight.get(p.getUniqueId()) == null) {
                    w.calculateWeight(p);
                    message(p,"receive",item,weight, amount);
                }else{
                    String s = "pick";
                    putWeightValue(p, item, amount, s);
                    message(p,"receive",item,weight, amount);
                    w.getWeightsEffect(p);
                }
            }
        }
    }



    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();
        Material item = e.getItemDrop().getItemStack().getType();
        int amount = e.getItemDrop().getItemStack().getAmount();
        float weight = globalitemsweight.get(item);
        if(!p.hasPermission("weight.bypass")) {
            if (playerweight.get(p.getUniqueId()) == 0 || playerweight.get(p.getUniqueId()) == null) {
                w.calculateWeight(p);
                message(p,"lose",item,weight, amount);
            } else {
                String s = "place";
                putWeightValue(p, item, amount, s);
                message(p,"lose",item,weight, amount);
                w.getWeightsEffect(p);
            }
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Material block = e.getBlock().getType();
        float weight = globalitemsweight.get(block);
        if(!p.hasPermission("weight.bypass")) {
            if (playerweight.get(p.getUniqueId()) == 0 || playerweight.get(p.getUniqueId()) == null) {
                w.calculateWeight(p);
                message(p,"lose",block,weight, 1);
            }else{
                String s = "place";
                putWeightValue(p, block, 1, s);
                message(p,"lose",block,weight, 1);
                w.getWeightsEffect(p);
            }
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


    private boolean checkblocks(Location loc) {
        loc.setX(loc.getBlockX() +0.5);
        if(loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB"))
            return true;
        loc.setX(loc.getBlockX() -1);
        if(loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB"))
            return true;
        loc.setX(loc.getBlockX() +0.5);
        loc.setZ(loc.getBlockZ() +0.5);
        if(loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB"))
            return true;
        loc.setZ(loc.getBlockZ() -1);
        return loc.getBlock().getType().toString().contains("STAIRS") || loc.getBlock().getType().toString().contains("SLAB");
    }

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

    private void message(Player p, String action, Material item, float weight, int amount){
        String message = null;
        if (action.equalsIgnoreCase("receive"))
            message = getPlugin().getConfig().getString("receive-item-message");
        else if(action.equalsIgnoreCase("lose"))
            message = getPlugin().getConfig().getString("lose-message");
        if(!pickordropmessage.containsKey(p.getUniqueId())) {
            pickordropmessage.put(p.getUniqueId(), System.currentTimeMillis());
            if(getPlugin().getConfig().getBoolean("actionbar-messages")) {
                message.replaceAll("%block%", String.valueOf(item));
                message.replaceAll("%itemweight%", String.valueOf(weight));
                message.replaceAll("%totalweight%", String.valueOf(weight*amount));
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message, p))));
            }else {
                message.replaceAll("%block%", String.valueOf(item));
                message.replaceAll("%itemweight%", String.valueOf(weight));
                message.replaceAll("%totalweight%", String.valueOf(weight*amount));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message, p)));
            }
        }else{
            long timeElapsed = System.currentTimeMillis() - pickordropmessage.get(p.getUniqueId());
            if(timeElapsed >= getPlugin().getConfig().getDouble("messages-cooldown") * 1000){
                pickordropmessage.put(p.getUniqueId(), System.currentTimeMillis());
                if(getPlugin().getConfig().getBoolean("actionbar-messages")) {
                    message.replaceAll("%block%", String.valueOf(item));
                    message.replaceAll("%itemweight%", String.valueOf(weight));
                    message.replaceAll("%totalweight%", String.valueOf(weight*amount));
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message, p))));
                }else {
                    message.replaceAll("%block%", String.valueOf(item));
                    message.replaceAll("%itemweight%", String.valueOf(weight));
                    message.replaceAll("%totalweight%", String.valueOf(weight*amount));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message, p)));
                }
            }
        }
    }







}
