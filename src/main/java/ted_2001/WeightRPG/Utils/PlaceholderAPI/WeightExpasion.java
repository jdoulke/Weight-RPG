package ted_2001.WeightRPG.Utils.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ted_2001.WeightRPG.WeightRPG;

import static ted_2001.WeightRPG.Utils.CalculateWeight.playerweight;
import static ted_2001.WeightRPG.Utils.CalculateWeight.weightThresholdValues;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class WeightExpasion extends PlaceholderExpansion {

    private WeightRPG plugin = WeightRPG.getPlugin();
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
            if(playerweight.get(p.getUniqueId()) != null) {
                return String.format("%.2f", playerweight.get(p.getUniqueId()));
            }
        }
        if(params.equals("max_weight")){
            float maxWeight = 0;
            if (Weight3)
                maxWeight = weightThresholdValues[2];
            else if (Weight2)
                maxWeight = weightThresholdValues[1];
            else
                maxWeight = weightThresholdValues[0];
            return String.valueOf(maxWeight);
        }
        if(params.equals("weight_level1"))
            return String.valueOf(weightThresholdValues[0]);
        if(params.equals("weight_level2"))
            if(Weight2)
                return String.valueOf(weightThresholdValues[1]);
            else
                return "Level 2 is disabled";
        if(params.equals("weight_level3"))
            if(Weight3)
                return String.valueOf(weightThresholdValues[2]);
            else
                return "Level 3 is disabled";

        return "Unknown";
    }
}
