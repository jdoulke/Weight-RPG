package ted_2001.WeightRPG.Listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.ColorUtils;
import ted_2001.WeightRPG.Utils.ItemLoreUtils;
import ted_2001.WeightRPG.Utils.Messages;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegion;

import java.util.HashMap;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.Utils.CalculateWeight.cooldown;
import static ted_2001.WeightRPG.Utils.CalculateWeight.isEnabled;
import static ted_2001.WeightRPG.Utils.CalculateWeight.playerBoostWeight;
import static ted_2001.WeightRPG.Utils.CalculateWeight.playerWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.boostItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class WeightCalculateListeners implements Listener {

    private final CalculateWeight weightCalculation = new CalculateWeight();
    private final HashMap<UUID, Long> jumpMessage = new HashMap<>();
    private final HashMap<UUID, Long> pickMessage = new HashMap<>();
    private final HashMap<UUID, Long> placeMessage = new HashMap<>();
    private final HashMap<UUID, Long> dropMessage = new HashMap<>();
    private final HashMap<UUID, Long> notifyMessage = new HashMap<>();
    private final HashMap<UUID, Long> dropCooldown = new HashMap<>();

    private final String pluginPrefix = org.bukkit.ChatColor.GRAY + "["
            + org.bukkit.ChatColor.YELLOW + "Weight-RPG" + org.bukkit.ChatColor.GRAY + "] ";

    private NamespacedKey weightKey;
    private NamespacedKey boostKey;

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!isWorldGuardEnabled(player)) {
            weightCalculation.calculateWeight(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!isWorldGuardEnabled(player)) {
            weightCalculation.calculateWeight(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!isWorldGuardEnabled(player)) {
            weightCalculation.calculateWeight(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        // calculateWeight performs enabled-world/gamemode and WorldGuard checks itself.
        weightCalculation.calculateWeight(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR) {
            event.getPlayer().setWalkSpeed(0.2f);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickUp(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!checkIfEnable(player) || isWorldGuardEnabled(player)) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        int amount = item.getAmount();
        ResolvedItemWeight resolved = resolveItemWeight(item, player, false);

        if (!resolved.known()) {
            notifyMissingWeight(item);
            return;
        }

        float currentWeight = playerWeight.getOrDefault(player.getUniqueId(), 0f);
        boolean level2Enabled = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
        boolean level3Enabled = getPlugin().getConfig().getBoolean("weight-level-3.enabled");
        boolean disableLevel1 = getPlugin().getConfig().getBoolean("weight-level-1.disable-pick-up", false);
        boolean disableLevel2 = level2Enabled && getPlugin().getConfig().getBoolean("weight-level-2.disable-pick-up", false);
        boolean disableLevel3 = level3Enabled && getPlugin().getConfig().getBoolean("weight-level-3.disable-pick-up", false);

        if ((disableLevel3 && currentWeight >= weightCalculation.calculateWeightThreshold(player, 3))
                || (disableLevel2 && currentWeight >= weightCalculation.calculateWeightThreshold(player, 2))
                || (disableLevel1 && currentWeight >= weightCalculation.calculateWeightThreshold(player, 1))) {
            event.setCancelled(true);
            return;
        }

        updateLore(item, resolved);
        event.getItem().setItemStack(item);
        message(player, "receive", item, resolved.weight(), amount);
        getServer().getScheduler().runTask(getPlugin(), () -> weightCalculation.calculateWeight(player));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!checkIfEnable(player) || isWorldGuardEnabled(player) || player.hasPermission("weight.bypass")) {
            return;
        }

        if (getPlugin().getConfig().getBoolean("drop-cooldown.enabled", false) && isDropOnCooldown(player)) {
            event.setCancelled(true);
            sendDropCooldownMessage(player);
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();
        ResolvedItemWeight resolved = resolveItemWeight(item, player, true);
        if (!resolved.known()) {
            notifyMissingWeight(item);
            return;
        }

        weightCalculation.calculateWeight(player);
        message(player, "lose", item, resolved.weight(), item.getAmount());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!checkIfEnable(player) || isWorldGuardEnabled(player)) {
            return;
        }

        // Preserve custom name/PDC metadata from the actual consumed item.
        ItemStack block = event.getItemInHand().clone();
        block.setAmount(1);
        if (block.getType() == Material.FIRE) {
            return;
        }

        ResolvedItemWeight resolved = resolveItemWeight(block, player, true);
        if (!resolved.known()) {
            notifyMissingWeight(block);
            return;
        }

        weightCalculation.calculateWeight(player);
        message(player, "place", block, resolved.weight(), 1);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJump(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null) {
            return;
        }

        Location from = event.getFrom();
        if (Double.compare(from.getX(), to.getX()) == 0
                && Double.compare(from.getY(), to.getY()) == 0
                && Double.compare(from.getZ(), to.getZ()) == 0) {
            return; // yaw/pitch-only movement
        }

        Player player = event.getPlayer();
        if (!checkIfEnable(player) || isWorldGuardEnabled(player) || player.hasPermission("weight.bypass")) {
            return;
        }

        if (Float.compare(player.getWalkSpeed(), 0f) == 0
                && (Double.compare(to.getX(), from.getX()) != 0 || Double.compare(to.getZ(), from.getZ()) != 0)) {
            event.setCancelled(true);
            return;
        }

        // Weight is maintained by inventory events + scheduler. Do not rescan the whole inventory per move packet.
        if (to.getY() <= from.getY()) {
            return;
        }

        Location location = player.getLocation();
        if (checkBlocks(location.clone()) || !isOnWholeBlockY(location.getY())) {
            return;
        }

        Material standingMaterial = location.getBlock().getType();
        if (standingMaterial == Material.LADDER || standingMaterial == Material.WATER || standingMaterial == Material.LAVA
                || player.isFlying()) {
            return;
        }

        Location twoBlocksBelow = location.clone().subtract(0, 2, 0);
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() == Material.ELYTRA
                && twoBlocksBelow.getBlock().getType() == Material.AIR) {
            return;
        }

        Float currentWeight = playerWeight.get(player.getUniqueId());
        if (currentWeight == null || !isJumpDisabled(player, currentWeight)) {
            return;
        }

        to.setY(from.getY());
        long now = System.currentTimeMillis();
        Long lastMessage = jumpMessage.get(player.getUniqueId());
        long cooldownMillis = Messages.getMessages().getLong("disable-jump-message-cooldown") * 1000L;
        if (lastMessage == null || now - lastMessage >= cooldownMillis) {
            jumpMessage(player);
        }
    }

    private boolean isJumpDisabled(Player player, float weight) {
        float level1 = weightCalculation.calculateWeightThreshold(player, 1);
        float level2 = weightCalculation.calculateWeightThreshold(player, 2);
        float level3 = weightCalculation.calculateWeightThreshold(player, 3);
        boolean level2Enabled = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
        boolean level3Enabled = getPlugin().getConfig().getBoolean("weight-level-3.enabled");

        if (level3Enabled && weight >= level3) {
            return getPlugin().getConfig().getBoolean("weight-level-3.disable-jump", false);
        }
        if (level2Enabled && weight >= level2) {
            return getPlugin().getConfig().getBoolean("weight-level-2.disable-jump", false);
        }
        if (weight >= level1) {
            return getPlugin().getConfig().getBoolean("weight-level-1.disable-jump", false);
        }
        return false;
    }

    private void jumpMessage(Player player) {
        jumpMessage.put(player.getUniqueId(), System.currentTimeMillis());
        String message = Messages.getMessages().getString("disable-jump-message", "");
        sendFormatted(player, message);
    }

    private boolean checkIfEnable(Player player) {
        return isEnabled(player);
    }

    private boolean isWorldGuardEnabled(Player player) {
        if (!getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            return false;
        }
        return new WorldGuardRegion().isInRegion(player);
    }

    private boolean checkBlocks(Location location) {
        Location test = location.clone();
        test.setX(test.getBlockX() + 0.5);
        if (isStepLike(test.getBlock().getType())) return true;

        test.setX(test.getBlockX() - 1);
        if (isStepLike(test.getBlock().getType())) return true;

        test.setX(test.getBlockX() + 0.5);
        test.setZ(test.getBlockZ() + 0.5);
        if (isStepLike(test.getBlock().getType())) return true;

        test.setZ(test.getBlockZ() - 1);
        return isStepLike(test.getBlock().getType());
    }

    private boolean isStepLike(Material material) {
        String name = material.name();
        return name.contains("STAIRS") || name.contains("SLAB");
    }

    private boolean isOnWholeBlockY(double y) {
        double fraction = Math.abs(y - Math.floor(y));
        return fraction < 0.1 || fraction > 0.9;
    }

    private ResolvedItemWeight resolveItemWeight(ItemStack item, Player player, boolean adjustBoost) {
        if (item == null || item.getType().isAir()) {
            return new ResolvedItemWeight(true, 0f, false, 0f);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            Float pdcWeight = pdc.get(weightKey(), PersistentDataType.FLOAT);
            if (pdcWeight != null) {
                return new ResolvedItemWeight(true, pdcWeight, false, 0f);
            }

            Float pdcBoost = pdc.get(boostKey(), PersistentDataType.FLOAT);
            if (pdcBoost != null) {
                if (adjustBoost) {
                    subtractBoost(player, pdcBoost * item.getAmount());
                }
                return new ResolvedItemWeight(true, 0f, true, pdcBoost);
            }

            String displayName = meta.getDisplayName();
            Float customWeight = customItemsWeight.get(displayName);
            if (customWeight != null) {
                return new ResolvedItemWeight(true, customWeight, false, 0f);
            }

            Float boostWeight = boostItemsWeight.get(displayName);
            if (boostWeight != null) {
                if (adjustBoost) {
                    subtractBoost(player, boostWeight * item.getAmount());
                }
                return new ResolvedItemWeight(true, 0f, true, boostWeight);
            }
        }

        Float globalWeight = globalItemsWeight.get(item.getType());
        return globalWeight == null
                ? new ResolvedItemWeight(false, 0f, false, 0f)
                : new ResolvedItemWeight(true, globalWeight, false, 0f);
    }

    private void subtractBoost(Player player, float amount) {
        UUID playerId = player.getUniqueId();
        float updated = Math.max(0f, playerBoostWeight.getOrDefault(playerId, 0f) - amount);
        playerBoostWeight.put(playerId, updated);
    }

    private void updateLore(ItemStack item, ResolvedItemWeight resolved) {
        if (resolved.boost()) {
            ItemLoreUtils.updateBoostItemLore(item, resolved.boostPerItem());
        } else {
            ItemLoreUtils.updateItemLore(item, resolved.weight());
        }
    }

    private NamespacedKey weightKey() {
        if (weightKey == null) {
            weightKey = new NamespacedKey(getPlugin(), "weight");
        }
        return weightKey;
    }

    private NamespacedKey boostKey() {
        if (boostKey == null) {
            boostKey = new NamespacedKey(getPlugin(), "boost");
        }
        return boostKey;
    }

    private boolean isDropOnCooldown(Player player) {
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        Long lastDrop = dropCooldown.get(playerId);
        long cooldownMillis = (long) (getPlugin().getConfig().getDouble("drop-cooldown.cooldown") * 1000L);
        if (lastDrop != null && now - lastDrop < cooldownMillis) {
            return true;
        }
        dropCooldown.put(playerId, now);
        return false;
    }

    private void sendDropCooldownMessage(Player player) {
        String message = Messages.getMessages().getString("drop-cooldown-message", "");
        sendFormatted(player, message);
    }

    private void message(Player player, String action, ItemStack item, float weight, int amount) {
        if (player.hasPermission("weight.bypass")) {
            return;
        }

        String path;
        String enabledPath;
        String cooldownPath;
        HashMap<UUID, Long> cooldownMap;
        if (action.equalsIgnoreCase("receive")) {
            path = "receive-item-message";
            enabledPath = "receive-item-message-enabled";
            cooldownPath = "receive-item-message-cooldown";
            cooldownMap = pickMessage;
        } else if (action.equalsIgnoreCase("lose")) {
            path = "lost-item-message";
            enabledPath = "lost-item-message-enabled";
            cooldownPath = "lost-item-message-cooldown";
            cooldownMap = dropMessage;
        } else if (action.equalsIgnoreCase("place")) {
            path = "place-block-message";
            enabledPath = "place-block-message-enabled";
            cooldownPath = "place-block-message-cooldown";
            cooldownMap = placeMessage;
        } else {
            return;
        }

        if (!Messages.getMessages().getBoolean(enabledPath)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldownMillis = (long) (Messages.getMessages().getDouble(cooldownPath) * 1000L);
        Long last = cooldownMap.get(playerId);
        if (last != null && now - last < cooldownMillis) {
            return;
        }

        cooldownMap.put(playerId, now);
        messageSender(Messages.getMessages().getString(path), player, item, weight, amount);
    }

    private void messageSender(String message, Player player, ItemStack item, float weight, int amount) {
        if (message == null) {
            return;
        }
        sendFormatted(player, getPlaceholders(message, item, weight, amount, player));
    }

    private void sendFormatted(Player player, String message) {
        String formatted = ColorUtils.translateColorCodes(weightCalculation.formatMessage(message, player));
        if (getPlugin().getConfig().getBoolean("actionbar-messages")) {
            BaseComponent[] actionBarMessage = TextComponent.fromLegacyText(formatted);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, actionBarMessage);
        } else {
            player.sendMessage(formatted);
        }
    }

    private String getPlaceholders(String message, ItemStack item, float weight, int amount, Player player) {
        ItemMeta meta = item.getItemMeta();
        String displayName = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();

        float effectiveWeight = weight;
        if (getPlugin().getConfig().getBoolean("shulker-boxes") && meta instanceof BlockStateMeta blockMeta
                && blockMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
            effectiveWeight = CalculateWeight.shulkerBoxWeightCalculations(shulkerBox, player);
        }

        return message
                .replace("%block%", item.getType().toString())
                .replace("%itemdisplayname%", displayName)
                .replace("%itemweight%", String.format("%.2f", effectiveWeight))
                .replace("%amount%", String.valueOf(amount))
                .replace("%totalweight%", String.format("%.2f", effectiveWeight * amount))
                .replace('_', ' ');
    }

    private void notifyMissingWeight(ItemStack item) {
        getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.AQUA + item.getType()
                + ChatColor.GRAY + " isn't in the weight files. You might want to add it manually.");
        notifyAdmins(item);
    }

    private void notifyAdmins(ItemStack item) {
        long now = System.currentTimeMillis();
        long cooldownMillis = getPlugin().getConfig().getLong("notify-permission-cooldown") * 1000L;
        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            if (!player.hasPermission("weight.notify")) {
                continue;
            }

            UUID playerId = player.getUniqueId();
            Long last = notifyMessage.get(playerId);
            if (last == null || now - last >= cooldownMillis) {
                player.sendMessage(pluginPrefix + ChatColor.AQUA + item.getType() + ChatColor.GRAY
                        + " isn't in the weight files. You might want to add it manually. You can use the /weight add command.");
                notifyMessage.put(playerId, now);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        playerWeight.remove(playerId);
        playerBoostWeight.remove(playerId);
        cooldown.remove(playerId);
        jumpMessage.remove(playerId);
        pickMessage.remove(playerId);
        placeMessage.remove(playerId);
        dropMessage.remove(playerId);
        notifyMessage.remove(playerId);
        dropCooldown.remove(playerId);
    }

    private record ResolvedItemWeight(boolean known, float weight, boolean boost, float boostPerItem) {
    }
}
