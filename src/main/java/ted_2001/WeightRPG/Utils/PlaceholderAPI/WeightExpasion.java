package ted_2001.WeightRPG.Utils.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ted_2001.WeightRPG.WeightRPG;

import java.util.Objects;

import static ted_2001.WeightRPG.Utils.CalculateWeight.*;
import static ted_2001.WeightRPG.Utils.JsonFile.customitemsweight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;


public class WeightExpasion extends PlaceholderExpansion {

    private final WeightRPG plugin = WeightRPG.getPlugin();
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
        return "1.0";
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
    public String onPlaceholderRequest(Player p, String params) {
        boolean Weight2 = plugin.getConfig().getBoolean("weight-level-2.enabled");
        boolean Weight3 = plugin.getConfig().getBoolean("weight-level-3.enabled");
        if(p == null){
            return "";
        }
        if(params.equals("current_weight")){
            if(isEnabled(p)) {
                if (playerWeight.get(p.getUniqueId()) != null) {
                    return String.format("%.2f", playerWeight.get(p.getUniqueId()));
                }
            }else
                return "0";
        }
        if(params.equals("max_weight")){
            float maxWeight = 0;
            if (Weight3)
                maxWeight = calculateWeightLevel3(p);
            else if (Weight2)
                maxWeight = calculateWeightLevel2(p);
            else
                maxWeight = calculateWeightLevel1(p);

            return String.valueOf(maxWeight);
        }
        if(params.equals("weight_level1")) {
            return String.valueOf(calculateWeightLevel1(p));
        }
        if(params.equals("weight_level2"))
            if(Weight2)
                return String.valueOf(calculateWeightLevel2(p));
            else
                return "Level 2 is disabled";
        if(params.equals("weight_level3"))
            if(Weight3)
                return String.valueOf(calculateWeightLevel3(p));
            else
                return "Level 3 is disabled";
        if(params.equals("item_in_main_hand")){
            ItemStack mainHand = p.getInventory().getItemInMainHand();
            if(isEnabled(p)) {
                if (mainHand.getType() != Material.AIR)
                    return itemWeightCalculations(mainHand);
                else
                    return "0";
            }else
                return "0";
        }
        if(params.equals("item_in_second_hand")){
            ItemStack secondHand = p.getInventory().getItemInOffHand();
            if(isEnabled(p)) {
                if (secondHand.getType() != Material.AIR)
                    return itemWeightCalculations(secondHand);
                else
                    return "0";
            }else
                return "0";
        }
        if(params.equals("armor_weight")){
            ItemStack[] armor = p.getInventory().getArmorContents();
            float weight = 0;
            for (ItemStack itemStack : armor) {
                if(itemStack != null) {
                    weight += Float.parseFloat(itemWeightCalculations(itemStack));
                }
            }
            return String.valueOf(weight);
        }
        return "Unknown";
    }

    private String itemWeightCalculations(ItemStack item) {
        boolean customItems;
        float itemWeight;
        String weight = "0";
        customItems = false;
        if(customitemsweight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName())){
            itemWeight = customitemsweight.get(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
            weight = String.valueOf(itemWeight * item.getAmount());
            customItems = true;
        }
        if (globalitemsweight.get(item.getType()) != null && !customItems) {
            itemWeight = globalitemsweight.get(item.getType());
            weight = String.valueOf(itemWeight * item.getAmount());
        }
        return weight;
    }

    private float calculateWeightLevel1(Player p){
        float weight = 0;
        if(plugin.getConfig().getBoolean("permission-mode")){
            for(int i = 0; i <= 10000; i++) {
                if(p.hasPermission("weight.level1." + i)) {
                    weight = i;
                    break;
                }else {
                    weight = weightThresholdValues[0];
                }
            }
        }
        else {
            weight = weightThresholdValues[0];
        }
        return weight;
    }

    private float calculateWeightLevel2(Player p){
        float weight = 0;
        if(plugin.getConfig().getBoolean("permission-mode")){
            for(int i = 0; i <= 10000; i++) {
                if(p.hasPermission("weight.level2." + i)) {
                    weight = i;
                    break;
                }else {
                    weight = weightThresholdValues[1];
                }
            }
        }
        else {
            weight = weightThresholdValues[1];
        }
        return weight;
    }

    private float calculateWeightLevel3(Player p){
        float weight = 0;
        if(plugin.getConfig().getBoolean("permission-mode")){
            for(int i = 0; i <= 100000; i++) {
                if(p.hasPermission("weight.level3." + i)) {
                    weight = i;
                    break;
                }else {
                    weight = weightThresholdValues[2];
                }
            }
        }
        else {
            weight = weightThresholdValues[2];
        }
        return weight;
    }
}
