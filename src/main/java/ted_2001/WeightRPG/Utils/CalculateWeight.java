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
    public static HashMap<UUID,Float> playerWeight = new HashMap<>();
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
        if(!isEnabled(p))
            return;
        if(isWorldGuardEnabled) {
            WorldGuardRegion worldguard = new WorldGuardRegion();
            if (worldguard.isInRegion(p))
                return;
        }
        if(p.hasPermission("weight.bypass")) {
            playerWeight.put(p.getUniqueId(), 0f);
            if(p.getWalkSpeed() < 2.0f)
                p.setWalkSpeed(2.0f);
            return;
        }

        PlayerInventory inv = p.getInventory();
        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        ItemStack secondhand = inv.getItemInOffHand();
        weight = 0;
        for (ItemStack item : items) {
            if (item != null) {
                secondHandWeightCalculator(item);
            }
        }
        for (ItemStack itemStack : armor) {
            if(itemStack != null) {
                itemWeightCalculations(itemStack);
            }
        }
        if(secondhand.getItemMeta() != null){
            secondHandWeightCalculator(secondhand);
        }
        playerWeight.put(p.getUniqueId(), weight);
        getWeightsEffect(p);

    }

    private void itemWeightCalculations(ItemStack itemStack) {
        boolean customItems;
        float itemWeight;
        customItems = false;
        if(customitemsweight.containsKey(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName())){
            itemWeight = customitemsweight.get(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName());
            weight += itemWeight * itemStack.getAmount();
            customItems = true;
        }
        if (globalitemsweight.get(itemStack.getType()) != null && !customItems) {
            itemWeight = globalitemsweight.get(itemStack.getType());
            weight += itemWeight * itemStack.getAmount();
        }
    }

    private void secondHandWeightCalculator(ItemStack secondhand) {
        itemWeightCalculations(secondhand);
    }


    public void getWeightsEffect(Player p) {
        UUID id = p.getUniqueId();

        if (playerWeight.get(id) == null)
            return;
        double weight1;
        double weight2;
        double weight3;
        if(getPlugin.getConfig.getBoolean("permission-mode")){
        
            for(int i = 0; i <= 10000; i--) {
                if(player.hasPermission("weight.level1." + i)) {
                    weight1 = i;
                    break;
                }
            }

            for(int i = 0; i <= 10000; i--) {
                if(player.hasPermission("weight.level2." + i)) {
                    weight2 = i;
                    break;
                }
            }

            for(int i = 0; i <= 100000; i--) {
                if(player.hasPermission("weight.level3." + i)) {
                    weight3 = i;
                    break;
                }
            }
        }else {
            weight1 = weightThresholdValues[0];
            weight2 = weightThresholdValues[1];
            weight3 = weightThresholdValues[2];
        }
        String message;

        if (playerWeight.get(id) <= weight1) {
            if(p.getWalkSpeed() < 0.2)
                p.setWalkSpeed(0.2f);
            if(getPlugin().getConfig().getBoolean("message-before-level1-enabled")) {
                message = getPlugin().getConfig().getString("message-before-level1");
                messageChooser(message, p, null);
            }
        } else if(playerWeight.get(id) >= weight1 && playerWeight.get(id) < weight2){
            p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-1.speed"));
            if(getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
                message = getPlugin().getConfig().getString("weight-level-1.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-1.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                messageChooser(message, p, s);
            }
        }else if(playerWeight.get(id) >= weight2 && playerWeight.get(id) < weight3 && Weight2) {
            p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-2.speed"));
            if(getPlugin().getConfig().getBoolean("weight-level-2.message-enabled")) {
                message = getPlugin().getConfig().getString("weight-level-2.message");
                Sound s = null;
                if (!Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-2.sound")).equalsIgnoreCase("none"))
                    s = Sound.valueOf(getPlugin().getConfig().getString("weight-level-3.sound"));
                messageChooser(message, p, s);
            }
        }else if(playerWeight.get(id) >= weight3 && Weight3) {
            p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-3.speed"));
            if(getPlugin().getConfig().getBoolean("weight-level-1.message-enabled")) {
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
            cooldownMessenger(p,s,message);
        }else{
            long timeElapsed = System.currentTimeMillis() - cooldown.get(p.getUniqueId());
            if(timeElapsed >= getPlugin().getConfig().getDouble("messages-cooldown") * 1000){
                cooldownMessenger(p,s,message);
            }
        }
    }


    public static boolean isEnabled(Player p) {
        List<String> disabledWorlds = getPlugin().getConfig().getStringList("disabled-worlds");
        for (String disabledWorld : disabledWorlds) {
            if (disabledWorld.equalsIgnoreCase((p.getWorld().getName()))) {
                p.setWalkSpeed((float) 0.2);
                return false;
            }
        }
        String PlayerGamemode = p.getGameMode().toString();
        return !PlayerGamemode.equalsIgnoreCase("CREATIVE") && !PlayerGamemode.equalsIgnoreCase("SPECTATOR");
    }


    private void cooldownMessenger(Player p, Sound s, String message) {
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
        if(playerWeight.get(p.getUniqueId())!= null)
            message = message.replaceAll("%weight%", String.format("%.2f", playerWeight.get(p.getUniqueId())));
        else
            message = message.replaceAll("%weight%", "0");
        message = message.replaceAll("%world%", p.getWorld().getName());
        message = message.replaceAll("%level1%", Objects.requireNonNull(String.valueOf(weightThresholdValues[0])));
        message = message.replaceAll("%level2%", Objects.requireNonNull(String.valueOf(weightThresholdValues[1])));
        message = message.replaceAll("%level3%", Objects.requireNonNull(String.valueOf(weightThresholdValues[2])));
        message = message.replaceAll("%percentageweight%", generateProgressBar(p));
        message = message.replaceAll("%percentage%", String.format("%.2f", getPercentage(p)));

        if (Weight3)
            message = message.replaceAll("%maxweight%", String.valueOf(weightThresholdValues[2]));
        else if (Weight2)
            message = message.replaceAll("%maxweight%", String.valueOf(weightThresholdValues[1]));
        else
            message = message.replaceAll("%maxweight%", String.valueOf(weightThresholdValues[0]));

        return message;
    }

    public float getPercentage(Player p){
        if(playerWeight.get(p.getUniqueId()) == null)
            return 0;
        float weight = playerWeight.get(p.getUniqueId()), maxWeight;
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
        if(playerWeight.get(p.getUniqueId()) == null)
            weight = 0;
        else
            weight = playerWeight.get(p.getUniqueId());
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
