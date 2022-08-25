package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class CalculateWeight {

    private List<String> disabledworlds;

    float weight;
    public static HashMap<UUID,Float> playerweight = new HashMap<>();
    public static HashMap<UUID, Long> cooldown = new HashMap<>();
    boolean Weight2 = getPlugin().getConfig().getBoolean("weight-level-2.enabled");
    boolean Weight3 = getPlugin().getConfig().getBoolean("weight-level-3.enabled");




    public void calculateWeight(Player p){
        String PlayerGamemode = p.getGameMode().toString();
        disabledworlds = getPlugin().getConfig().getStringList("disabled-worlds");
        if(PlayerGamemode.equalsIgnoreCase("CREATIVE") || PlayerGamemode.equalsIgnoreCase("SPECTATOR")) {
            p.setWalkSpeed((float) 0.2);
            return;
        }
        for (String disabledworld : disabledworlds) {
            if (disabledworld.equalsIgnoreCase((p.getWorld().getName()))) {
                p.setWalkSpeed((float) 0.2);
                return;
            }
        }
        PlayerInventory inv = p.getInventory();
        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        weight = 0;
        float itemweight = 0;
        for (ItemStack item : items) {
            if (item != null) {
                if(globalitemsweight.get(item.getType()) != null) {
                    itemweight = globalitemsweight.get(item.getType());
                    weight += itemweight * item.getAmount();
                }
            }
        }
        for (ItemStack itemStack : armor) {
            if(itemStack != null) {
                if (globalitemsweight.get(itemStack.getType()) != null) {
                    itemweight = globalitemsweight.get(itemStack.getType());
                    weight += itemweight * itemStack.getAmount();
                }
            }
        }
        weight = (float) (Math.round(weight * 1000.0) / 1000.0);
        playerweight.put(p.getUniqueId(), weight);
        getWeightsEffect(p);

    }

    public void getWeightsEffect(Player p) {
        double weight1 = getPlugin().getConfig().getDouble("weight-level-1.value");
        double weight2 = getPlugin().getConfig().getDouble("weight-level-2.value");
        double weight3 = getPlugin().getConfig().getDouble("weight-level-3.value");
        UUID id = p.getUniqueId();
        String message;
        if(playerweight.get(id) == null)
            return;
        if(playerweight.get(id) <= weight1){
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
            p.setWalkSpeed((float) 0.2);
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
        message = message.replaceAll("%weight%", String.valueOf(playerweight.get(p.getUniqueId())));
        message = message.replaceAll("%world%", p.getWorld().getName());
        message = message.replaceAll("%level1%", Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-1.value")));
        message = message.replaceAll("%level2%", Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-2.value")));
        message = message.replaceAll("%level3%", Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-3.value")));
        message = message.replaceAll("%percentageweight%", percentageGetter(p));
        message = message.replaceAll("%percentage%", String.valueOf(percentagegetter(p)));
        if(Weight3) {
            float maxweight = (float) getPlugin().getConfig().getDouble("weight-level-3.value");
            message = message.replaceAll("%maxweight%", String.valueOf(maxweight));
            return message;
        }else if(Weight2){
            float maxweight = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
            message = message.replaceAll("%maxweight%", String.valueOf(maxweight));
            return message;
        }else {
            float maxweight = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
            message = message.replaceAll("%maxweight%", String.valueOf(maxweight));
            return message;
        }
    }
    public float percentagegetter(Player p){
        if(playerweight.get(p.getUniqueId()) == null)
            return 0;
        float weight = playerweight.get(p.getUniqueId());
        float maxweight;
        if(Weight3) {
            maxweight = (float) getPlugin().getConfig().getDouble("weight-level-3.value");
        }else if(Weight2){
            maxweight = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
        }else {
            maxweight = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
        }
        return (weight * 100/maxweight);
    }

    public String percentageGetter(Player p){
        float weight = playerweight.get(p.getUniqueId());
        float maxweight;
        String message = "";
        if(Weight3) {
            maxweight = (float) getPlugin().getConfig().getDouble("weight-level-3.value");
        }else if(Weight2){
            maxweight = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
        }else {
            maxweight = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
        }
        float percentage =  (weight * 100/maxweight);
        percentage = (float) (Math.round(percentage * 1000.0) / 1000.0);
        if (percentage >= 0 && percentage <= 5.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&a&l|&f&l|||||||||||||||||||&7&l]");
        }else if (percentage >= 6 && percentage <= 10.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&a&l||&f&l||||||||||||||||||&7&l]");
        }else if (percentage >= 11 && percentage <= 14.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&a&l|||&f&l|||||||||||||||||&7&l]");
        }else if (percentage >= 15 && percentage <= 19.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&2&l||||&f&l||||||||||||||||&7&l]");
        }else if (percentage >= 20 && percentage <= 24.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&2&l|||||&f&l|||||||||||||||&7&l]");
        }else if (percentage >= 25 && percentage <= 29.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&2&l||||||&f&l||||||||||||||&7&l]");
        }else if (percentage >= 30 && percentage <= 34.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&2&l|||||||&f&l|||||||||||||&7&l]");
        }else if (percentage >= 35 && percentage <= 39.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&e&l||||||||&f&l||||||||||||&7&l]");
        }else if (percentage >= 40 && percentage <= 44.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&e&l|||||||||&f&l|||||||||||&7&l]");
        }else if (percentage >= 45 && percentage <= 49.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&e&l||||||||||&f&l||||||||||&7&l]");
        }else if (percentage >= 50 && percentage <= 54.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&6&l|||||||||||&f&l|||||||||&7&l]");
        }else if (percentage >= 55 && percentage <= 59.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&6&l||||||||||||&f&l||||||||&7&l]");
        }else if (percentage >= 60 && percentage <= 64.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&6&l|||||||||||||&f&l|||||||&7&l]");
        }else if (percentage >= 65 && percentage <= 69.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&6&l||||||||||||||&f&l||||||&7&l]");
        }else if (percentage >= 70 && percentage <= 74.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&c&l|||||||||||||||&f&l|||||&7&l]");
        }else if (percentage >= 75 && percentage <= 79.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&c&l||||||||||||||||&f&l||||&7&l]");
        }else if (percentage >= 80 && percentage <= 84.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&c&l|||||||||||||||||&f&l|||&7&l]");
        }else if (percentage >= 85 && percentage <= 89.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&4&l||||||||||||||||||&f&l||&7&l]");
        }else if (percentage >= 90 && percentage <= 94.99){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&4&l|||||||||||||||||||&f&l|&7&l]");
        }else if (percentage >= 95){
            message = ChatColor.translateAlternateColorCodes('&', "&7&l[&4&l||||||||||||||||||||&7&l]");
        }
        return message;
    }
}