package ted_2001.WeightRPG.Utils;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Utility class for handling item lore related to Weight-RPG.
 */
public final class ItemLoreUtils {

    private static final Map<String, String> MATERIAL_NAME_CACHE = new HashMap<>();
    private static NamespacedKey weightLoreKey;
    private static NamespacedKey boostLoreKey;

    private ItemLoreUtils() {
    }

    public static void updateItemLore(ItemStack item, float weight) {
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        NamespacedKey loreKey = weightLoreKey();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> existingLore = meta.hasLore() ? meta.getLore() : null;
        String previousLine = pdc.get(loreKey, PersistentDataType.STRING);

        boolean enabled = getPlugin().getConfig().getBoolean("item-weight-lore.enabled");
        String itemName = meta.hasDisplayName() ? meta.getDisplayName() : formatMaterialName(item.getType().name());
        String template = Objects.requireNonNull(getPlugin().getConfig().getString(
                "item-weight-lore.message", "&7%item% Weight: &e%weight%"));
        String prefix = ChatColor.stripColor(template.replace("%item%", itemName).split("%weight%", 2)[0]);

        String line = null;
        String plainLine = null;
        if (enabled && weight > 0f) {
            line = ChatColor.translateAlternateColorCodes('&', template
                    .replace("%weight%", String.format("%.2f", weight))
                    .replace("%item%", itemName));
            plainLine = ChatColor.stripColor(line);
        }

        int index = findManagedLoreIndex(existingLore, previousLine, prefix);

        // Hot path: periodic inventory scans usually find lore already correct. Avoid cloning
        // lore, touching PDC and calling setItemMeta() when nothing actually changed.
        if (line != null && previousLine != null && previousLine.equals(plainLine)
                && index >= 0 && existingLore != null && existingLore.get(index).equals(line)) {
            return;
        }
        if (line == null && previousLine == null && index == -1) {
            return;
        }

        List<String> lore = existingLore == null ? new ArrayList<>() : new ArrayList<>(existingLore);
        if (previousLine != null) {
            pdc.remove(loreKey);
        }

        removeManagedLore(lore, index);

        if (line != null) {
            trimTrailingEmptyLines(lore);
            lore.add("");
            lore.add(line);
            pdc.set(loreKey, PersistentDataType.STRING, plainLine);
        }

        meta.setLore(lore.isEmpty() ? null : lore);
        item.setItemMeta(meta);
    }

    public static void updateBoostItemLore(ItemStack item, float boostWeight) {
        if (item == null) {
            return;
        }

        // Boost items should not display a normal weight line.
        updateItemLore(item, 0f);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        NamespacedKey loreKey = boostLoreKey();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> existingLore = meta.hasLore() ? meta.getLore() : null;
        String previousLine = pdc.get(loreKey, PersistentDataType.STRING);

        boolean enabled = getPlugin().getConfig().getBoolean("item-weight-lore.enabled");
        String itemName = meta.hasDisplayName() ? meta.getDisplayName() : formatMaterialName(item.getType().name());
        String template = Objects.requireNonNull(getPlugin().getConfig().getString(
                "item-weight-lore.boost-message", "&7%item% Weight Boost: &e%boost%"));
        String prefix = ChatColor.stripColor(template.replace("%item%", itemName).split("%boost%", 2)[0]);

        String line = null;
        String plainLine = null;
        if (enabled && boostWeight > 0f) {
            line = ChatColor.translateAlternateColorCodes('&', template
                    .replace("%boost%", String.format("%.2f", boostWeight))
                    .replace("%item%", itemName));
            plainLine = ChatColor.stripColor(line);
        }

        int index = findManagedLoreIndex(existingLore, previousLine, prefix);
        if (line != null && previousLine != null && previousLine.equals(plainLine)
                && index >= 0 && existingLore != null && existingLore.get(index).equals(line)) {
            return;
        }
        if (line == null && previousLine == null && index == -1) {
            return;
        }

        List<String> lore = existingLore == null ? new ArrayList<>() : new ArrayList<>(existingLore);
        if (previousLine != null) {
            pdc.remove(loreKey);
        }

        removeManagedLore(lore, index);

        if (line != null) {
            trimTrailingEmptyLines(lore);
            lore.add("");
            lore.add(line);
            pdc.set(loreKey, PersistentDataType.STRING, plainLine);
        }

        meta.setLore(lore.isEmpty() ? null : lore);
        item.setItemMeta(meta);
    }

    private static int findManagedLoreIndex(List<String> lore, String previousLine, String prefix) {
        if (lore == null || lore.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < lore.size(); i++) {
            String plain = ChatColor.stripColor(lore.get(i));
            if (previousLine != null ? previousLine.equals(plain) : plain != null && plain.startsWith(prefix)) {
                return i;
            }
        }
        return -1;
    }

    private static void removeManagedLore(List<String> lore, int index) {
        if (index == -1 || index >= lore.size()) {
            return;
        }

        lore.remove(index);
        if (index - 1 >= 0 && lore.get(index - 1).isEmpty()) {
            lore.remove(index - 1);
        }
    }

    private static void trimTrailingEmptyLines(List<String> lore) {
        while (!lore.isEmpty() && lore.get(lore.size() - 1).isEmpty()) {
            lore.remove(lore.size() - 1);
        }
    }

    private static NamespacedKey weightLoreKey() {
        if (weightLoreKey == null) {
            weightLoreKey = new NamespacedKey(getPlugin(), "weightLore");
        }
        return weightLoreKey;
    }

    private static NamespacedKey boostLoreKey() {
        if (boostLoreKey == null) {
            boostLoreKey = new NamespacedKey(getPlugin(), "boostLore");
        }
        return boostLoreKey;
    }

    private static String formatMaterialName(String material) {
        return MATERIAL_NAME_CACHE.computeIfAbsent(material, key -> {
            String[] parts = key.toLowerCase().split("_");
            StringBuilder builder = new StringBuilder();
            for (String part : parts) {
                if (part.isEmpty()) {
                    continue;
                }
                builder.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(' ');
            }
            return builder.toString().trim();
        });
    }
}
