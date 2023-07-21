package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.Utils.JsonFile.*;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class CalculateWeight {

    public static HashMap<UUID, Float> playerWeight = new HashMap<>();
    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    boolean Weight2 = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
    boolean Weight3 = getPlugin().getConfig().getBoolean("weight-level-3.enabled");

    boolean isWorldGuardEnabled = getServer().getPluginManager().isPluginEnabled("WorldGuard");


    // Array to store weight threshold values for different levels
    public static float[] weightThresholdValues = new float[]{
            (float) getPlugin().getConfig().getDouble("weight-level-1.value"),
            (float) getPlugin().getConfig().getDouble("weight-level-2.value"),
            (float) getPlugin().getConfig().getDouble("weight-level-3.value")
    };

    // Color codes for the progress bar
    private static final String whiteColor = "&f&l";
    private static final String darkRedColor = "&4&l";
    private static final String[] colorCodes = {"&a&l", "&2&l", "&e&l", "&6&l", "&c&l"}; // Light Green, Green, Yellow, Orange, Red

    // Method to calculate player's weight and apply effects
    public void calculateWeight(Player p) {

        if (!isEnabled(p)) // // Check if weight calculation is enabled for the player in his current world and gamemode
            return;

        if (isWorldGuardEnabled) { // Check if WorldGuard is enabled and if player is in a region
            WorldGuardRegion worldguard = new WorldGuardRegion();
            if (worldguard.isInRegion(p))
                return; // If the player is in a WorldGuard region, do not calculate weight
        }

        if (p.hasPermission("weight.bypass")) { // Check if player has a bypass permission
            playerWeight.put(p.getUniqueId(), 0f); // Set player's weight to 0 if they have a bypass permission
            if (p.getWalkSpeed() < 0.2f)
                p.setWalkSpeed(0.2f); // Set player's walk speed to a minimum value if they have a bypass permission
            return;
        }

        PlayerInventory inventory = p.getInventory();
        float totalWeight = 0f;

        // Calculate weight for each item in player's inventory and armor slots
        for (ItemStack item : inventory.getStorageContents()) {
            if (item != null) {
                totalWeight += itemWeightCalculations(item);
            }
        }

        for (ItemStack itemStack : inventory.getArmorContents()) {
            if (itemStack != null) {
                totalWeight += itemWeightCalculations(itemStack);
            }
        }

        ItemStack offHandItem = inventory.getItemInOffHand();
        if (offHandItem != null) {
            totalWeight += itemWeightCalculations(offHandItem);
        }
        // Update player's weight in the HashMap
        playerWeight.put(p.getUniqueId(), PlayerWeight);
        applyWeightEffects(p); // Apply effects based on player's weight
    }


    private float  itemWeightCalculations(ItemStack itemStack) {

        float itemWeight = 0.0f;
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            String displayName = itemMeta.getDisplayName();
            
            // Check if the item has a custom weight based on its display name from config file
            if (customItemsWeight.containsKey(displayName)) 
                itemWeight = customItemsWeight.get(displayName);

            // Check if the item is a boost item weight based on its display name from config file
            else if (boostItemsWeight.containsKey(displayName)) 
                // Boost items don't add weight to the player.
                return 0.0f;

            // Check if the item has a global weight based on its material type
            else if (globalItemsWeight.containsKey(itemStack.getType())) 
                itemWeight = globalItemsWeight.get(itemStack.getType());            
            

        return itemWeight * itemStack.getAmount();
    }

    public void applyWeightEffects(Player p) {
        UUID id = p.getUniqueId();

        // If the player's weight is not calculated yet, return
        if (playerWeight.get(id) == null)
            return;

        float weight = playerWeight.get(id);

        // Calculate weight thresholds for different levels
        double weight1 = calculateWeightLevel1(p);
        double weight2 = calculateWeightLevel2(p);
        double weight3 = calculateWeightLevel3(p);

        // Get walk speed values from plugin's configuration for each weight level
        float walkSpeedLevel1 = (float) getPlugin().getConfig().getDouble("weight-level-1.speed");
        float walkSpeedLevel2 = (float) getPlugin().getConfig().getDouble("weight-level-2.speed");
        float walkSpeedLevel3 = (float) getPlugin().getConfig().getDouble("weight-level-3.speed");

        String message;

        // Check player's weight level and adjust walk speed and display messages accordingly
        if (weight < weight1) {

            // If the player's weight is below to weight level 1 threshold
            // and their walk speed is not already at the normal speed, set it to normal speed
            if (p.getWalkSpeed() < 0.2 || p.getWalkSpeed() == walkSpeedLevel2 || p.getWalkSpeed() == walkSpeedLevel3 || p.getWalkSpeed() == walkSpeedLevel1)
                p.setWalkSpeed(0.2f);

            // Display a message if the message before level 1 is enabled in the plugin's configuration
            if (getPlugin().getConfig().getBoolean("message-before-level1-enabled")) {
                message = getPlugin().getConfig().getString("message-before-level1");
                sendMessage(message, p, null);
            }
        } else if (weight >= weight1 && weight < weight2) {

            // If the player's weight is between weight level 1 and weight level 2 thresholds
            // and his walk speed is faster than walkingspeed1 or equals to the others 2 levels, set it to the weight level 1 value
            if (p.getWalkSpeed() > walkSpeedLevel1 || p.getWalkSpeed() == walkSpeedLevel2 || p.getWalkSpeed() == walkSpeedLevel3)
                p.setWalkSpeed(walkSpeedLevel1);

            // Display a message if the weight level 1 message is enabled in the plugin's configuration
            if (getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
                message = getPlugin().getConfig().getString("weight-level-1.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-1.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                sendMessage(message, p, s);
            }
        } else if (weight >= weight2 && weight < weight3 && Weight2) {

            // If the player's weight is between weight level 2 and weight level 3 thresholds
            // and the weight level 2 feature is enabled
            // and his walk speed is faster than walkingspeed2 or equals to the others 2 levels, set it to the weight level 2 value
            if (p.getWalkSpeed() > walkSpeedLevel2 || p.getWalkSpeed() == walkSpeedLevel1 || p.getWalkSpeed() == walkSpeedLevel3)
                p.setWalkSpeed(walkSpeedLevel2);

            // Display a message if the weight level 2 message is enabled in the plugin's configuration
            if (getPlugin().getConfig().getBoolean("weight-level-2.message-enabled")) {
                message = getPlugin().getConfig().getString("weight-level-2.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-2.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                sendMessage(message, p, s);
            }
        } else if (weight >= weight3 && Weight3) {
            // If the player's weight is above or equal to weight level 3 threshold
            // and the weight level 3 feature is enabled
            // and his walk speed is faster than walkingspeed3 or equals to the others 2 levels, set it to the weight level 3 value
            if (p.getWalkSpeed() > walkSpeedLevel3 || p.getWalkSpeed() == walkSpeedLevel1 || p.getWalkSpeed() == walkSpeedLevel2)
                p.setWalkSpeed(walkSpeedLevel3);

            // Display a message if the weight level 3 message is enabled in the plugin's configuration
            if (getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
                message = getPlugin().getConfig().getString("weight-level-3.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-3.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                sendMessage(message, p, s);
            }
        }
    }

    private void sendMessage(String message, Player p, Sound s) {
        if (!cooldown.containsKey(p.getUniqueId())) {
            // If the player's cooldown does not exist, send the message and set the cooldown
            cooldownMessenger(p, s, message);
        } else {
            // If the player's cooldown exists, check if enough time has passed since the last message was sent
            long timeElapsed = System.currentTimeMillis() - cooldown.get(p.getUniqueId());
        
            // Convert messages cooldown from seconds to milliseconds
            double messagesCooldown = getPlugin().getConfig().getDouble("messages-cooldown") * 1000;

            if (timeElapsed >= messagesCooldown) {
                // If enough time has passed since the last message, send the message and update the cooldown
                cooldownMessenger(p, s, message);
            }
        }
    }


    // Check if weight calculation is enabled for the player in their current world and game mode
    public static boolean isEnabled(Player p) {
        // Retrieve the list of disabled worlds from the plugin's configuration
        List<String> disabledWorlds = getPlugin().getConfig().getStringList("disabled-worlds");

        // Check if the player's current world is in the list of disabled worlds
        for (String disabledWorld : disabledWorlds) {
            if (disabledWorld.equalsIgnoreCase((p.getWorld().getName()))) {
                // If the player's world is disabled, set their walk speed to a minimum value and return false
                p.setWalkSpeed(0.2f);
                return false;
            }
        }
    
        // Check if the player is not in Creative or Spectator game mode, return true otherwise
        String playerGameMode = p.getGameMode().toString();
        return !playerGameMode.equalsIgnoreCase("CREATIVE") && !playerGameMode.equalsIgnoreCase("SPECTATOR");
    }


    private void cooldownMessenger(Player p, Sound s, String message) {
        // Check if the player has the 'weight.bypass' permission to avoid sending messages to players with this permission
        if (!p.hasPermission("weight.bypass")) {
            // Set the player's cooldown by updating the last message time in the 'cooldown' HashMap
            cooldown.put(p.getUniqueId(), System.currentTimeMillis());
        
            // Check if action bar messages are enabled in the plugin's configuration
            if (getPlugin().getConfig().getBoolean("actionbar-messages")) {
                // Send the formatted message to the player's action bar if action bar messages are enabled
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', formatMessage(message, p))));
            } else {
                // Send the formatted message as a regular chat message if action bar messages are not enabled
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', formatMessage(message, p)));
            }
        
            // Check if a sound is specified, and if so, play the sound at the player's location
            if (s != null)
                p.playSound(p.getLocation(), s, 1, 1);
        }   
    }



    // Formats and replaces placeholders in the given message before sending it to the player.
    public String formatMessage(String message, Player p){

        message = message.replaceAll("%playername%", p.getName());
        message = message.replaceAll("%displayname%", p.getDisplayName());

        if(playerWeight.get(p.getUniqueId())!= null)
            message = message.replaceAll("%weight%", String.format("%.2f", playerWeight.getOrDefault(p.getUniqueId(), 0f)));
        else
            message = message.replaceAll("%weight%", "0");

        message = message.replaceAll("%world%", p.getWorld().getName());

        message = message.replaceAll("%level1%", Objects.requireNonNull(String.valueOf(calculateWeightThreshold(p,1))));
        message = message.replaceAll("%level2%", Objects.requireNonNull(String.valueOf(calculateWeightThreshold(p,2))));
        message = message.replaceAll("%level3%", Objects.requireNonNull(String.valueOf(calculateWeightThreshold(p,3))));

        // Replace '%percentageweight%' with the player's weight represented as a progress bar
        message = message.replaceAll("%percentageweight%", generateProgressBar(p));

        // Replace '%percentage%' with the player's weight as a percentage, formatted to two decimal places
        message = message.replaceAll("%percentage%", String.format("%.2f", getPercentage(p)));
        message = message.replaceAll("%pluginprefix%", getPlugin().getPluginPrefix());

        // Replace '%maxweight%' with the maximum weight threshold based on the enabled weight levels (level 1, level 2, or level 3)
        message = message.replaceAll("%maxweight%", String.valueOf(calculateWeightThreshold(player, getEnabledWeightLevel())));

        return message;
    }

    // Calculates and returns the percentage of the player's weight relative to the maximum weight threshold,
    // based on the enabled weight levels (level 1, level 2, or level 3).
    public float getPercentage(Player p){
        if(playerWeight.get(p.getUniqueId()) == null)
            return 0;

        float weight = playerWeight.get(p.getUniqueId());
        float maxWeight = calculateWeightThreshold(player, getEnabledWeightLevel());

        return weight * 100 / maxWeight;
    }

    // Generates a progress bar based on the player's weight relative to the maximum weight threshold
    // and returns the formatted progress bar as a string.
    public String generateProgressBar(Player p) {

        if (p == null)
            return "";

        float weight = playerWeight.getOrDefault(player.getUniqueId(), 0f);
        float maxWeight = calculateWeightThreshold(player, getEnabledWeightLevel());

        // Calculate the percentage of the player's weight relative to the maximum weight threshold
        float percentage = (weight * 100 / maxWeight);

        // Initialize the progress bar with 20 bars ('|') and white color
        StringBuilder message = new StringBuilder(38);
        message.append("&7&l[||||||||||||||||||||&7&l]");

        // Determine the number of colored bars and the corresponding color index
        int coloredBars = ((int) percentage >= 100) ? 20 : (((int) percentage) / 5 + 1);
        int colorIndex = ((int) percentage >= 100) ? 4 : (((int) percentage) / 20);

        // Insert the appropriate color code at the beginning and end of the colored bars
        if ((int) percentage >= 95)
            message.insert(5, darkRedColor);
        else
            message.insert(5, colorCodes[colorIndex]);
        message.insert(9 + coloredBars, whiteColor);

        // Return the formatted progress bar with color codes translated
        return ChatColor.translateAlternateColorCodes('&', message.toString());
    }

 



    // Calculates and returns the weight threshold for level 1 based on the player's permissions.
    // If permission-mode is disabled, the weight threshold value from the 'config.yml' file will be used.
    public float calculateWeightLevel1(Player p){

        PlayerInventory inv = p.getInventory();

        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        ItemStack secondhand = inv.getItemInOffHand();

        float totalExtraWeight = 0.0f;

        for (ItemStack item : items) {
             if (item != null && boostItemsWeight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName()))
                totalExtraWeight += boostItemsWeight.get(item.getItemMeta().getDisplayName());
        }

        for (ItemStack itemStack : armor) {
            if (itemStack != null && boostItemsWeight.containsKey(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName()))
                totalExtraWeight += boostItemsWeight.get(itemStack.getItemMeta().getDisplayName());
        }

        if(secondhand.getItemMeta() != null && boostItemsWeight.containsKey(Objects.requireNonNull(secondhand.getItemMeta()).getDisplayName()))
            totalExtraWeight += boostItemsWeight.get(secondhand.getItemMeta().getDisplayName());

        if (!getPlugin().getConfig().getBoolean("permission-mode")) {
        // If permission-mode is disabled, return the weight threshold value from the 'config.yml' file
        return weightThresholdValues[0] + totalExtraWeight;
        }

        // If permission-mode is enabled, calculate the weight threshold based on the player's permissions
        float weight = weightThresholdValues[0];
        for (int i = 10000; i > 0; i -= 100) {
            if (p.hasPermission("weight.level1." + i)) {
                weight = i;
                return weight + totalExtraWeight;
            }
        }

        // If no specific permission found, return the default weight threshold for level 1
        return weight + totalExtraWeight;
    }

    // Calculates and returns the weight threshold for level 2 based on the player's permissions.
    // If permission-mode is disabled, the weight threshold value from the 'config.yml' file will be used.
    public float calculateWeightLevel2(Player p) {

        PlayerInventory inv = p.getInventory();

        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        ItemStack secondhand = inv.getItemInOffHand();

        float totalExtraWeight = 0.0f;

        for (ItemStack item : items) {
            if (item != null && boostItemsWeight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName()))
                totalExtraWeight += boostItemsWeight.get(item.getItemMeta().getDisplayName());
        }

        for (ItemStack itemStack : armor) {
            if (itemStack != null && boostItemsWeight.containsKey(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName()))
                totalExtraWeight += boostItemsWeight.get(itemStack.getItemMeta().getDisplayName());
        }

        if(secondhand.getItemMeta() != null && boostItemsWeight.containsKey(Objects.requireNonNull(secondhand.getItemMeta()).getDisplayName()))
            totalExtraWeight += boostItemsWeight.get(secondhand.getItemMeta().getDisplayName());

        if (!getPlugin().getConfig().getBoolean("permission-mode")) {
            // If permission-mode is disabled, return the weight threshold value from the 'config.yml' file
            return weightThresholdValues[1] + totalExtraWeight;
        }

        // If permission-mode is enabled, calculate the weight threshold based on the player's permissions
        float weight = weightThresholdValues[1];
        for (int i = 10000; i > 0; i -= 100) {
            if (p.hasPermission("weight.level2." + i)) {
                weight = i;
                return weight + totalExtraWeight;
            }
        }

        // If no specific permission found, return the default weight threshold for level 2
        return weight + totalExtraWeight;
    }

    // Calculates and returns the weight threshold for level 3 based on the player's permissions.
    // If permission-mode is disabled, the weight threshold value from the 'config.yml' file will be used.
    public float calculateWeightLevel3(Player p) {

        PlayerInventory inv = p.getInventory();

        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        ItemStack secondhand = inv.getItemInOffHand();

        float totalExtraWeight = 0.0f;

        for (ItemStack item : items) {
            if (item != null && boostItemsWeight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName()))
                totalExtraWeight += boostItemsWeight.get(item.getItemMeta().getDisplayName());
        }

        for (ItemStack itemStack : armor) {
            if (itemStack != null && boostItemsWeight.containsKey(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName()))
                totalExtraWeight += boostItemsWeight.get(itemStack.getItemMeta().getDisplayName());
        }

        if(secondhand.getItemMeta() != null && boostItemsWeight.containsKey(Objects.requireNonNull(secondhand.getItemMeta()).getDisplayName()))
            totalExtraWeight += boostItemsWeight.get(secondhand.getItemMeta().getDisplayName());

        if (!getPlugin().getConfig().getBoolean("permission-mode")) {
            // If permission-mode is disabled, return the weight threshold value from the 'config.yml' file
            return weightThresholdValues[2] + totalExtraWeight;
        }

        // If permission-mode is enabled, calculate the weight threshold based on the player's permissions
        float weight = weightThresholdValues[2];
        for (int i = 20000; i > 0; i -= 100) {
            if (p.hasPermission("weight.level3." + i)) {
                weight = i;
                return weight + totalExtraWeight;
            }
        }

        // If no specific permission found, return the default weight threshold for level 3
        return weight + totalExtraWeight;
    }


    public int getEnabledWeightLevel() {
        if (Weight3) {
            return 3;
        } else if (Weight2) {
            return 2;
        } else {
            return 1;
        }
    }

}
