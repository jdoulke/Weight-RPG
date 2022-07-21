package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class CalculateWeight {

    private List<String> disabledworlds;

    float weight;
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
        ItemMeta itemsmeta;
        boolean loreflag = false;
        p.sendMessage(ChatColor.RED+ "Items:");
        weight = 0;
        for (ItemStack item : items) {
            if (item != null) {
                p.sendMessage(String.valueOf((item.getType())));
                p.sendMessage(String.valueOf(item.getAmount()));
                /*itemsmeta = item.getItemMeta();
                if(itemsmeta != null) {
                    if (itemsmeta.hasLore()) {
                        List<String> lore = itemsmeta.getLore();
                        for(int i = 0;i < lore.size();i++){
                            if(lore.get(i).contains("Weight")){
                                lore.set(i, "Weight " + getWeight(item));
                                loreflag = true;
                            }
                        }if(!loreflag){
                            lore.add("Weight " + getWeight(item));
                        }
                    }
                }//stone,5 dirt,5 oak_wooden_planks 10
                loreflag = false;*/

                weight += item.getAmount();
            }
        }
        p.sendMessage(ChatColor.YELLOW+ "weight =" + ChatColor.RED+weight);
        p.sendMessage(ChatColor.BLUE+"Armor");
        for (ItemStack itemStack : armor) {
            if (itemStack != null) {
                p.sendMessage(String.valueOf(itemStack));
                weight += 5;
            }
        }
        p.sendMessage(ChatColor.YELLOW+ "weight =" + ChatColor.RED+weight);
        double weight1 = getPlugin().getConfig().getDouble("weight-level-1.value");
        double weight2 = getPlugin().getConfig().getDouble("weight-level-2.value");
        double weight3 = getPlugin().getConfig().getDouble("weight-level-3.value");
        String message;
        if(weight >= weight1 && weight < weight2){
            p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-1.speed"));
            Sound s = getPlugin().getConfig().getObject("weight-level-1.sound", Sound.class);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1,1);
            message = getPlugin().getConfig().getString("weight-level-1.message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSender(message,p)));
        }else if(weight >= weight2 && weight < weight3 && Weight2) {
            p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-2.speed"));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            message = getPlugin().getConfig().getString("weight-level-2.message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSender(message,p)));
        }else if(weight >= weight3 && Weight3) {
            p.setWalkSpeed((float) getPlugin().getConfig().getDouble("weight-level-3.speed"));
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            message = getPlugin().getConfig().getString("weight-level-3.message");
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', messageSender(message,p)));
        }else {
            p.setWalkSpeed((float) 0.2);
        }
    }


    private String messageSender(String message, Player p){
        message = message.replaceAll("%playername%", p.getName());
        message = message.replaceAll("%displayname%", p.getDisplayName());
        message = message.replaceAll("%weight%", String.valueOf(weight));
        message = message.replaceAll("%world%", p.getWorld().getName());
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
}
