package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegion;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import static ted_2001.WeightRPG.Utils.JsonFile.boostItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class CalculateWeight {

    public static final HashMap<UUID, Float> playerWeight = new HashMap<>();
    public static final HashMap<UUID, Long> cooldown = new HashMap<>();
    public static final HashMap<UUID, Float> playerBoostWeight = new HashMap<>();

    public static final float[] weightThresholdValues = new float[]{0f, 0f, 0f};

    private static final String WHITE_COLOR = "&f&l";
    private static final String DARK_RED_COLOR = "&4&l";
    private static final String[] COLOR_CODES = {"&a&l", "&2&l", "&e&l", "&6&l", "&c&l"};

    private static NamespacedKey cachedWeightKey;
    private static NamespacedKey cachedBoostKey;

    private static NamespacedKey weightKey() {
        if (cachedWeightKey == null) {
            cachedWeightKey = new NamespacedKey(getPlugin(), "weight");
        }
        return cachedWeightKey;
    }

    private static NamespacedKey boostKey() {
        if (cachedBoostKey == null) {
            cachedBoostKey = new NamespacedKey(getPlugin(), "boost");
        }
        return cachedBoostKey;
    }

    public CalculateWeight() {
        refreshThresholdValues();
    }

    public static void refreshThresholdValues() {
        weightThresholdValues[0] = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
        weightThresholdValues[1] = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
        weightThresholdValues[2] = (float) getPlugin().getConfig().getDouble("weight-level-3.value");
    }

    public void calculateWeight(Player player) {
        if (!isEnabled(player)) {
            return;
        }

        if (getPlugin().getServer().getPluginManager().isPluginEnabled("WorldGuard")
                && new WorldGuardRegion().isInRegion(player)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (player.hasPermission("weight.bypass")) {
            playerWeight.put(playerId, 0f);
            playerBoostWeight.put(playerId, 0f);
            if (player.getWalkSpeed() < 0.2f) {
                player.setWalkSpeed(0.2f);
            }
            return;
        }

        playerBoostWeight.put(playerId, 0f);
        float totalWeight = calculateInventoryWeight(player.getInventory(), player);
        playerWeight.put(playerId, totalWeight);
        applyWeightEffects(player);
    }

    private float calculateInventoryWeight(PlayerInventory inventory, Player player) {
        boolean shulkerBoxesEnabled = getPlugin().getConfig().getBoolean("shulker-boxes");
        float weight = 0f;

        for (ItemStack item : inventory.getStorageContents()) {
            weight += calculateItemWeight(item, shulkerBoxesEnabled, player);
        }
        for (ItemStack item : inventory.getExtraContents()) {
            weight += calculateItemWeight(item, shulkerBoxesEnabled, player);
        }
        for (ItemStack item : inventory.getArmorContents()) {
            weight += calculateItemWeight(item, shulkerBoxesEnabled, player);
        }
        // Preserve the plugin's established slot calculation behavior.
        weight += calculateItemWeight(inventory.getItemInOffHand(), shulkerBoxesEnabled, player);
        return weight;
    }

    private float calculateItemWeight(ItemStack item, boolean shulkerBoxesEnabled, Player player) {
        if (item == null || item.getType().isAir()) {
            return 0f;
        }

        if (shulkerBoxesEnabled && item.getItemMeta() instanceof BlockStateMeta blockStateMeta
                && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
            return shulkerBoxWeightCalculations(shulkerBox, player);
        }
        return calculateItemWeight(item, player);
    }

    private static float calculateItemWeight(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return 0f;
        }

        float itemWeight = globalItemsWeight.getOrDefault(itemStack.getType(), 0f);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            Float persistentWeight = pdc.get(weightKey(), PersistentDataType.FLOAT);
            Float persistentBoost = pdc.get(boostKey(), PersistentDataType.FLOAT);

            if (persistentWeight != null) {
                itemWeight = persistentWeight;
            } else if (persistentBoost != null) {
                addBoost(player, persistentBoost * itemStack.getAmount());
                ItemLoreUtils.updateBoostItemLore(itemStack, persistentBoost);
                return 0f;
            } else {
                String displayName = itemMeta.getDisplayName();
                Float customWeight = customItemsWeight.get(displayName);
                Float boostWeight = boostItemsWeight.get(displayName);

                if (customWeight != null) {
                    itemWeight = customWeight;
                } else if (boostWeight != null) {
                    addBoost(player, boostWeight * itemStack.getAmount());
                    ItemLoreUtils.updateBoostItemLore(itemStack, boostWeight);
                    return 0f;
                } else {
                    ItemLoreUtils.updateBoostItemLore(itemStack, 0f);
                }
            }
        }

        ItemLoreUtils.updateItemLore(itemStack, itemWeight);
        return itemWeight * itemStack.getAmount();
    }

    private static void addBoost(Player player, float boost) {
        playerBoostWeight.merge(player.getUniqueId(), boost, Float::sum);
    }

    public void applyWeightEffects(Player player) {
        UUID playerId = player.getUniqueId();
        Float currentWeight = playerWeight.get(playerId);
        if (currentWeight == null) {
            calculateWeight(player);
            return;
        }

        float level1 = calculateWeightThreshold(player, 1);
        float level2 = calculateWeightThreshold(player, 2);
        float level3 = calculateWeightThreshold(player, 3);

        float speed1 = (float) getPlugin().getConfig().getDouble("weight-level-1.speed");
        float speed2 = (float) getPlugin().getConfig().getDouble("weight-level-2.speed");
        float speed3 = (float) getPlugin().getConfig().getDouble("weight-level-3.speed");

        boolean level2Enabled = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
        boolean level3Enabled = getPlugin().getConfig().getBoolean("weight-level-3.enabled");

        if (currentWeight < level1) {
            setWalkSpeedIfNeeded(player, 0.2f);
            if (getPlugin().getConfig().getBoolean("message-before-level1-enabled")) {
                sendMessage(getPlugin().getConfig().getString("message-before-level1"), player, null);
            }
            return;
        }

        // Highest active level wins. Level 2 and Level 3 can be disabled independently.
        if (level3Enabled && currentWeight >= level3) {
            setWalkSpeedIfNeeded(player, speed3);
            if (getPlugin().getConfig().getBoolean("weight-level-3.message-enabled")) {
                sendMessage(getPlugin().getConfig().getString("weight-level-3.message"), player,
                        configuredSound("weight-level-3.sound"));
            }
            return;
        }

        if (level2Enabled && currentWeight >= level2) {
            setWalkSpeedIfNeeded(player, speed2);
            if (getPlugin().getConfig().getBoolean("weight-level-2.message-enabled")) {
                sendMessage(getPlugin().getConfig().getString("weight-level-2.message"), player,
                        configuredSound("weight-level-2.sound"));
            }
            return;
        }

        setWalkSpeedIfNeeded(player, speed1);
        if (getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
            sendMessage(getPlugin().getConfig().getString("weight-level-1.message"), player,
                    configuredSound("weight-level-1.sound"));
        }
    }

    private void setWalkSpeedIfNeeded(Player player, float speed) {
        float clamped = Math.max(0f, Math.min(1f, speed));
        if (Float.compare(player.getWalkSpeed(), clamped) != 0) {
            player.setWalkSpeed(clamped);
        }
    }

    private Sound configuredSound(String path) {
        String configured = getPlugin().getConfig().getString(path, "none");
        if (configured == null || configured.equalsIgnoreCase("none")) {
            return null;
        }
        try {
            return Sound.valueOf(configured.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            getPlugin().getLogger().warning("Invalid sound in " + path + ": " + configured);
            return null;
        }
    }

    private void sendMessage(String message, Player player, Sound sound) {
        if (message == null || message.isEmpty()) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldownMillis = Math.max(0L,
                (long) (getPlugin().getConfig().getDouble("messages-cooldown") * 1000L));
        Long lastMessage = cooldown.get(playerId);

        if (lastMessage == null || now - lastMessage >= cooldownMillis) {
            cooldownMessenger(player, sound, message, now);
        }
    }

    public static boolean isEnabled(Player player) {
        for (String disabledWorld : getPlugin().getConfig().getStringList("disabled-worlds")) {
            if (disabledWorld.equalsIgnoreCase(player.getWorld().getName())) {
                if (Float.compare(player.getWalkSpeed(), 0.2f) != 0) {
                    player.setWalkSpeed(0.2f);
                }
                return false;
            }
        }

        GameMode gameMode = player.getGameMode();
        if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) {
            if (Float.compare(player.getWalkSpeed(), 0.2f) != 0) {
                player.setWalkSpeed(0.2f);
            }
            return false;
        }
        return true;
    }

    private void cooldownMessenger(Player player, Sound sound, String message, long now) {
        if (player.hasPermission("weight.bypass")) {
            return;
        }

        cooldown.put(player.getUniqueId(), now);
        String formatted = ColorUtils.translateColorCodes(formatMessage(message, player));
        if (getPlugin().getConfig().getBoolean("actionbar-messages")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formatted));
        } else {
            player.sendMessage(formatted);
        }

        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1f, 1f);
        }
    }

    public String formatMessage(String message, Player player) {
        if (message == null) {
            return "";
        }

        return message
                .replace("%playername%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%weight%", String.format("%.2f", playerWeight.getOrDefault(player.getUniqueId(), 0f)))
                .replace("%world%", player.getWorld().getName())
                .replace("%level1%", String.valueOf(calculateWeightThreshold(player, 1)))
                .replace("%level2%", String.valueOf(calculateWeightThreshold(player, 2)))
                .replace("%level3%", String.valueOf(calculateWeightThreshold(player, 3)))
                .replace("%percentageweight%", generateProgressBar(player))
                .replace("%percentage%", String.format("%.2f", getPercentage(player)))
                .replace("%pluginprefix%", getPlugin().getPluginPrefix())
                .replace("%maxweight%", String.valueOf(calculateWeightThreshold(player, getEnabledWeightLevel())));
    }

    public float getPercentage(Player player) {
        float weight = playerWeight.getOrDefault(player.getUniqueId(), 0f);
        float maxWeight = calculateWeightThreshold(player, getEnabledWeightLevel());
        if (maxWeight <= 0f) {
            return 0f;
        }
        return weight * 100f / maxWeight;
    }

    public String generateProgressBar(Player player) {
        if (player == null) {
            return "";
        }

        float percentage = Math.max(0f, getPercentage(player));
        StringBuilder message = new StringBuilder("&7&l[||||||||||||||||||||&7&l]");
        int coloredBars = percentage >= 100f ? 20 : Math.min(20, ((int) percentage / 5) + 1);
        int colorIndex = percentage >= 100f ? 4 : Math.min(4, (int) percentage / 20);

        message.insert(5, percentage >= 95f ? DARK_RED_COLOR : COLOR_CODES[colorIndex]);
        message.insert(9 + coloredBars, WHITE_COLOR);
        return ChatColor.translateAlternateColorCodes('&', message.toString());
    }

    public int getEnabledWeightLevel() {
        if (getPlugin().getConfig().getBoolean("weight-level-3.enabled")) {
            return 3;
        }
        if (getPlugin().getConfig().getBoolean("weight-level-2.enabled")) {
            return 2;
        }
        return 1;
    }

    public float calculateWeightThreshold(Player player, int level) {
        if (level < 1 || level > 3) {
            throw new IllegalArgumentException("Weight level must be between 1 and 3");
        }

        float boostWeight = playerBoostWeight.getOrDefault(player.getUniqueId(), 0f);
        float configuredWeight = weightThresholdValues[level - 1];
        if (!getPlugin().getConfig().getBoolean("permission-mode")) {
            return configuredWeight + boostWeight;
        }

        // Preserve the documented permission contract and Bukkit's normal wildcard resolution.
        // Level 1/2: 100..10000, Level 3: 100..20000, multiples of 100 only.
        int maxPermissionWeight = level == 3 ? 20000 : 10000;
        for (int value = maxPermissionWeight; value >= 100; value -= 100) {
            if (player.hasPermission("weight.level" + level + "." + value)) {
                return value + boostWeight;
            }
        }

        return configuredWeight + boostWeight;
    }

    public static float shulkerBoxWeightCalculations(ShulkerBox shulkerBox, Player player) {
        float weight = globalItemsWeight.getOrDefault(shulkerBox.getType(), 0f);
        for (ItemStack item : shulkerBox.getInventory().getStorageContents()) {
            if (item != null && !item.getType().isAir()) {
                weight += calculateItemWeight(item, player);
            }
        }
        return weight;
    }
}
