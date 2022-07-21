package ted_2001.WeightRPG.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;


import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class JsonFile {

    public void saveJsonFile(){
        JSONObject ItemsWeight = new JSONObject();
        JSONObject ArmorAndWeaponsWeight = new JSONObject();
        JSONArray blocks = new JSONArray();
        JSONArray items = new JSONArray();
        JSONArray records = new JSONArray();
        JSONArray armor = new JSONArray();
        JSONArray tools = new JSONArray();
        JSONArray arrow = new JSONArray();
        JSONArray wools = new JSONArray();
        JSONArray terracotta = new JSONArray();
        JSONArray glass = new JSONArray();
        JSONArray concrete = new JSONArray();
        JSONArray flowers = new JSONArray();
        JSONArray carpet = new JSONArray();
        JSONArray candle = new JSONArray();
        JSONArray banner = new JSONArray();
        JSONArray wood = new JSONArray();
        JSONArray leaves = new JSONArray();
        JSONArray head = new JSONArray();
        JSONArray bed = new JSONArray();
        JSONArray shulker = new JSONArray();
        JSONArray ingots = new JSONArray();
        JSONArray plate = new JSONArray();
        JSONArray button = new JSONArray();
        int bcount = 0, icount = 0, rcount = 0, armorcount = 0, tcount = 0, arrowcount = 0 ,woolscount = 0, terracottacount = 0, glasscount = 0, concretecount = 0;
        int flowerscount = 0, carpetcount = 0, candlecount = 0, headcount = 0, bannercount = 0, bedcount = 0, woodcount = 0 , leavescount = 0, shulkercount = 0;
        int ingotscount = 0, buttoncount = 0, platecount = 0;
        File weights = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Items Weight.json");
        if(getPlugin().getDataFolder().exists()) {
            if (!weights.exists()) {
                Material[] allitems = Material.values();
                for(int i =1;i < allitems.length;i++){
                    String tempitem = allitems[i].toString();
                    if(allitems[i].isRecord()) {
                        records.put(rcount, tempitem + "=0.5");
                        rcount++;
                    }else if(tempitem.contains("ARROW")) {
                        arrow.put(arrowcount, tempitem + "=0.1");
                        arrowcount++;
                    }else if(tempitem.contains("LEAVES")) {
                        leaves.put(leavescount, tempitem + "=0.01");
                        leavescount++;
                    }else if(tempitem.contains("WOOL")) {
                        wools.put(woolscount, tempitem + "=1");
                        woolscount++;
                    }else if(tempitem.contains("BED")) {
                        bed.put(bedcount, tempitem + "=8");
                        bedcount++;
                    }else if(tempitem.contains("GLASS") && !tempitem.contains("SPYGLASS")) {
                        if(tempitem.contains("PANE"))
                            glass.put(glasscount, tempitem + "=0.4");
                        else
                            glass.put(glasscount, tempitem + "=1");
                        glasscount++;
                    }else if(tempitem.contains("CONCRETE")) {
                        concrete.put(concretecount, tempitem + "=3");
                        concretecount++;
                    }else if(tempitem.contains("TERRACOTTA")) {
                        terracotta.put(terracottacount, tempitem + "=3");
                        terracottacount++;
                    }else if(tempitem.contains("CARPET")) {
                        carpet.put(carpetcount, tempitem + "=0.5");
                        carpetcount++;
                    }else if(tempitem.contains("BUTTON")) {
                        button.put(buttoncount, tempitem + "=0.1");
                        buttoncount++;
                    }else if(tempitem.contains("PLATE")) {
                        plate.put(platecount, tempitem + "=0.3");
                        platecount++;
                    }else if(tempitem.contains("BANNER") && !tempitem.contains("WALL") && !tempitem.contains("PATTERN")) {
                        banner.put(bannercount, tempitem + "=1.5");
                        bannercount++;
                    }else if(tempitem.contains("SHULKER_BOX")) {
                        shulker.put(shulkercount, tempitem + "=1.5");
                        shulkercount++;
                    }else if(tempitem.contains("CANDLE") && !tempitem.contains("CAKE")) {
                        candle.put(candlecount, tempitem + "=0.2");
                        candlecount++;
                    }else if(tempitem.contains("HEAD") || tempitem.contains("SKULL")) {
                        head.put(headcount, tempitem + "=7");
                        headcount++;
                    }else if((tempitem.contains("CORAL") && !tempitem.contains("BLOCK")) || tempitem.contains("POTTED")) {
                        flowers.put(flowerscount, tempitem + "=0.05");
                        flowerscount++;
                    }else if(tempitem.contains("CHESTPLATE") || tempitem.contains("LEGGINGS") || tempitem.contains("HELMET") || tempitem.contains("BOOTS")) {
                        if (tempitem.contains("IRON") || tempitem.contains("CHAINMAIL"))
                            armor.put(armorcount, tempitem + "=10");
                        else if (tempitem.contains("NETHERITE"))
                            armor.put(armorcount, tempitem + "=25");
                        else if (tempitem.contains("GOLDEN"))
                            armor.put(armorcount, tempitem + "=14");
                        else if (tempitem.contains("DIAMOND"))
                            armor.put(armorcount, tempitem + "=20");
                        else
                            armor.put(armorcount, tempitem + "=5");
                        armorcount++;
                    }else if((!tempitem.contains("COPPER") && tempitem.contains("AXE") )|| tempitem.contains("SWORD") || tempitem.contains("SHOVEL") || tempitem.contains("HOE") || tempitem.contains("SPYGLASS") ||
                            tempitem.contains("FISHING") || tempitem.contains("COMPASS") || tempitem.contains("FLINT_AND_STEEL") || tempitem.equalsIgnoreCase("BOW") || tempitem.contains("NAME_TAG")
                            || tempitem.contains("CROSSBOW")) {
                        if(tempitem.contains("DIAMOND"))
                            tools.put(tcount, tempitem + "=8");
                        else if (tempitem.contains("WOODEN"))
                            tools.put(tcount, tempitem + "=4");
                        else if (tempitem.contains("STONE"))
                            tools.put(tcount, tempitem + "=5");
                        else if (tempitem.contains("IRON"))
                            tools.put(tcount, tempitem + "=6");
                        else if (tempitem.contains("NETHERITE"))
                            tools.put(tcount, tempitem + "=10");
                        else if (tempitem.contains("GOLD"))
                            tools.put(tcount, tempitem + "=7");
                        else if (tempitem.contains("BOW"))
                            tools.put(tcount, tempitem + "=12");
                        else
                            tools.put(tcount, tempitem + "=5");
                        tcount++;
                    }else if(tempitem.contains("LOG") || tempitem.contains("PLANKS") || tempitem.contains("SAPLING") || tempitem.contains("WOOD")) {
                        if(tempitem.contains("LOG") || tempitem.contains("WOOD"))
                            wood.put(woodcount, tempitem + "=4");
                        else if(tempitem.contains("PLANKS"))
                            wood.put(woodcount, tempitem + "=1");
                        else
                            wood.put(woodcount, tempitem + "=0.2");
                        woodcount++;
                    }else if(tempitem.contains("BLOCK") || tempitem.contains("GRANITE") ||tempitem.contains("DIORITE") ||tempitem.contains("ANDESITE") ||
                            tempitem.contains("DEEPSLATE") || tempitem.contains("STONE") || tempitem.contains("SLAB") || tempitem.contains("BRICK") ||
                            tempitem.contains("STAIRS") || tempitem.contains("WAXED")) {
                        blocks.put(bcount, tempitem + "=2");
                        bcount++;
                    }else if(tempitem.contains("INGOT") || tempitem.contains("DIAMOND") || tempitem.contains("EMERALD") ||
                            tempitem.contains("COAL") || tempitem.contains("LAPIS_LAZULI") || tempitem.contains("RAW_IRON") ||
                            tempitem.contains("RAW_GOLD") || tempitem.contains("RAW_COPPER") || tempitem.contains("NETHERITE_SCRAP")) {
                        ingots.put(ingotscount, tempitem + "=1.5");
                        ingotscount++;
                    }else{
                        //if(allitems[i].isSolid()) {
                            items.put(icount, tempitem + "=1");
                            icount++;
                        //}
                    }
                }
                ArmorAndWeaponsWeight.put("Armor Weight", armor);
                ArmorAndWeaponsWeight.put("Tools Weight", tools);
                ArmorAndWeaponsWeight.put("Arrows Weight", arrow);
                ItemsWeight.put("Blocks Weight", blocks);
                ItemsWeight.put("Logs, Planks and Saplings Weight", wood);
                ItemsWeight.put("Concretes Weight", concrete);
                ItemsWeight.put("Carpets Weight", carpet);
                ItemsWeight.put("Candles Weight", candle);
                ItemsWeight.put("Heads Weight", head);
                ItemsWeight.put("Banners Weight", banner);
                ItemsWeight.put("Glasses Weight", glass);
                ItemsWeight.put("Beds Weight", bed);
                ItemsWeight.put("Terracottas Weight", terracotta);
                ItemsWeight.put("Wools Weight", wools);
                ItemsWeight.put("Records Weight", records);
                ItemsWeight.put("Misc Items Weight", items);
                ItemsWeight.put("Leaves Weight", leaves);
                ItemsWeight.put("Shulker Boxes Weight", shulker);
                ItemsWeight.put("Ingots and Ores Weight", ingots);
                ItemsWeight.put("Plates Weight", plate);
                ItemsWeight.put("Buttons Weight", button);
                try (PrintWriter out = new PrintWriter(new FileWriter(weights))) {
                    out.write(ItemsWeight.toString(2));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try (PrintWriter out2 = new PrintWriter(new FileWriter(getPlugin().getDataFolder().getAbsolutePath() +"\\Tools and Weapons Weight.json"))) {
                    out2.write(ArmorAndWeaponsWeight.toString(2));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
