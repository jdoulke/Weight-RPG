package ted_2001.WeightRPG.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;

public class ItemsWeightLore {


    public void onInventoryOpen(Player p){
        PlayerInventory inv = p.getInventory();
        ItemStack[] items = inv.getStorageContents();
        ItemStack[] armor = inv.getArmorContents();
        float itemweight;
        ItemStack itemstack;
        ItemMeta itemmeta;
        ArrayList<String> lore = new ArrayList<>();
        for (ItemStack item : items) {
            if (item != null) {
                if(globalitemsweight.get(item.getType()) != null) {
                    itemweight = globalitemsweight.get(item.getType());
                    itemstack = new ItemStack(item.getType());
                    itemmeta = itemstack.getItemMeta();
                    if (itemmeta != null) {
                        lore = (ArrayList<String>) itemmeta.getLore();
                        if(lore != null) {
                            lore.add("");
                            lore.add("Weight" + (itemweight * item.getAmount()));
                        }else {
                            lore = new ArrayList<>();
                            lore.add("");
                            lore.add("Weight" + (itemweight * item.getAmount()));
                        }
                    }
                    if (itemmeta != null) {
                        itemmeta.setLore(lore);
                    }
                    itemstack.setItemMeta(itemmeta);
                }
            }
        }
        for (ItemStack item: armor) {
            if(item != null) {
                if (globalitemsweight.get(item.getType()) != null) {
                    itemstack = new ItemStack(item.getType());
                    itemmeta = itemstack.getItemMeta();
                    itemweight = globalitemsweight.get(item.getType());
                    lore = (ArrayList<String>) itemmeta.getLore();
                    if(lore != null) {
                        lore.add("");
                        lore.add("Weight" + (itemweight * item.getAmount()));
                    }else {
                        lore = new ArrayList<>();
                        lore.add("");
                        lore.add("Weight" + (itemweight * item.getAmount()));
                    }
                    if (itemmeta != null) {
                        itemmeta.setLore(lore);
                        }
                    itemstack.setItemMeta(itemmeta);
                    }
            }
        }
    }
}
