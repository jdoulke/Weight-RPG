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

    public static HashMap<UUID, Float> playerBoostWeight = new HashMap<>();

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
                totalWeight += itemWeightCalculations(item, p);
            }
        }

        for (ItemStack itemStack : inventory.getArmorContents()) {
            if (itemStack != null) {
                totalWeight += itemWeightCalculations(itemStack, p);
            }
        }

        ItemStack offHandItem = inventory.getItemInOffHand();
        if (offHandItem != null) {
            totalWeight += itemWeightCalculations(offHandItem, p);
        }
        // Update player's weight in the HashMap
        playerWeight.put(p.getUniqueId(), totalWeight);
        applyWeightEffects(p); // Apply effects based on player's weight
    }


    private float  itemWeightCalculations(ItemStack itemStack, Player p) {

        float itemWeight = 0.0f;
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            String displayName = itemMeta.getDisplayName();
            float boostWeight = 0;
            // Check if the item has a custom weight based on its display name from config file
            if (customItemsWeight.containsKey(displayName))
                itemWeight = customItemsWeight.get(displayName);

                // Check if the item is a boost item weight based on its display name from config file
            else if (boostItemsWeight.containsKey(displayName)) {
                // Boost items don't add weight to the player.
                boostWeight += boostItemsWeight.get(displayName);
                boostWeight *= itemStack.getAmount();
                playerBoostWeight.put(p.getUniqueId(), boostWeight);
                return 0.0f;
            }
                // Check if the item has a global weight based on its material type
            else if (globalItemsWeight.containsKey(itemStack.getType()))
                itemWeight = globalItemsWeight.get(itemStack.getType());
        }

        return itemWeight * itemStack.getAmount();
    }

    public void applyWeightEffects(Player p) {
        UUID id = p.getUniqueId();

        // If the player's weight is not calculated yet, return
        if (playerWeight.get(id) == null)
            return;

        float weight = playerWeight.get(id);

        // Calculate weight thresholds for different levels
        double weight1 = calculateWeightThreshold(p, 1);
        double weight2 = calculateWeightThreshold(p, 2);
        double weight3 = calculateWeightThreshold(p, 3);

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
        message = message.replaceAll("%weight%", String.format("%.2f", playerWeight.getOrDefault(p.getUniqueId(), 0f)));
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
        message = message.replaceAll("%maxweight%", String.valueOf(calculateWeightThreshold(p, getEnabledWeightLevel())));

        return message;
    }

    // Calculates and returns the percentage of the player's weight relative to the maximum weight threshold,
    // based on the enabled weight levels (level 1, level 2, or level 3).
    public float getPercentage(Player p){

        if(playerWeight.get(p.getUniqueId()) == null)
            return 0;

        float boostWeight = playerBoostWeight.getOrDefault(p.getUniqueId(), 0f);

        float weight = playerWeight.get(p.getUniqueId());
        float maxWeight = calculateWeightThreshold(p, getEnabledWeightLevel());

        return weight * 100 / (maxWeight + boostWeight);
    }

    // Generates a progress bar based on the player's weight relative to the maximum weight threshold
    // and returns the formatted progress bar as a string.
    public String generateProgressBar(Player p) {

        if (p == null)
            return "";


        // Calculate the percentage of the player's weight
        float percentage = getPercentage(p);

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


    public int getEnabledWeightLevel() {
        if (Weight3) {
            return 3;
        } else if (Weight2) {
            return 2;
        } else {
            return 1;
        }
    }

    public float calculateWeightThreshold(Player player, int level) {

        float weight = weightThresholdValues[level - 1];
        float boostWeight = playerBoostWeight.getOrDefault(player.getUniqueId(), 0f);

        if (!getPlugin().getConfig().getBoolean("permission-mode")) {
            // If permission-mode is disabled, return the weight threshold value from the 'config.yml' file
            return weightThresholdValues[level - 1] + boostWeight;
        }
        for (int i = 10000; i > 0; i -= 100) {
            if (player.hasPermission("weight.level" + level + "." + i)) {
                weight = i;
                break;
            }
        }
        return weight + boostWeight;
    }

}
