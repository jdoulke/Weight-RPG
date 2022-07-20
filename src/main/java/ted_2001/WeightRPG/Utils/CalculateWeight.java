package ted_2001.WeightRPG.Utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class CalculateWeight {

    private List<String> disabledworlds;

    public void calculateWeight(Player p){
        String PlayerGamemode = p.getGameMode().toString();
        disabledworlds = getPlugin().getConfig().getStringList("disabled-worlds");
        if(PlayerGamemode.equalsIgnoreCase("CREATIVE") || PlayerGamemode.equalsIgnoreCase("SPECTATOR")) {
            p.removePotionEffect(PotionEffectType.SLOW);
            return;
        }
        for (String disabledworld : disabledworlds) {
            if (disabledworld.equalsIgnoreCase((p.getWorld().getName()))) {
                p.removePotionEffect(PotionEffectType.SLOW);
                return;
            }
        }
        PotionEffect sloweffect;
        PlayerInventory inv = p.getInventory();
        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        p.sendMessage(ChatColor.RED+ "Items:");
        int weight = 0;
        for (ItemStack item : items) {
            if (item != null) {
                p.sendMessage(String.valueOf((item.getType())));
                p.sendMessage(String.valueOf(item.getAmount()));
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
        if(weight >= weight1 && weight < weight2){
            p.removePotionEffect(PotionEffectType.SLOW);
            sloweffect = new PotionEffect(PotionEffectType.SLOW, 2000000,0,false, false);
            p.addPotionEffect(sloweffect);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1,1);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getPlugin().getConfig().getString("weight-level-1.message"))));
        }else if(weight >= weight2 && weight < weight3) {
            p.removePotionEffect(PotionEffectType.SLOW);
            sloweffect = new PotionEffect(PotionEffectType.SLOW, 2000000, 1, false, false);
            p.addPotionEffect(sloweffect);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            p.sendMessage(p.getDisplayName() + ChatColor.GOLD + ": I am Carrying too much items.");
        }else if(weight >= weight3) {
            p.removePotionEffect(PotionEffectType.SLOW);
            sloweffect = new PotionEffect(PotionEffectType.SLOW, 2000000, 2, false, false);
            p.addPotionEffect(sloweffect);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1, 1);
            p.sendMessage(p.getDisplayName() + ChatColor.RED + "" +ChatColor.BOLD+": I am Carrying too much items.");
        }else {
            p.removePotionEffect(PotionEffectType.SLOW);

        }
    }
}
