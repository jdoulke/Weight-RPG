package ted_2001.WeightRPG.Utils.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.WeightRPG;

import static ted_2001.WeightRPG.Utils.CalculateWeight.isEnabled;
import static ted_2001.WeightRPG.Utils.CalculateWeight.playerWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.boostItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;

public class WeightExpansion extends PlaceholderExpansion {

    private final WeightRPG plugin = WeightRPG.getPlugin();
    private final CalculateWeight weightCalculator = new CalculateWeight();

    @Override
    public String getIdentifier() {
        return "weight-rpg";
    }

    @Override
    public String getAuthor() {
        return "Ted_2001";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }

        boolean level2Enabled = plugin.getConfig().getBoolean("weight-level-2.enabled");
        boolean level3Enabled = plugin.getConfig().getBoolean("weight-level-3.enabled");

        if (params.equals("current_weight")) {
            if (!isEnabled(player)) {
                return "0";
            }
            Float currentWeight = playerWeight.get(player.getUniqueId());
            return currentWeight == null ? null : String.format("%.2f", currentWeight);
        }

        if (params.equals("max_weight")) {
            float maxWeight = weightCalculator.calculateWeightThreshold(player, weightCalculator.getEnabledWeightLevel());
            return String.valueOf(maxWeight);
        }

        if (params.equals("weight_level1")) {
            return String.valueOf(weightCalculator.calculateWeightThreshold(player, 1));
        }

        if (params.equals("weight_level2")) {
            return level2Enabled
                    ? String.valueOf(weightCalculator.calculateWeightThreshold(player, 2))
                    : "Level 2 is disabled";
        }

        if (params.equals("weight_level3")) {
            return level3Enabled
                    ? String.valueOf(weightCalculator.calculateWeightThreshold(player, 3))
                    : "Level 3 is disabled";
        }

        if (params.equals("item_in_main_hand")) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            return isEnabled(player) && mainHand.getType() != Material.AIR
                    ? itemWeightCalculations(mainHand)
                    : "0";
        }

        if (params.equals("item_in_second_hand")) {
            ItemStack secondHand = player.getInventory().getItemInOffHand();
            return isEnabled(player) && secondHand.getType() != Material.AIR
                    ? itemWeightCalculations(secondHand)
                    : "0";
        }

        if (params.equals("armor_weight")) {
            float weight = 0f;
            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                if (itemStack != null) {
                    weight += Float.parseFloat(itemWeightCalculations(itemStack));
                }
            }
            return String.valueOf(weight);
        }

        return "Unknown";
    }

    private String itemWeightCalculations(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            Float customWeight = customItemsWeight.get(itemMeta.getDisplayName());
            if (customWeight != null) {
                return String.valueOf(customWeight * item.getAmount());
            }

            // Boost items don't have their own weight.
            if (boostItemsWeight.containsKey(itemMeta.getDisplayName())) {
                return "0";
            }
        }

        return String.valueOf(globalItemsWeight.getOrDefault(item.getType(), 0f) * item.getAmount());
    }
}
