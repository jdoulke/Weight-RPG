package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    private static final Map<ModelDataKey, Float> customModelDataWeights = new HashMap<>();
    private static final Map<ModelDataKey, Float> customModelDataBoosts = new HashMap<>();

    private static NamespacedKey cachedWeightKey;
    private static NamespacedKey cachedBoostKey;

    public CalculateWeight() {
        refreshThresholdValues();
    }

    public static void refreshThresholdValues() {
        weightThresholdValues[0] = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
        weightThresholdValues[1] = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
        weightThresholdValues[2] = (float) getPlugin().getConfig().getDouble("weight-level-3.value");
        refreshCustomModelDataMappings();
    }

    private static void refreshCustomModelDataMappings() {
        customModelDataWeights.clear();
        customModelDataBoosts.clear();
        loadCustomModelDataMappings("custom-model-data-weight", customModelDataWeights);
        loadCustomModelDataMappings("custom-model-data-boost", customModelDataBoosts);
    }

    private static void loadCustomModelDataMappings(String path, Map<ModelDataKey, Float> target) {
        for (String entry : getPlugin().getConfig().getStringList(path)) {
            if (entry == null || entry.isBlank()) {
                continue;
            }

            int equals = entry.lastIndexOf('=');
            int separator = entry.lastIndexOf(';', equals - 1);
            if (equals <= 0 || separator <= 0 || separator >= equals - 1 || equals == entry.length() - 1) {
                getPlugin().getLogger().warning("Invalid " + path + " entry '" + entry
                        + "'. Expected MATERIAL;MODEL_DATA=value");
                continue;
            }

            String materialName = entry.substring(0, separator).trim().toUpperCase(Locale.ROOT);
            String modelValue = entry.substring(separator + 1, equals).trim();
            String configuredValue = entry.substring(equals + 1).trim();
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                getPlugin().getLogger().warning("Invalid material in " + path + ": " + materialName);
                continue;
            }

            try {
                float modelData = Float.parseFloat(modelValue);
                float value = Float.parseFloat(configuredValue);
                if (!Float.isFinite(modelData) || !Float.isFinite(value)) {
                    throw new NumberFormatException("non-finite value");
                }
                target.put(new ModelDataKey(material, modelData), value);
            } catch (NumberFormatException exception) {
                getPlugin().getLogger().warning("Invalid numeric value in " + path + " entry '" + entry + "'.");
            }
        }
    }

    public static ModelDataMatch resolveCustomModelData(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return ModelDataMatch.NONE;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelDataComponent()) {
            return ModelDataMatch.NONE;
        }

        List<Float> floats = meta.getCustomModelDataComponent().getFloats();
        if (floats.isEmpty()) {
            return ModelDataMatch.NONE;
        }

        ModelDataKey key = new ModelDataKey(item.getType(), floats.getFirst());
        Float weight = customModelDataWeights.get(key);
        if (weight != null) {
            return new ModelDataMatch(true, weight, 0f);
        }

        Float boost = customModelDataBoosts.get(key);
        if (boost != null) {
            return new ModelDataMatch(true, 0f, boost);
        }
        return ModelDataMatch.NONE;
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
            setWalkSpeedIfNeeded(player, 0.2f);
            return;
        }

        CalculationResult result = calculateInventoryWeight(player.getInventory(), player);
        playerWeight.put(playerId, result.weight());
        playerBoostWeight.put(playerId, result.boost());
        applyWeightEffects(player);
    }

    private CalculationResult calculateInventoryWeight(PlayerInventory inventory, Player player) {
        boolean shulkerBoxesEnabled = getPlugin().getConfig().getBoolean("shulker-boxes");
        MutableCalculation total = new MutableCalculation();

        accumulateInventorySection(inventory.getStorageContents(), shulkerBoxesEnabled, player, total);
        accumulateInventorySection(inventory.getExtraContents(), shulkerBoxesEnabled, player, total);
        accumulateInventorySection(inventory.getArmorContents(), shulkerBoxesEnabled, player, total);
        accumulateItem(inventory.getItemInOffHand(), shulkerBoxesEnabled, player, total);
        return new CalculationResult(total.weight, total.boost);
    }

    private void accumulateInventorySection(ItemStack[] contents, boolean shulkerBoxesEnabled,
                                            Player player, MutableCalculation total) {
        for (ItemStack item : contents) {
            accumulateItem(item, shulkerBoxesEnabled, player, total);
        }
    }

    private void accumulateItem(ItemStack item, boolean shulkerBoxesEnabled,
                                Player player, MutableCalculation total) {
        if (item == null || item.getType().isAir()) {
            return;
        }

        if (shulkerBoxesEnabled && item.getItemMeta() instanceof BlockStateMeta blockStateMeta
                && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
            accumulateShulkerBox(shulkerBox, player, total);
            return;
        }

        ResolvedItem resolved = resolveItem(item);
        if (resolved.boostPerItem() != 0f) {
            total.boost += resolved.boostPerItem() * item.getAmount();
            ItemLoreUtils.updateBoostItemLore(item, resolved.boostPerItem());
            return;
        }

        ItemLoreUtils.updateItemLore(item, resolved.weightPerItem());
        total.weight += resolved.weightPerItem() * item.getAmount();
    }

    private static ResolvedItem resolveItem(ItemStack item) {
        float itemWeight = globalItemsWeight.getOrDefault(item.getType(), 0f);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return new ResolvedItem(itemWeight, 0f);
        }

        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        Float persistentWeight = pdc.get(weightKey(), PersistentDataType.FLOAT);
        if (persistentWeight != null) {
            return new ResolvedItem(persistentWeight, 0f);
        }

        Float persistentBoost = pdc.get(boostKey(), PersistentDataType.FLOAT);
        if (persistentBoost != null) {
            return new ResolvedItem(0f, persistentBoost);
        }

        ModelDataMatch modelData = resolveCustomModelData(item);
        if (modelData.matched()) {
            return new ResolvedItem(modelData.weightPerItem(), modelData.boostPerItem());
        }

        String displayName = itemMeta.getDisplayName();
        Float customWeight = customItemsWeight.get(displayName);
        if (customWeight != null) {
            return new ResolvedItem(customWeight, 0f);
        }

        Float boostWeight = boostItemsWeight.get(displayName);
        if (boostWeight != null) {
            return new ResolvedItem(0f, boostWeight);
        }

        ItemLoreUtils.updateBoostItemLore(item, 0f);
        return new ResolvedItem(itemWeight, 0f);
    }

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

    public void applyWeightEffects(Player player) {
        UUID playerId = player.getUniqueId();
        Float currentWeight = playerWeight.get(playerId);
        if (currentWeight == null) {
            calculateWeight(player);
            return;
        }

        Thresholds thresholds = calculateThresholds(player);
        boolean level2Enabled = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
        boolean level3Enabled = getPlugin().getConfig().getBoolean("weight-level-3.enabled");

        if (currentWeight < thresholds.level1()) {
            setWalkSpeedIfNeeded(player, 0.2f);
            if (getPlugin().getConfig().getBoolean("message-before-level1-enabled")) {
                sendMessage(getPlugin().getConfig().getString("message-before-level1"), player, null);
            }
            return;
        }

        if (level3Enabled && currentWeight >= thresholds.level3()) {
            applyLevel(player, 3);
            return;
        }
        if (level2Enabled && currentWeight >= thresholds.level2()) {
            applyLevel(player, 2);
            return;
        }
        applyLevel(player, 1);
    }

    private Thresholds calculateThresholds(Player player) {
        if (!getPlugin().getConfig().getBoolean("permission-mode")) {
            float boost = playerBoostWeight.getOrDefault(player.getUniqueId(), 0f);
            return new Thresholds(
                    weightThresholdValues[0] + boost,
                    weightThresholdValues[1] + boost,
                    weightThresholdValues[2] + boost
            );
        }
        return new Thresholds(
                calculateWeightThreshold(player, 1),
                calculateWeightThreshold(player, 2),
                calculateWeightThreshold(player, 3)
        );
    }

    private void applyLevel(Player player, int level) {
        String path = "weight-level-" + level;
        float speed = (float) getPlugin().getConfig().getDouble(path + ".speed");
        setWalkSpeedIfNeeded(player, speed);
        if (getPlugin().getConfig().getBoolean(path + ".message-enabled")) {
            sendMessage(getPlugin().getConfig().getString(path + ".message"), player,
                    configuredSound(path + ".sound"));
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

        UUID playerId = player.getUniqueId();
        Thresholds thresholds = calculateThresholds(player);
        float maxWeight = switch (getEnabledWeightLevel()) {
            case 3 -> thresholds.level3();
            case 2 -> thresholds.level2();
            default -> thresholds.level1();
        };
        float currentWeight = playerWeight.getOrDefault(playerId, 0f);
        float percentage = maxWeight <= 0f ? 0f : currentWeight * 100f / maxWeight;

        return message
                .replace("%playername%", player.getName())
                .replace("%displayname%", player.getDisplayName())
                .replace("%weight%", String.format("%.2f", currentWeight))
                .replace("%world%", player.getWorld().getName())
                .replace("%level1%", String.valueOf(thresholds.level1()))
                .replace("%level2%", String.valueOf(thresholds.level2()))
                .replace("%level3%", String.valueOf(thresholds.level3()))
                .replace("%percentageweight%", generateProgressBar(percentage))
                .replace("%percentage%", String.format("%.2f", percentage))
                .replace("%pluginprefix%", getPlugin().getPluginPrefix())
                .replace("%maxweight%", String.valueOf(maxWeight));
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
        return generateProgressBar(Math.max(0f, getPercentage(player)));
    }

    private String generateProgressBar(float rawPercentage) {
        float percentage = Math.max(0f, rawPercentage);
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

        int maxPermissionWeight = level == 3 ? 20000 : 10000;
        for (int value = maxPermissionWeight; value >= 100; value -= 100) {
            if (player.hasPermission("weight.level" + level + "." + value)) {
                return value + boostWeight;
            }
        }
        return configuredWeight + boostWeight;
    }

    public static float shulkerBoxWeightCalculations(ShulkerBox shulkerBox, Player player) {
        MutableCalculation calculation = new MutableCalculation();
        calculation.weight = globalItemsWeight.getOrDefault(shulkerBox.getType(), 0f);
        for (ItemStack item : shulkerBox.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            ResolvedItem resolved = resolveItem(item);
            if (resolved.boostPerItem() != 0f) {
                float boost = resolved.boostPerItem() * item.getAmount();
                calculation.boost += boost;
                ItemLoreUtils.updateBoostItemLore(item, resolved.boostPerItem());
            } else {
                ItemLoreUtils.updateItemLore(item, resolved.weightPerItem());
                calculation.weight += resolved.weightPerItem() * item.getAmount();
            }
        }

        if (calculation.boost != 0f) {
            playerBoostWeight.merge(player.getUniqueId(), calculation.boost, Float::sum);
        }
        return calculation.weight;
    }

    private static void accumulateShulkerBox(ShulkerBox shulkerBox, Player player, MutableCalculation total) {
        total.weight += globalItemsWeight.getOrDefault(shulkerBox.getType(), 0f);
        for (ItemStack item : shulkerBox.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            ResolvedItem resolved = resolveItem(item);
            if (resolved.boostPerItem() != 0f) {
                total.boost += resolved.boostPerItem() * item.getAmount();
                ItemLoreUtils.updateBoostItemLore(item, resolved.boostPerItem());
            } else {
                ItemLoreUtils.updateItemLore(item, resolved.weightPerItem());
                total.weight += resolved.weightPerItem() * item.getAmount();
            }
        }
    }

    public record ModelDataMatch(boolean matched, float weightPerItem, float boostPerItem) {
        private static final ModelDataMatch NONE = new ModelDataMatch(false, 0f, 0f);
    }

    private record ModelDataKey(Material material, float modelData) { }
    private record ResolvedItem(float weightPerItem, float boostPerItem) { }
    private record CalculationResult(float weight, float boost) { }
    private record Thresholds(float level1, float level2, float level3) { }

    private static final class MutableCalculation {
        private float weight;
        private float boost;
    }
}
