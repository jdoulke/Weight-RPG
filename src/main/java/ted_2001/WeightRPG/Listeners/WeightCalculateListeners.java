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
import org.bukkit.inventory.meta.ItemMeta;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.ColorUtils;
import ted_2001.WeightRPG.Utils.Messages;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.Utils.CalculateWeight.*;
import static ted_2001.WeightRPG.Utils.JsonFile.*;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


/**
 * WeightCalculateListeners is a listener class responsible for handling events related to weight calculations.
 */
public class WeightCalculateListeners implements Listener {



    CalculateWeight weightCalculation = new CalculateWeight();

    //HashMaps for messages Cooldown
    public final HashMap<UUID, Long> jumpMessage = new HashMap<>();
    public final HashMap<UUID, Long> pickMessage = new HashMap<>();
    public final HashMap<UUID, Long> placeMessage = new HashMap<>();
    public final HashMap<UUID, Long> dropMessage = new HashMap<>();

    private final HashMap<UUID, Long> notifyMessage = new HashMap<>();
    private final HashMap<UUID, Long> dropCooldown = new HashMap<>();

    private final String pluginPrefix = org.bukkit.ChatColor.GRAY + "[" + org.bukkit.ChatColor.YELLOW + "Weight-RPG" + org.bukkit.ChatColor.GRAY + "] ";

    boolean isPluginEnabled = false;

    // Handles the event when a player closes their inventory.
    // Calculates and updates the player's weight if the Weight-RPG plugin is enabled for the player's world and region.
    @EventHandler (priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent e){

        Player p = (Player) e.getPlayer();

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        weightCalculation.calculateWeight(p);
    }

