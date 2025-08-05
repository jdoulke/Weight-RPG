package ted_2001.WeightRPG.Utils;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Utility class for handling item lore related to Weight-RPG.
 */
public final class ItemLoreUtils {

    private ItemLoreUtils() {
        // Utility class
    }

    /**
     * Updates the lore of the provided item with its weight value. If the lore is disabled in
     * the configuration or the weight is zero, any previously added lore line by the plugin
     * will be removed.
     *
     * @param item   the {@link ItemStack} to update
     * @param weight the weight of a single item
     */
    /**
     * Updates the lore of the provided item with its weight value. If the lore is disabled in
     * the configuration or the weight is zero, any previously added lore line by the plugin
     * will be removed.
     *
     * @param item   the {@link ItemStack} to update
     * @param weight the weight of a single item
     */
    public static void updateItemLore(ItemStack item, float weight) {
        if (item == null)
            return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        NamespacedKey loreKey = new NamespacedKey(getPlugin(), "weightLore");
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        String previousLine = pdc.get(loreKey, PersistentDataType.STRING);
        if (previousLine != null) {
            lore.removeIf(line -> line.equals(previousLine));
            pdc.remove(loreKey);
        }

        if (getPlugin().getConfig().getBoolean("item-weight-lore.enabled") && weight > 0f) {
            String template = Objects.requireNonNull(getPlugin().getConfig().getString(
                    "item-weight-lore.message", "&7%item% Weight: &e%weight%"));

            String itemName = meta.hasDisplayName() ? meta.getDisplayName() : formatMaterialName(item.getType().name());
            String line = template
                    .replace("%weight%", String.format("%.2f", weight))
                    .replace("%item%", itemName);

            line = ChatColor.translateAlternateColorCodes('&', line);
            lore.add(line);
            pdc.set(loreKey, PersistentDataType.STRING, line);
        }

        meta.setLore(lore.isEmpty() ? null : lore);
        item.setItemMeta(meta);
    }

    /**
     * Updates the lore of the provided boost item with the weight it grants to the player. If lore
     * messages are disabled or the boost value is zero, any previously added lore line by the plugin
     * will be removed.
     *
     * @param item       the {@link ItemStack} to update
     * @param boostWeight the weight bonus of a single item
     */
    public static void updateBoostItemLore(ItemStack item, float boostWeight) {
        if (item == null)
            return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;

        NamespacedKey loreKey = new NamespacedKey(getPlugin(), "weightLore");
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

        String previousLine = pdc.get(loreKey, PersistentDataType.STRING);
        if (previousLine != null) {
            lore.removeIf(line -> line.equals(previousLine));
            pdc.remove(loreKey);
        }

        if (getPlugin().getConfig().getBoolean("item-weight-lore.enabled") && boostWeight > 0f) {
            String template = Objects.requireNonNull(getPlugin().getConfig().getString(
                    "item-weight-lore.boost-message", "&7%item% Weight Boost: &e%boost%"));

            String itemName = meta.hasDisplayName() ? meta.getDisplayName() : formatMaterialName(item.getType().name());
            String line = template
                    .replace("%boost%", String.format("%.2f", boostWeight))
                    .replace("%item%", itemName);

            line = ChatColor.translateAlternateColorCodes('&', line);
            lore.add(line);
            pdc.set(loreKey, PersistentDataType.STRING, line);
        }

        meta.setLore(lore.isEmpty() ? null : lore);
        item.setItemMeta(meta);
    }

    private static String formatMaterialName(String material) {
        String[] parts = material.toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            builder.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(' ');
        }
        return builder.toString().trim();
    }
}
