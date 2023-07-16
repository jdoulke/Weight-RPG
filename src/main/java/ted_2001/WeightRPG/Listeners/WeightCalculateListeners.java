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
import org.bukkit.inventory.ItemStack;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.Messages;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.Utils.CalculateWeight.*;
import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class WeightCalculateListeners implements Listener {


    CalculateWeight w= new CalculateWeight();
    public final HashMap<UUID, Long> jumpMessage = new HashMap<>();
    public final HashMap<UUID, Long> pickMessage = new HashMap<>();
    public final HashMap<UUID, Long> placeMessage = new HashMap<>();
    public final HashMap<UUID, Long> dropMessage = new HashMap<>();
    private final HashMap<UUID, Long> notifyMessage = new HashMap<>();

    private final HashMap<UUID, Long> dropCooldown = new HashMap<>();

    private final String pluginPrefix = org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.YELLOW + "Weight-RPG" + org.bukkit.ChatColor.GRAY + "] ";

    boolean isPluginEnabled = false;


    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(isWorldGuardEnabled(p))
            return;
        w.calculateWeight(p);
    }




    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(isWorldGuardEnabled(p))
            return;
        w.calculateWeight(p);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        if(isWorldGuardEnabled(p))
            return;
        w.calculateWeight(p);
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e){
        Player p = e.getPlayer();
        isPluginEnabled = checkIfEnable(p);
        if(!isPluginEnabled)
            return;
        if(isWorldGuardEnabled(p))
            return;
        w.calculateWeight(p);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onGamemodeChange(PlayerGameModeChangeEvent e){
        Player p =e.getPlayer();
        if(e.getNewGameMode().toString().equalsIgnoreCase("CREATIVE") || e.getNewGameMode().toString().equalsIgnoreCase("SPECTATOR")){
            p.setWalkSpeed(0.2f);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onItemPickUp(EntityPickupItemEvent e){
        if(e.getEntity() instanceof Player){
            Player p = ((Player) e.getEntity()).getPlayer();
            assert p != null;

            isPluginEnabled = checkIfEnable(p);
            if(!isPluginEnabled)
                return;
            if(isWorldGuardEnabled(p))
                return;

            ItemStack item = e.getItem().getItemStack();
            int amount = e.getItem().getItemStack().getAmount();
            if(globalItemsWeight.get(item.getType()) == null){
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA +item.getType()  + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually.");
                notifyAdmins(item);
                return;
            }
            float weight = 0;
            boolean isCustomItem = false;
            if(customItemsWeight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName())) {
                weight = customItemsWeight.get(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
                isCustomItem = true;
            } else if(globalItemsWeight.get(item.getType()) != null)
                weight = globalItemsWeight.get(item.getType());
            if (playerWeight.get(p.getUniqueId()) == 0 || playerWeight.get(p.getUniqueId()) == null || isCustomItem) {
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



    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){
        Player p = e.getPlayer();

        isPluginEnabled = checkIfEnable(p);
        if(!isPluginEnabled)
            return;
        if(isWorldGuardEnabled(p))
            return;
        if(p.hasPermission("weight.bypass"))
            return;

        if(getPlugin().getConfig().getBoolean("drop-cooldown.enabled")){
            if(!dropCooldown.containsKey(p.getUniqueId())) {
                dropCooldown.put(p.getUniqueId(), System.currentTimeMillis());
            }else {
                long timeElapsed = System.currentTimeMillis() - dropCooldown.get(p.getUniqueId());
                if(timeElapsed < getPlugin().getConfig().getDouble("drop-cooldown.cooldown") * 1000){
                    e.setCancelled(true);
                    String message = Messages.getMessages().getString("drop-cooldown-message");
                    assert message != null;
                    message = w.messageSender(message, p);
                    if(getPlugin().getConfig().getBoolean("actionbar-messages"))
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p))));
                    else
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p)));
                    return;
                }else
                    dropCooldown.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }



        ItemStack item = e.getItemDrop().getItemStack();
        int amount = e.getItemDrop().getItemStack().getAmount();

        if(globalItemsWeight.get(item.getType()) == null){
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + item.getType()  + ChatColor.GRAY +
                    " isn't in the weight files. You might want to add it manually.");
            notifyAdmins(item);
            return;
        }

        boolean isCustomItem = false;
        float weight = 0;

        if(customItemsWeight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName())) {
                weight = customItemsWeight.get(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
            isCustomItem = true;
        } else if(globalItemsWeight.get(item.getType()) != null)
            weight = globalItemsWeight.get(item.getType());


        if (playerWeight.get(p.getUniqueId()) == 0 || playerWeight.get(p.getUniqueId()) == null || isCustomItem) {
            w.calculateWeight(p);
            message(p, "lose", item, weight, amount);
        } else {
            String s = "place";
            putWeightValue(p, item, amount, s);
            message(p, "lose", item, weight, amount);
            w.getWeightsEffect(p);
        }

    }



    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        isPluginEnabled = checkIfEnable(p);
        if(!isPluginEnabled)
            return;
        if(isWorldGuardEnabled(p))
            return;
        ItemStack block = new ItemStack(e.getBlock().getType());
        if(globalItemsWeight.get(block.getType()) == null){
            if(block.getType().toString().equalsIgnoreCase("FIRE"))
                return;
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + block.getType()  + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually.");
            notifyAdmins(block);
            return;
        }
        float weight = 0;
        boolean isCustomItem = false;
        if(customItemsWeight.containsKey(Objects.requireNonNull(block.getItemMeta()).getDisplayName())) {
            weight = customItemsWeight.get(Objects.requireNonNull(block.getItemMeta()).getDisplayName());
            isCustomItem = true;
        }
       else if(globalItemsWeight.get(block.getType()) != null)
            weight = globalItemsWeight.get(block.getType());
       if (playerWeight.get(p.getUniqueId()) == 0 || playerWeight.get(p.getUniqueId()) == null || isCustomItem) {
           w.calculateWeight(p);
           message(p, "place", block, weight, 1);
       } else {
           String s = "place";
           putWeightValue(p, block, 1, s);
           message(p, "place", block, weight, 1);
           w.getWeightsEffect(p);
       }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJump(PlayerMoveEvent e){
        Player p = e.getPlayer();

        isPluginEnabled = checkIfEnable(p);
        if(!isPluginEnabled)
            return;
        if(isWorldGuardEnabled(p))
            return;

        Location loc = p.getLocation();
        if(p.hasPermission("weight.bypass"))
            return;
        if(checkBlocks(loc))
            return;
        String y = String.valueOf(p.getLocation().getY());
        String[] ydigit = y.split("\\.");
        int digit = Integer.parseInt(String.valueOf(ydigit[1].charAt(0)));

        if(digit != 0)
            return;
        if(loc.getBlock().getType() == Material.LADDER ||  loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.LAVA)
            return;
        if(p.isFlying()) {
            return;
        }
        loc = p.getLocation();
        loc.setY(loc.getY() - 2);
        if( p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.ELYTRA && loc.getBlock().getType() == Material.AIR) {
            return;
        }

        if(Objects.requireNonNull(e.getTo()).getY() > e.getFrom().getY()) {
                if(playerWeight.get(p.getUniqueId())!= null){
                    float weight = playerWeight.get(p.getUniqueId());
                    boolean disableJump = false;
                    double weight1 = w.calculateWeightLevel1(p);
                    double weight2 = w.calculateWeightLevel2(p);
                    double weight3 = w.calculateWeightLevel3(p);
                    if(weight > weight1 && weight < weight2) {
                        disableJump = getPlugin().getConfig().getBoolean("weight-level-1.disable-jump");
                    }else if(weight > weight2 && weight < weight3) {
                        disableJump = getPlugin().getConfig().getBoolean("weight-level-2.disable-jump");
                    }else if(weight > weight3) {
                        disableJump = getPlugin().getConfig().getBoolean("weight-level-3.disable-jump");
                    }
                    if(disableJump) {
                    e.getTo().setY(e.getFrom().getY());
                    if(!jumpMessage.containsKey(p.getUniqueId())) {
                        jumpMessage(p);
                    }else{
                        long timeElapsed = System.currentTimeMillis() - jumpMessage.get(p.getUniqueId());
                        if(timeElapsed >= Messages.getMessages().getInt("disable-jump-message-cooldown") * 1000L){
                             jumpMessage(p);
                        }
                    }
                }
            }else{
                w.calculateWeight(p);
            }
        }
    }

    private void jumpMessage(Player p) {
        jumpMessage.put(p.getUniqueId(), System.currentTimeMillis());
        String message = Messages.getMessages().getString("disable-jump-message");
        if(getPlugin().getConfig().getBoolean("actionbar-messages"))
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p))));
        else
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message,p)));
    }

    //check if the player is on a disabled world or is on creative or spectator mode
    private boolean checkIfEnable(Player p) {
        return isEnabled(p);
    }

    private boolean isWorldGuardEnabled(Player p) {
        if(getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardRegion worldguard = new WorldGuardRegion();
            return worldguard.isInRegion(p);
        }
        return false;
    }




    private boolean checkBlocks(Location loc) {
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

    private void putWeightValue(Player p, ItemStack item, int amount,String s) {
        isPluginEnabled = checkIfEnable(p);
        if(!isPluginEnabled)
            return;
        float weight = 0;
        if (playerWeight.get(p.getUniqueId()) != null && s.equalsIgnoreCase("place")) {
            weight = playerWeight.get(p.getUniqueId());
            weight -= globalItemsWeight.get(item.getType()) * amount;
        }else if (playerWeight.get(p.getUniqueId()) != null && s.equalsIgnoreCase("pick")) {
            weight = playerWeight.get(p.getUniqueId());
            weight += globalItemsWeight.get(item.getType()) * amount;
        }
        playerWeight.put(p.getUniqueId(), weight);

    }

    private void message(Player p, String action, ItemStack item, float weight, int amount){
        if(p.hasPermission("weight.bypass"))
            return;
        String message = "";
        if (action.equalsIgnoreCase("receive"))
            message = Messages.getMessages().getString("receive-item-message");
        else if(action.equalsIgnoreCase("lose"))
            message = Messages.getMessages().getString("lost-item-message");
        else if(action.equalsIgnoreCase("place"))
            message = Messages.getMessages().getString("place-block-message");
        if(!pickMessage.containsKey(p.getUniqueId()) && action.equalsIgnoreCase("receive") && Messages.getMessages().getBoolean("receive-item-message-enabled")) {
            pickMessage.put(p.getUniqueId(), System.currentTimeMillis());
            messageSender(message, p, item, weight, amount);
        }else if(action.equalsIgnoreCase("receive") && Messages.getMessages().getBoolean("receive-item-message-enabled")){
            long timeElapsed = System.currentTimeMillis() - pickMessage.get(p.getUniqueId());
            if(timeElapsed >= Messages.getMessages().getDouble("receive-item-message-cooldown") * 1000){
                pickMessage.put(p.getUniqueId(), System.currentTimeMillis());
                messageSender(message, p, item, weight, amount);
            }
        }
        if(!placeMessage.containsKey(p.getUniqueId()) && action.equalsIgnoreCase("place") && Messages.getMessages().getBoolean("place-block-message-enabled")) {
            placeMessage.put(p.getUniqueId(), System.currentTimeMillis());
            messageSender(message, p, item, weight, amount);
        }else if(action.equalsIgnoreCase("place") && Messages.getMessages().getBoolean("place-block-message-enabled")){
            long timeElapsed = System.currentTimeMillis() - placeMessage.get(p.getUniqueId());
            if(timeElapsed >= Messages.getMessages().getDouble("place-block-message-cooldown") * 1000){
                placeMessage.put(p.getUniqueId(), System.currentTimeMillis());
                messageSender(message, p, item, weight, amount);
            }
        }if(!dropMessage.containsKey(p.getUniqueId()) && action.equalsIgnoreCase("lose") && Messages.getMessages().getBoolean("lost-item-message-enabled")) {
            dropMessage.put(p.getUniqueId(), System.currentTimeMillis());
            messageSender(message, p, item, weight, amount);
        }else if(action.equalsIgnoreCase("lose") && Messages.getMessages().getBoolean("lost-item-message-enabled")){
            long timeElapsed = System.currentTimeMillis() - dropMessage.get(p.getUniqueId());
            if(timeElapsed >= Messages.getMessages().getDouble("lost-item-message-cooldown") * 1000){
                dropMessage.put(p.getUniqueId(), System.currentTimeMillis());
                messageSender(message, p, item, weight, amount);
            }
        }
    }

    private void messageSender(String message, Player p, ItemStack item, float weight, int amount ){
        if(message == null)
            return;
        if(getPlugin().getConfig().getBoolean("actionbar-messages")) {
            message = getPlaceholders(message, item, weight, amount);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', w.messageSender(message, p))));
        }else {
            message = getPlaceholders(message, item, weight, amount);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.messageSender(message, p)));
        }
    }

    private String getPlaceholders(String message, ItemStack item, float weight, int amount) {
        message = message.replaceAll("%block%", String.valueOf(item.getType()));
        if(!Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(""))
            message = message.replaceAll("%itemdisplayname%", item.getItemMeta().getDisplayName());
        else
            message = message.replaceAll("%itemdisplayname%", String.valueOf(item.getType()));
        message = message.replaceAll("%itemweight%", String.format("%.2f", weight));
        message = message.replaceAll("%amount%", String.valueOf(amount));
        message = message.replaceAll("%totalweight%", String.format("%.2f", weight*amount));
        message = message.replaceAll("_", " ");
        return message;
    }

    private void notifyAdmins(ItemStack item) {
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
        for (Player player : players) {
            if (player.hasPermission("weight.notify")) {
                if (!notifyMessage.containsKey(player.getUniqueId())) {
                    player.sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually." +
                            " You can use the /weight add command.");
                    notifyMessage.put(player.getUniqueId(), System.currentTimeMillis());
                } else {
                    long timeElapsed = System.currentTimeMillis() - notifyMessage.get(player.getUniqueId());
                    if (timeElapsed >= getPlugin().getConfig().getLong("notify-permission-cooldown") * 1000) {
                        player.sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually." +
                                " You can use the /weight add command.");
                        notifyMessage.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }

            }
        }
    }




}