    // Handles the event when a player joins the server.
    // Calculates and updates the player's weight if the Weight-RPG plugin is enabled for the player's world and region.
    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e){

        Player p = e.getPlayer();

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        weightCalculation.calculateWeight(p);
    }

    // Handles the event when a player respawns.
    // Calculates and updates the player's weight if the Weight-RPG plugin is enabled for the player's world and region.
    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent e){

        Player p = e.getPlayer();

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        weightCalculation.calculateWeight(p);
    }

    // Handles the event when a player change world.
    // Calculates and updates the player's weight if the Weight-RPG plugin is enabled for the player's world and region.
    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e){
    
        Player p = e.getPlayer();

        // Check if the plugin is enabled for the player's world or if the player is in Creative or Spectator mode.
        if (checkIfEnable(p))
            return;

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        weightCalculation.calculateWeight(p);
    }


    // Handles the event when a player's gamemode changes.
    // If the player switches to Creative or Spectator mode, their walk speed is set to the default speed value.
    // This ensures that players in Creative or Spectator mode do not have weight restrictions.
    @EventHandler (priority = EventPriority.LOWEST)
    public void onGamemodeChange(PlayerGameModeChangeEvent e){

        Player p =e.getPlayer();

        if(e.getNewGameMode().toString().equalsIgnoreCase("CREATIVE") || e.getNewGameMode().toString().equalsIgnoreCase("SPECTATOR"))
            p.setWalkSpeed(0.2f);
        
    }

    // Handles the event when a player picks up an item from the ground.
    // Calculates the weight of the item and checks if the player can carry it based on their current weight capacity.
    // Notifies the player about the pickup and applies weight effects if necessary.
    @EventHandler (priority = EventPriority.HIGH)
    public void onItemPickUp(EntityPickupItemEvent e){

        if(e.getEntity() instanceof Player){

            Player p = ((Player) e.getEntity()).getPlayer();
            assert p != null;

            // Check if the plugin is enabled for the player's world or if the player is in Creative or Spectator mode.
            if (!checkIfEnable(p))
                return;

            // Check if WorldGuard protection is enabled and if the player is in a protected region.
            if (isWorldGuardEnabled(p))
                return;

            // Get the item stack being picked up and its amount.
            ItemStack item = e.getItem().getItemStack();
            int amount = e.getItem().getItemStack().getAmount();

            // Check if the item is defined in the weight files.
            if (globalItemsWeight.get(item.getType()) == null) {
                // The item is not in the weight files. Notify the console and administrators (players with weight.notify permission).
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually.");
                notifyAdmins(item);
                return;
            }

            boolean disablePickUpLevel1 = getPlugin().getConfig().getBoolean("weight-level-1.disable-pick-up", false);
            boolean disablePickUpLevel2 = getPlugin().getConfig().getBoolean("weight-level-2.disable-pick-up", false);
            boolean disablePickUpLevel3 = getPlugin().getConfig().getBoolean("weight-level-3.disable-pick-up", false);

            if ((disablePickUpLevel1 && playerWeight.get(p.getUniqueId()) >= weightCalculation.calculateWeightThreshold(p, 1))
            || (disablePickUpLevel2 && playerWeight.get(p.getUniqueId()) >= weightCalculation.calculateWeightThreshold(p, 2))
            || (disablePickUpLevel3 && playerWeight.get(p.getUniqueId()) >= weightCalculation.calculateWeightThreshold(p, 3))) {
                e.setCancelled(true);
                return;
            }

            float weight = 0.0f;
            float boostWeight;
            boolean isCustomItem = false;
            ItemMeta itemMeta = item.getItemMeta();

            // Check if the item is a custom-named item with weight defined in the config file.
            if (itemMeta != null && customItemsWeight.containsKey(itemMeta.getDisplayName())) {
                weight = customItemsWeight.get(itemMeta.getDisplayName());
                isCustomItem = true;
            } else if(itemMeta != null && boostItemsWeight.containsKey(itemMeta.getDisplayName())){
                boostWeight = playerBoostWeight.getOrDefault(p.getUniqueId(), 0f);
                if(boostWeight != 0)
                    boostWeight +=  (boostItemsWeight.get(itemMeta.getDisplayName()) * amount);
                playerBoostWeight.put(p.getUniqueId(), boostWeight);
                weight = 0.0f;
            }else if (globalItemsWeight.get(item.getType()) != null) 
                weight = globalItemsWeight.get(item.getType());
            
            // Check if the player's weight is not being tracked yet or if the item is a custom item.
            if (playerWeight.get(p.getUniqueId()) == 0 || playerWeight.get(p.getUniqueId()) == null || isCustomItem) {
                // Calculate the player's weight and notify them about the received item.
                weightCalculation.calculateWeight(p);
                message(p, "receive", item, weight, amount);
            } else {
                // The player's weight is already being tracked. Update the weight for the picked item.
                String s = "pick";
                putWeightValue(p, item, amount, s);
                message(p, "receive", item, weight, amount);
                weightCalculation.applyWeightEffects(p);
            }
        }
    }


    // Handles the PlayerDropItemEvent, which occurs when a player drops an item or items from their inventory.
    @EventHandler (priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent e){

        Player p = e.getPlayer();

        // Check if the plugin is enabled for the player's world or if the player is in Creative or Spectator mode.
        isPluginEnabled = checkIfEnable(p);
        if (!isPluginEnabled)
            return;

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        // Check if the player has the "weight.bypass" permission, exempting them from weight restrictions.  
        if(p.hasPermission("weight.bypass"))
            return;

         // Handle item drop cooldown, if enabled in the plugin's configuration.
        if(getPlugin().getConfig().getBoolean("drop-cooldown.enabled")){

            if(!dropCooldown.containsKey(p.getUniqueId())) 
                dropCooldown.put(p.getUniqueId(), System.currentTimeMillis());
            else {
                long timeElapsed = System.currentTimeMillis() - dropCooldown.get(p.getUniqueId());

                if(timeElapsed < getPlugin().getConfig().getDouble("drop-cooldown.cooldown") * 1000){

                    // Prevent item drop and send a cooldown message to the player.
                    e.setCancelled(true);

                    String message = Messages.getMessages().getString("drop-cooldown-message");
                    assert message != null;
                    message = weightCalculation.formatMessage(message, p);

                    // Check if action bar messages are enabled in the plugin's configuration
                    if(getPlugin().getConfig().getBoolean("actionbar-messages")){
                        // Send the formatted message to the player's action bar if action bar messages are enabled
                        BaseComponent[] actionBarMessage = TextComponent.fromLegacyText(ColorUtils.translateColorCodes(message));
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
                    }else
                        // Send the formatted message as a regular chat message if action bar messages are not enabled
                        p.sendMessage(ColorUtils.translateColorCodes(message));

                    return;

                }else
                    dropCooldown.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }


        // Retrieve the item being dropped and its amount.
        ItemStack item = e.getItemDrop().getItemStack();
        int amount = e.getItemDrop().getItemStack().getAmount();

        // Check if the item is defined in the weight files.
        if (globalItemsWeight.get(item.getType()) == null) {
            // The item is not in the weight files. Notify the console and administrators (players with weight.notify permission).
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually.");
            notifyAdmins(item);
            return;
        }


        float weight = 0.0f;
        float boostWeight;
        boolean isCustomItem = false;
        ItemMeta itemMeta = item.getItemMeta();

        // Check if the item is a custom-named item with weight defined in the config file.
        if (itemMeta != null && customItemsWeight.containsKey(itemMeta.getDisplayName())) {
            weight = customItemsWeight.get(itemMeta.getDisplayName());
            isCustomItem = true;
        } else if(itemMeta != null && boostItemsWeight.containsKey(itemMeta.getDisplayName())){
            boostWeight = playerBoostWeight.getOrDefault(p.getUniqueId(), 0f);
            if(boostWeight != 0)
                boostWeight -= (boostItemsWeight.get(itemMeta.getDisplayName()) * amount);
            playerBoostWeight.put(p.getUniqueId(), boostWeight);
            weight = 0.0f;
        }else if(globalItemsWeight.get(item.getType()) != null)
            weight = globalItemsWeight.get(item.getType());

        // Handle weight calculation and weight effects for the player based on the dropped item.
        if (playerWeight.get(p.getUniqueId()) == 0 || playerWeight.get(p.getUniqueId()) == null || isCustomItem) {
            // Calculate the player's weight and notify them about the item loss.
            weightCalculation.calculateWeight(p);
            message(p, "lose", item, weight, amount);
        } else {
            // Update the player's weight and notify them about the item loss.
            String s = "place";
            putWeightValue(p, item, amount, s);
            message(p, "lose", item, weight, amount);
            weightCalculation.applyWeightEffects(p);
        }
    }

    // Handles the BlockPlaceEvent, which occurs when a player places a block.
    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerBlockPlace(BlockPlaceEvent e){

        Player p = e.getPlayer();

        // Check if the plugin is enabled for the player's world or if the player is in Creative or Spectator mode.
        isPluginEnabled = checkIfEnable(p);
        if (!isPluginEnabled)
            return;

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        // Get the block being placed.    
        ItemStack block = new ItemStack(e.getBlock().getType());

        // Check if the placed block is defined in the weight files.
        if (globalItemsWeight.get(block.getType()) == null) {
            // If the block is FIRE, do nothing as it is exempt from weight tracking.
            if (block.getType().toString().equalsIgnoreCase("FIRE"))
                return;

            // The item is not in the weight files. Notify the console and administrators (players with weight.notify permission).
            getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + block.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually.");
            notifyAdmins(block);
            return;
        }

        float weight = 0.0f;
        float boostWeight;
        boolean isCustomItem = false;
        ItemMeta itemMeta = block.getItemMeta();

        // Check if the item is a custom-named item with weight defined in the config file.
        if (itemMeta != null && customItemsWeight.containsKey(itemMeta.getDisplayName())) {
            weight = customItemsWeight.get(itemMeta.getDisplayName());
            isCustomItem = true;
        } else if(itemMeta != null && boostItemsWeight.containsKey(itemMeta.getDisplayName())){
            boostWeight = playerBoostWeight.getOrDefault(p.getUniqueId(), 0f);
            if(boostWeight != 0)
                boostWeight -= boostItemsWeight.get(itemMeta.getDisplayName());
            playerBoostWeight.put(p.getUniqueId(), boostWeight);
            weight = 0.0f;
        }else if(globalItemsWeight.get(block.getType()) != null)
            weight = globalItemsWeight.get(block.getType());

        // Handle weight calculation and weight effects for the player based on the placed block.
        if (playerWeight.get(p.getUniqueId()) == 0 || playerWeight.get(p.getUniqueId()) == null || isCustomItem) {
            // Calculate the player's weight and notify them about the placed block.
            weightCalculation.calculateWeight(p);
            message(p, "place", block, weight, 1);
        } else {
            // Update the player's weight and notify them about the placed block.
            String s = "place";
            putWeightValue(p, block, 1, s);
            message(p, "place", block, weight, 1);
            weightCalculation.applyWeightEffects(p);
        }
    }

    // Handles the PlayerMoveEvent, which occurs when a player moves from one location to another.
    // This method specifically checks for player jumping behavior and applies weight-related restrictions if necessary.
    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerJump(PlayerMoveEvent e){

        Player p = e.getPlayer();

         // Check if the plugin is enabled for the player's world or if the player is in Creative or Spectator mode.
        isPluginEnabled = checkIfEnable(p);
        if (!isPluginEnabled)
            return;

        // Check if WorldGuard protection is enabled and if the player is in a protected region.
        if (isWorldGuardEnabled(p))
            return;

        // Check if the player has the "weight.bypass" permission, exempting them from weight restrictions.
        if(p.hasPermission("weight.bypass"))
            return;

        // Check if the player's walk speed is 0.
        if(p.getWalkSpeed() == 0)
            // If the player's walk speed is 0, player can't move.
            if(Objects.requireNonNull(e.getTo()).getX() != e.getFrom().getX() || Objects.requireNonNull(e.getTo()).getZ() != e.getFrom().getZ())
                e.setCancelled(true);

        // Get the player's location.
        Location loc = p.getLocation();

        
        if(checkBlocks(loc))
            return;

        // Get the digit after the decimal point in the player's Y coordinate.    
        String y = String.valueOf(p.getLocation().getY());
        String[] ydigit = y.split("\\.");
        int digit = Integer.parseInt(String.valueOf(ydigit[1].charAt(0)));

        // If the digit after the decimal point is not 0, the player is not on a block's surface and jump restrictions don't apply.
        if (digit != 0)
            return;

        // Check if the player is on specific block types where jump restrictions don't apply (e.g., ladder, water, lava).
        if (loc.getBlock().getType() == Material.LADDER || loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.LAVA)
            return;

        // Check if the player is flying, and if so, jump restrictions don't apply.
        if (p.isFlying()) 
            return;


        // Adjust the location to the block two blocks below the player's current location.
        loc = p.getLocation();
        loc.setY(loc.getY() - 2);

        // Check if the player is wearing an Elytra chestplate and if there is an open airspace to allow flying with them.
        if (p.getInventory().getChestplate() != null && p.getInventory().getChestplate().getType() == Material.ELYTRA && loc.getBlock().getType() == Material.AIR) 
            return;
        
         // Perform jump restrictions based on the player's weight and the configured weight levels.
        if (Objects.requireNonNull(e.getTo()).getY() > e.getFrom().getY()) {

            // If the player is moving upwards (jumping), perform weight-based jump restrictions.
            if (playerWeight.get(p.getUniqueId()) != null) {
                float weight = playerWeight.get(p.getUniqueId());
                boolean disableJump = false;

                // Calculate the weight levels used for jump restriction configuration.
                double weight1 = weightCalculation.calculateWeightThreshold(p,1);
                double weight2 = weightCalculation.calculateWeightThreshold(p,2);
                double weight3 = weightCalculation.calculateWeightThreshold(p,3);

                // Check if the player's weight falls within specific weight levels and disable jumping if needed.
                if (weight > weight1 && weight < weight2) 
                    disableJump = getPlugin().getConfig().getBoolean("weight-level-1.disable-jump");
                else if (weight > weight2 && weight < weight3) 
                    disableJump = getPlugin().getConfig().getBoolean("weight-level-2.disable-jump");
                else if (weight > weight3) 
                    disableJump = getPlugin().getConfig().getBoolean("weight-level-3.disable-jump");
                

                if (disableJump) {
                    // If jumping is disabled due to weight, set the player's Y coordinate back to the previous value, preventing the jump.
                    e.getTo().setY(e.getFrom().getY());

                    // Notify the player about the jump restriction.
                    if (!jumpMessage.containsKey(p.getUniqueId())) {
                        jumpMessage(p);
                    } else {
                        long timeElapsed = System.currentTimeMillis() - jumpMessage.get(p.getUniqueId());
                        if (timeElapsed >= Messages.getMessages().getInt("disable-jump-message-cooldown") * 1000L) {
                            jumpMessage(p);
                        }
                    }
                }
            }
        } else 
            weightCalculation.calculateWeight(p);
    }

    // Sends a jump restriction message to the player and records the timestamp for cooldown.
    // This method notifies the player about the jump restriction due to their weight and sets the cooldown for the message.
    private void jumpMessage(Player p) {
        // Record the current time as the timestamp for jump restriction message cooldown.
        jumpMessage.put(p.getUniqueId(), System.currentTimeMillis());

        // Get the jump restriction message from the messages configuration.
        String message = Messages.getMessages().getString("disable-jump-message");

        // Check if action bar messages are enabled in the plugin's configuration
        if(getPlugin().getConfig().getBoolean("actionbar-messages"))
            // Send the formatted message to the player's action bar if action bar messages are enabled
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorUtils.translateColorCodes(weightCalculation.formatMessage(message,p))));
        else
            // Send the formatted message as a regular chat message if action bar messages are not enabled
            p.sendMessage(ColorUtils.translateColorCodes( weightCalculation.formatMessage(message,p)));
    }

    // Checks if the player is in an enabled world and not in Creative or Spectator mode.
    // This method determines if the plugin is enabled for the player's current world or if the player has Creative/Spectator mode.
    private boolean checkIfEnable(Player p) {
        return isEnabled(p);
    }

    // Checks if WorldGuard protection is enabled and if the player is in a protected region.
    // This method determines if the WorldGuard plugin is enabled and if the player is in a WorldGuard-protected region.
    private boolean isWorldGuardEnabled(Player p) {

        if(getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            WorldGuardRegion worldguard = new WorldGuardRegion();
            return worldguard.isInRegion(p);
        }

        return false;
    }

    // Checks if the player is standing on certain block types where jump restrictions don't apply.
    // This method examines the blocks around the player's location to see if they are on blocks like stairs or slabs.
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

    // Updates the weight of a player based on the action (place or drop and pick) and the item's weight.
    private void putWeightValue(Player p, ItemStack item, int amount,String s) {

        // Check if the plugin is enabled for the player's world or if the player is in Creative or Spectator mode.
        isPluginEnabled = checkIfEnable(p);
        if (!isPluginEnabled)
            return;

        float weight = 0;
        // Check the action type (place or pick) and update the player's weight accordingly.
        if (playerWeight.get(p.getUniqueId()) != null && s.equalsIgnoreCase("place")) {
            // If the action is 'place', subtract the weight of the item multiplied by the quantity from the player's weight.
            weight = playerWeight.get(p.getUniqueId());
            weight -= globalItemsWeight.get(item.getType()) * amount;
        } else if (playerWeight.get(p.getUniqueId()) != null && s.equalsIgnoreCase("pick")) {
            // If the action is 'pick', add the weight of the item multiplied by the quantity to the player's weight.
            weight = playerWeight.get(p.getUniqueId());
            weight += globalItemsWeight.get(item.getType()) * amount;
        }
        // Update the player's weight in the playerWeight map.
        playerWeight.put(p.getUniqueId(), weight);

    }

    // Sends weight-related messages to the player based on the given action (receive, lose, place) and item information.
    private void message(Player p, String action, ItemStack item, float weight, int amount){

        // Check if the player has the 'weight.bypass' permission, which allows skipping messages.
        if (p.hasPermission("weight.bypass"))
            return;

        String message = "";

        // Determine the appropriate message type based on the action.
        if (action.equalsIgnoreCase("receive"))
            message = Messages.getMessages().getString("receive-item-message");
        else if (action.equalsIgnoreCase("lose"))
            message = Messages.getMessages().getString("lost-item-message");
        else if (action.equalsIgnoreCase("place"))
            message = Messages.getMessages().getString("place-block-message");

        // Handle the cooldown for each message type to prevent spamming.
        // Receive item message
        if (!pickMessage.containsKey(p.getUniqueId()) && action.equalsIgnoreCase("receive") && Messages.getMessages().getBoolean("receive-item-message-enabled")) {
            pickMessage.put(p.getUniqueId(), System.currentTimeMillis());
            messageSender(message, p, item, weight, amount);
        } else if (action.equalsIgnoreCase("receive") && Messages.getMessages().getBoolean("receive-item-message-enabled")) {
            long timeElapsed = System.currentTimeMillis() - pickMessage.get(p.getUniqueId());
            if (timeElapsed >= Messages.getMessages().getDouble("receive-item-message-cooldown") * 1000) {
                pickMessage.put(p.getUniqueId(), System.currentTimeMillis());
                messageSender(message, p, item, weight, amount);
            }
        }

        // Place block message
        if(!placeMessage.containsKey(p.getUniqueId()) && action.equalsIgnoreCase("place") && Messages.getMessages().getBoolean("place-block-message-enabled")) {
            placeMessage.put(p.getUniqueId(), System.currentTimeMillis());
            messageSender(message, p, item, weight, amount);
        }else if(action.equalsIgnoreCase("place") && Messages.getMessages().getBoolean("place-block-message-enabled")){
            long timeElapsed = System.currentTimeMillis() - placeMessage.get(p.getUniqueId());
            if(timeElapsed >= Messages.getMessages().getDouble("place-block-message-cooldown") * 1000){
                placeMessage.put(p.getUniqueId(), System.currentTimeMillis());
                messageSender(message, p, item, weight, amount);
            }
        }
        // Lose item message
        if(!dropMessage.containsKey(p.getUniqueId()) && action.equalsIgnoreCase("lose") && Messages.getMessages().getBoolean("lost-item-message-enabled")) {
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
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorUtils.translateColorCodes(weightCalculation.formatMessage(message, p))));
        }else {
            message = getPlaceholders(message, item, weight, amount);
            p.sendMessage(ColorUtils.translateColorCodes(weightCalculation.formatMessage(message, p)));
        }
    }

    // Replaces placeholders in the given message with actual item-related information.
    private String getPlaceholders(String message, ItemStack item, float weight, int amount) {

        message = message.replaceAll("%block%", String.valueOf(item.getType()));

        // Replace the %itemdisplayname% placeholder with the display name of the item if available, or the material type otherwise.
        if(!Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(""))
            message = message.replaceAll("%itemdisplayname%", item.getItemMeta().getDisplayName());
        else
            message = message.replaceAll("%itemdisplayname%", String.valueOf(item.getType()));

        message = message.replaceAll("%itemweight%", String.format("%.2f", weight));
        message = message.replaceAll("%amount%", String.valueOf(amount));

        // Replace the %totalweight% placeholder with the total weight (weight * amount) of the items, formatted to two decimal places.
        message = message.replaceAll("%totalweight%", String.format("%.2f", weight*amount));

        // Replace underscores with spaces to make the message more readable.
        message = message.replaceAll("_", " ");
        return message;
    }

    // Notifies online admins with "weight.notify" permission that the specified item is not present in the weight files.
    // Administrators are informed through a message about the missing item and are provided with a command to add it manually.
    private void notifyAdmins(ItemStack item) {

        // Get a list of all online players on the server.
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();

        // Loop through each player to check for "weight.notify" permission.
        for (Player player : players) {

            if (player.hasPermission("weight.notify")) {
                // If the player has "weight.notify" permission and hasn't been notified recently, send the notification.
                if (!notifyMessage.containsKey(player.getUniqueId())) {

                    player.sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually." +
                            " You can use the /weight add command.");

                    // Store the current time to keep track of the notification cooldown for this player.
                    notifyMessage.put(player.getUniqueId(), System.currentTimeMillis());

                } else {
                    // If the player has "weight.notify" permission but was already notified, check if enough time has passed to notify them again.
                    long timeElapsed = System.currentTimeMillis() - notifyMessage.get(player.getUniqueId());
                    long cooldown = getPlugin().getConfig().getLong("notify-permission-cooldown") * 1000;

                    // If the cooldown has passed, send the notification again and update the notification time.
                    if (timeElapsed >= cooldown) {
                        player.sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually." +
                                " You can use the /weight add command.");

                        // Store the current time to reset the notification cooldown for this player.
                        notifyMessage.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
            }
        }
    }

}
