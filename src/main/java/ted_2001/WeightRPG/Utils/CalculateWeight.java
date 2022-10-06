package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import ted_2001.WeightRPG.Utils.WorldGuard.WorldGuardRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.Utils.JsonFile.customitemsweight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class CalculateWeight {

    float weight;
    public static HashMap<UUID,Float> playerweight = new HashMap<>();
    public static HashMap<UUID, Long> cooldown = new HashMap<>();
    boolean Weight2 = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
    boolean Weight3 = getPlugin().getConfig().getBoolean("weight-level-3.enabled");
    boolean isWorldGuardEnabled = getServer().getPluginManager().isPluginEnabled("WorldGuard");

    public static float[] weightThresholdValues = new float[]{
            (float) getPlugin().getConfig().getDouble("weight-level-1.value"),
            (float) getPlugin().getConfig().getDouble("weight-level-2.value"),
            (float) getPlugin().getConfig().getDouble("weight-level-3.value")
    };

    // Color codes for progress bar
    private static final String whiteColor = "&f&l";
    private static final String darkRedColor = "&4&l";
    private static final String[] colorCodes = {"&a&l", "&2&l", "&e&l", "&6&l", "&c&l"}; // Light Green, Green, Yellow, Orange, Red

    public void calculateWeight(Player p){
        if(!checkIfEnable(p))
            return;
        if(isWorldGuardEnabled) {
            WorldGuardRegion worldguard = new WorldGuardRegion();
            if (worldguard.isInRegion(p))
                return;
        }
        PlayerInventory inv = p.getInventory();
        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        weight = 0;
        float itemweight;
        boolean customitems;
        for (ItemStack item : items) {
            if (item != null) {
                customitems = false;
                if(customitemsweight.containsKey(Objects.requireNonNull(item.getItemMeta()).getDisplayName())){
                    itemweight = customitemsweight.get(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
                    weight += itemweight * item.getAmount();
                    customitems = true;
                }
                if(globalitemsweight.get(item.getType()) != null && !customitems) {
                    itemweight = globalitemsweight.get(item.getType());
                    weight += itemweight * item.getAmount();
                }
            }
        }
        for (ItemStack itemStack : armor) {
            if(itemStack != null) {
                customitems = false;
                if(customitemsweight.containsKey(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName())){
                    itemweight = weight = customitemsweight.get(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName());
                    weight += itemweight * itemStack.getAmount();
                    customitems = true;
                }
                if (globalitemsweight.get(itemStack.getType()) != null && !customitems) {
                    itemweight = globalitemsweight.get(itemStack.getType());
                    weight += itemweight * itemStack.getAmount();
                }
            }
        }
        playerweight.put(p.getUniqueId(), weight);
        getWeightsEffect(p);

    }



    public void getWeightsEffect(Player p) {
        UUID id = p.getUniqueId();
        if (playerweight.get(id) == null)
            return;
        double weight1 = weightThresholdValues[0];
        double weight2 = weightThresholdValues[1];
        double weight3 = weightThresholdValues[2];
        String message;

        if (playerweight.get(id) <= weight1) {
            if(p.getWalkSpeed() < 0.2)
                p.setWalkSpeed(0.2f);
            if(getPlugin().getConfig().getBoolean("message-before-level1-enabled")) {
                message = getPlugin().getConfig().getString("message-before-level1");
                messageChooser(message, p, null);
            }
        } else if(playerweight.get(id) >= weight1 && playerweight.get(id) < weight2){
            if(getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
                p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-1.speed"));
                message = getPlugin().getConfig().getString("weight-level-1.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-1.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                messageChooser(message, p, s);
            }
        }else if(playerweight.get(id) >= weight2 && playerweight.get(id) < weight3 && Weight2) {
            if(getPlugin().getConfig().getBoolean("weight-level-2.message-enabled")) {
                p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-2.speed"));
                message = getPlugin().getConfig().getString("weight-level-2.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-2.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                messageChooser(message, p, s);
            }
        }else if(playerweight.get(id) >= weight3 && Weight3) {
            if(getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
                p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-3.speed"));
                message = getPlugin().getConfig().getString("weight-level-3.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-3.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                messageChooser(message, p, s);
            }
        }else {
            p.setWalkSpeed(0.2f);
        }
    }

    private void messageChooser(String message, Player p,Sound s) {
        if(!cooldown.containsKey(p.getUniqueId())) {
            cooldownMessager(p,s,message);
        }else{
            long timeElapsed = System.currentTimeMillis() - cooldown.get(p.getUniqueId());
            if(timeElapsed >= getPlugin().getConfig().getDouble("messages-cooldown") * 1000){
                cooldownMessager(p,s,message);
            }
        }
    }

    private boolean checkIfEnable(Player p) {
        List<String> disabledworlds = getPlugin().getConfig().getStringList("disabled-worlds");
        for (String disabledworld : disabledworlds) {
            if (disabledworld.equalsIgnoreCase((p.getWorld().getName()))) {
                p.setWalkSpeed((float) 0.2);
                return false;
            }
        }
        String PlayerGamemode = p.getGameMode().toString();
        if(PlayerGamemode.equalsIgnoreCase("CREATIVE") || PlayerGamemode.equalsIgnoreCase("SPECTATOR"))
            return false;
        return true;
    }


    private void cooldownMessager(Player p, Sound s, String message) {
        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
        if(getPlugin().getConfig().getBoolean("actionbar-messages"))
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', messageSender(message,p))));
        else
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSender(message,p)));
        if(s != null)
            p.playSound(p.getLocation(),s,1,1);
    }




    public String messageSender(String message, Player p){
        message = message.replaceAll("%playername%", p.getName());
        message = message.replaceAll("%displayname%", p.getDisplayName());
        if(playerweight.get(p.getUniqueId())!= null)
            message = message.replaceAll("%weight%", String.format("%.1f",playerweight.get(p.getUniqueId())));
        else
            message = message.replaceAll("%weight%", "0");
        message = message.replaceAll("%world%", p.getWorld().getName());
        message = message.replaceAll("%level1%", Objects.requireNonNull(String.valueOf(weightThresholdValues[0])));
        message = message.replaceAll("%level2%", Objects.requireNonNull(String.valueOf(weightThresholdValues[1])));
        message = message.replaceAll("%level3%", Objects.requireNonNull(String.valueOf(weightThresholdValues[2])));
        message = message.replaceAll("%percentageweight%", generateProgressBar(p));
        message = message.replaceAll("%percentage%", String.format("%.1f", getPercentage(p)));

        if (Weight3)
            message = message.replaceAll("%maxweight%", String.valueOf(weightThresholdValues[2]));
        else if (Weight2)
            message = message.replaceAll("%maxweight%", String.valueOf(weightThresholdValues[1]));
        else
            message = message.replaceAll("%maxweight%", String.valueOf(weightThresholdValues[0]));

        return message;
    }

    public float getPercentage(Player p){
        if(playerweight.get(p.getUniqueId()) == null)
            return 0;
        float weight = playerweight.get(p.getUniqueId()), maxWeight;
        if (Weight3)
            maxWeight = weightThresholdValues[2];
        else if (Weight2)
            maxWeight = weightThresholdValues[1];
        else
            maxWeight = weightThresholdValues[0];

        return weight * 100 / maxWeight;
    }

    public String generateProgressBar(Player p) {
        if (p == null)
            return "";
        float weight;
        float maxWeight;
        if(playerweight.get(p.getUniqueId()) == null)
            weight = 0;
        else
            weight = playerweight.get(p.getUniqueId());
        if (Weight3)
            maxWeight = weightThresholdValues[2];
        else if (Weight2)
            maxWeight = weightThresholdValues[1];
        else
            maxWeight = weightThresholdValues[0];

        float percentage = (weight * 100 / maxWeight);

        StringBuilder message = new StringBuilder(38);
        message.append("&7&l[||||||||||||||||||||&7&l]");

        int coloredBars = ((int)percentage >= 100) ? 20 : (((int)percentage) / 5 + 1);
        int colorIndex = ((int)percentage >= 100) ? 4 : (((int)percentage) / 20);

        if((int)percentage >= 95)
            message.insert(5, darkRedColor);
        else
            message.insert(5, colorCodes[colorIndex]);
        message.insert(9 + coloredBars, whiteColor);

        return ChatColor.translateAlternateColorCodes('&', message.toString());
    }
}
