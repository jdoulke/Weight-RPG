package ted_2001.WeightRPG.Utils.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.WeightRPG;

import java.util.Objects;

import static ted_2001.WeightRPG.Utils.CalculateWeight.*;
import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;


public class WeightExpasion extends PlaceholderExpansion {

    private final WeightRPG plugin = WeightRPG.getPlugin();
    CalculateWeight w= new CalculateWeight();
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
                maxWeight = w.calculateWeightLevel3(p);
            else if (Weight2)
                maxWeight = w.calculateWeightLevel2(p);
            else
                maxWeight = w.calculateWeightLevel1(p);

            return String.valueOf(maxWeight);
        }
        if(params.equals("weight_level1")) {
            return String.valueOf(w.calculateWeightLevel1(p));
        }
        if(params.equals("weight_level2"))
            if(Weight2)
                return String.valueOf(w.calculateWeightLevel2(p));
            else
                return "Level 2 is disabled";
        if(params.equals("weight_level3"))
            if(Weight3)
                return String.valueOf(w.calculateWeightLevel3(p));
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

        float itemWeight = 0.0f;
        String weight = "0";
        ItemMeta itemMeta = item.getItemMeta();

        if(itemMeta != null && customItemsWeight.containsKey(itemMeta.getDisplayName())){

            itemWeight = customItemsWeight.get(itemMeta.getDisplayName());
            weight = String.valueOf(itemWeight * item.getAmount());
            return weight;
        }
        // Boost Items don't have weight.
        if(itemMeta != null && boostItemsWeight.containsKey(itemMeta.getDisplayName())){
            return weight;
        }
        if (globalItemsWeight.get(item.getType()) != null) {

            itemWeight = globalItemsWeight.get(item.getType());
            weight = String.valueOf(itemWeight * item.getAmount());
            return weight;
        }
        
        return weight;
    }


}
