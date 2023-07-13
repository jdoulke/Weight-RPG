package ted_2001.WeightRPG.Utils;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



import java.io.*;
import java.util.HashMap;
import java.util.List;


import static org.bukkit.Bukkit.getServer;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;



public class JsonFile {



    public static HashMap<Material, Float> globalitemsweight = new HashMap<>();
    public static HashMap<String, Float> customitemsweight = new HashMap<>();
    private final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Weight-RPG" +ChatColor.GRAY + "] ";

    public boolean successfullRead = true;
    public void saveJsonFile(){
        JSONObject ItemsWeight = new JSONObject();
        JSONObject ArmorAndWeaponsWeight = new JSONObject();
        JSONObject BlocksWeight = new JSONObject();
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
        JSONArray door = new JSONArray();
        JSONArray fence = new JSONArray();
        JSONArray egg = new JSONArray();
        JSONArray dyes = new JSONArray();
        JSONArray sign = new JSONArray();
        JSONArray horse = new JSONArray();
        JSONArray redstone = new JSONArray();
        JSONArray boat = new JSONArray();
        JSONArray food = new JSONArray();
        JSONArray work = new JSONArray();
        int bcount = 0, icount = 0, rcount = 0, armorcount = 0, tcount = 0, arrowcount = 0 ,woolscount = 0, terracottacount = 0, glasscount = 0, concretecount = 0;
        int flowerscount = 0, carpetcount = 0, candlecount = 0, headcount = 0, bannercount = 0, bedcount = 0, woodcount = 0 , leavescount = 0, shulkercount = 0;
        int ingotscount = 0, buttoncount = 0, platecount = 0, doorcount = 0, fencecount = 0, eggcount = 0, dyescount = 0, signcount = 0, horsecount = 0, redstonecount = 0;
        int boatcount = 0, workcount = 0, foodcount = 0;
        File blocksWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Blocks Weight.json");
        File toolsWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Tools And Weapons Weight.json");
        File miscWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Misc Items Weight.json");
        if(getPlugin().getDataFolder().exists()) {
            if (!blocksWeight.exists() || !toolsWeight.exists() || !miscWeight.exists()) {
                getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Weights files don't exist. Creating them...");
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
                    }else if(tempitem.contains("BOAT")) {
                        boat.put(boatcount, tempitem + "=15");
                        boatcount++;
                    }else if(tempitem.contains("PLATE") && !tempitem.contains("CHESTPLATE")) {
                        plate.put(platecount, tempitem + "=0.3");
                        platecount++;
                    }else if(tempitem.contains("BANNER") && !tempitem.contains("WALL")) {
                        banner.put(bannercount, tempitem + "=1.5");
                        bannercount++;
                    }else if(tempitem.contains("SHULKER_BOX")) {
                        shulker.put(shulkercount, tempitem + "=8");
                        shulkercount++;
                    }else if(tempitem.contains("DOOR")) {
                        if(tempitem.contains("TRAP"))
                            door.put(doorcount, tempitem + "=4");
                        else
                            door.put(doorcount, tempitem + "=8");
                        doorcount++;
                    }else if(tempitem.contains("EGG") && !tempitem.contains("LEGGINGS")) {
                        egg.put(eggcount, tempitem + "=0.1");
                        eggcount++;
                    }else if(tempitem.contains("DYE")) {
                        dyes.put(dyescount, tempitem + "=0.01");
                        dyescount++;
                    }else if(tempitem.contains("FENCE")) {
                        fence.put(fencecount, tempitem + "=1");
                        fencecount++;
                    }else if(tempitem.contains("HORSE_ARMOR")) {
                        horse.put(horsecount, tempitem + "=30");
                        horsecount++;
                    }else if(tempitem.contains("CANDLE") && !tempitem.contains("CAKE")) {
                        candle.put(candlecount, tempitem + "=0.2");
                        candlecount++;
                    }else if(tempitem.contains("SIGN") && !tempitem.contains("WALL")) {
                        sign.put(signcount, tempitem + "=4");
                        signcount++;
                    }else if(tempitem.contains("HEAD") || tempitem.contains("SKULL")) {
                        head.put(headcount, tempitem + "=7");
                        headcount++;
                    }else if(tempitem.contains("BOOTS") || tempitem.contains("LEGGINGS") || tempitem.contains("CHESTPLATE") || tempitem.contains("HELMET")) {
                        if(tempitem.contains("DIAMOND"))
                            armor.put(armorcount, tempitem + "=20");
                        else if(tempitem.contains("GOLD"))
                            armor.put(armorcount, tempitem + "=18");
                        else if(tempitem.contains("NETHERITE"))
                            armor.put(armorcount, tempitem + "=25");
                        else if(tempitem.contains("IRON") || tempitem.contains("CHAINMAIL"))
                            armor.put(armorcount, tempitem + "=15");
                        else
                            armor.put(armorcount, tempitem + "=5");
                        armorcount++;
                    }else if((tempitem.contains("REDSTONE") || tempitem.contains("RAIL") || tempitem.contains("PISTON") || tempitem.contains("DETECTOR") || tempitem.contains("DROPPER") ||
                                tempitem.contains("DISPENSER") || tempitem.contains("OBSERVER") || tempitem.contains("HOPPER") || tempitem.contains("MINECART") || tempitem.contains("REPEATER")||
                                tempitem.contains("COMPARATOR")|| tempitem.contains("TARGET") || tempitem.contains("HOOK"))&& !tempitem.contains("WALL") && !tempitem.contains("MOVING") &&
                                !tempitem.contains("ORE")) {
                            redstone.put(redstonecount, tempitem + "=1");
                            redstonecount++;
                    }else if((tempitem.contains("TABLE") && !tempitem.contains("BOOK")) || tempitem.contains("FURNACE") || tempitem.contains("SMOKER") || tempitem.contains("ANVIL") ||
                            (tempitem.contains("CHEST") && !tempitem.contains("PLATE")) || tempitem.contains("JUKEBOX") || tempitem.contains("LOOM") ||
                            tempitem.contains("COMPOSTER") || tempitem.contains("BARREL") || tempitem.contains("GRIDSTONE") || tempitem.contains("STONECUTTER") ||
                            tempitem.contains("STAND") ||tempitem.equalsIgnoreCase("CAULDRON")) {
                        work.put(workcount, tempitem + "=12");
                        workcount++;
                    }else if(tempitem.contains("COOK") ||tempitem.contains("APPLE") || (tempitem.contains("CARROT") && !tempitem.contains("STICK")  )|| tempitem.contains("BERRIES") ||
                            tempitem.contains("STEW") || tempitem.contains("BEETROOT") || tempitem.contains("MUTTON") || tempitem.contains("POTATO") || tempitem.contains("CHICKEN") ||
                            tempitem.contains("BEEF") || tempitem.contains("MELON") || (tempitem.contains("CAKE") && !tempitem.contains("CANDLE"))  || tempitem.equalsIgnoreCase("TROPICAL_FISH") || tempitem.equalsIgnoreCase("PUFFERFISH") ||
                            tempitem.equalsIgnoreCase("SALMON") || tempitem.equalsIgnoreCase("COD") || tempitem.contains("BREAD") || tempitem.contains("PORKCHOP") ||
                            tempitem.equalsIgnoreCase("RABBIT")) {
                        food.put(foodcount, tempitem + "=0.5");
                        foodcount++;
                    }else if((!tempitem.contains("COPPER") && tempitem.contains("AXE") )|| tempitem.contains("SWORD") || tempitem.contains("SHOVEL") || tempitem.contains("HOE") || tempitem.contains("SPYGLASS") ||
                            tempitem.contains("FISHING") || tempitem.contains("COMPASS") || tempitem.contains("FLINT_AND_STEEL") || tempitem.equalsIgnoreCase("BOW") || tempitem.contains("NAME_TAG")
                            || tempitem.contains("CROSSBOW") || tempitem.contains("CLOCK") || (tempitem.contains("BOOK") && !tempitem.contains("SHELF"))|| tempitem.contains("PAPER") || tempitem.contains("BUCKET") ||
                            tempitem.contains("SHIELD") || tempitem.contains("TRIDENT") || tempitem.contains("SHEARS") || tempitem.contains("LEAD") || tempitem.contains("MAP")) {
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
                        else if (tempitem.contains("PAPER") || tempitem.contains("MAP"))
                            tools.put(tcount, tempitem + "=0.2");
                        else if (tempitem.contains("BOOK"))
                            tools.put(tcount, tempitem + "=1");
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
                    }else if((tempitem.contains("CORAL") && !tempitem.contains("BLOCK")) || tempitem.contains("POTTED") || tempitem.contains("KELP") || tempitem.contains("VINES") ||
                            tempitem.contains("ROOTS") || tempitem.contains("TULIP") || tempitem.contains("DAISY") || tempitem.contains("FUNGUS") || (tempitem.contains("MUSHROOM") && !tempitem.contains("BLOCK"))||
                            tempitem.contains("BLUET") || tempitem.contains("ORCHID") || tempitem.contains("POPPY") || tempitem.contains("SEAGRASS") || tempitem.contains("AZALEA") ||
                            (tempitem.contains("GRASS") && !tempitem.contains("BLOCK")) || tempitem.contains("BUSH") || tempitem.contains("FERN") || tempitem.contains("ALLIUM") || tempitem.contains("DANDELION") ||
                            tempitem.contains("CORNFLOWER") || tempitem.contains("LILY") || tempitem.contains("ROSE") || tempitem.equalsIgnoreCase("SUGAR_CANE") || tempitem.contains("SPROUTS") || tempitem.contains("DRIPLEAF") ||
                            tempitem.contains("BAMBOO") ||tempitem.equalsIgnoreCase("GLOW_LICHEN") ||tempitem.contains("PEONY")  ||tempitem.contains("SEEDS") || tempitem.contains("PLANT") || tempitem.contains("FLOWER") ||tempitem.contains("VINE") || tempitem.contains("LILAC"))  {
                        flowers.put(flowerscount, tempitem + "=0.05");
                        flowerscount++;
                    }else if(tempitem.contains("BLOCK") || tempitem.contains("GRANITE") ||tempitem.contains("DIORITE") ||tempitem.contains("ANDESITE") ||
                            tempitem.contains("DEEPSLATE") || tempitem.contains("STONE") || tempitem.contains("SLAB") || tempitem.contains("BRICK") ||
                            tempitem.contains("STAIRS") || tempitem.contains("WAXED") || tempitem.contains("DIRT") || tempitem.contains("SAND") ||
                            (tempitem.contains("COPPER") && !tempitem.contains("INGOT")) || tempitem.contains("ORE") || tempitem.contains("PODZOL") ||
                            tempitem.contains("NYLIUM") || tempitem.contains("GRAVEL") || tempitem.contains("SPONGE") || tempitem.contains("HYPHAE") ||
                            tempitem.contains("OBSIDIAN") || tempitem.contains("BEEHIVE") || tempitem.contains("BEE_NEST") || tempitem.contains("BUD") ||
                            tempitem.contains("PILLAR") || tempitem.contains("FARMLAND") || tempitem.contains("CACTUS") || tempitem.contains("PUMPKIN") ||
                            tempitem.contains("BASALT") || tempitem.contains("JACK_O") || tempitem.contains("NETHERRACK") || tempitem.contains("SOUL_SOIL") ||
                            (tempitem.contains("PRISMARINE") && tempitem.contains("SHARD") && tempitem.contains("CRYSTALS")) || tempitem.contains("ICE") ||
                            tempitem.contains("SEA_LANTERN") || tempitem.contains("SCAFFOLDING") || (tempitem.contains("STEM") && !tempitem.contains("MELON")) ||  tempitem.contains("ANCHOR") ||
                            tempitem.contains("CAMPFIRE") || tempitem.contains("SHROOMLIGHT") || tempitem.equalsIgnoreCase("MYCELIUM") ||tempitem.contains("BOOKSHELF") ||
                            tempitem.contains("CALCITE") || tempitem.contains("TUFF") || tempitem.contains("ANCIENT") || tempitem.contains("QUARTZ") ||
                            (tempitem.contains("CLAY") && !tempitem.contains("BALL")) || tempitem.contains("MUD") || tempitem.contains("SCULK") || tempitem.contains("FROGLIGHT") ){
                        blocks.put(bcount, tempitem + "=2");
                        bcount++;
                    }else if(tempitem.contains("INGOT") || tempitem.equalsIgnoreCase("DIAMOND") || tempitem.equalsIgnoreCase("EMERALD") ||
                            tempitem.contains("COAL") || tempitem.contains("LAPIS_LAZULI") || tempitem.contains("RAW_IRON") ||
                            tempitem.contains("RAW_GOLD") || tempitem.contains("RAW_COPPER") || tempitem.contains("NETHERITE_SCRAP") ) {
                        ingots.put(ingotscount, tempitem + "=1.5");
                        ingotscount++;
                    }else{
                        if(!tempitem.contains("AIR") && !tempitem.contains("WALL") && !tempitem.equalsIgnoreCase("FIRE") && !tempitem.equalsIgnoreCase("SOUL_FIRE")
                            && !tempitem.equalsIgnoreCase("LAVA") && !tempitem.equalsIgnoreCase("WATER")  && !tempitem.contains("POWDER_SNOW") && !tempitem.contains("CAKE") &&
                            !tempitem.contains("COLUMN")  && !tempitem.contains("GATEWAY")  && !tempitem.contains("PORTAL")  && !tempitem.contains("STEM") && !tempitem.contains("LIGHT") && !tempitem.contains("VOID") &&
                            !tempitem.contains("BUNDLE") && !tempitem.contains("CAULDRON") ) {
                            items.put(icount, tempitem + "=1");
                            icount++;
                        }
                    }
                }
                ArmorAndWeaponsWeight.put("Armor Weight", armor);
                ArmorAndWeaponsWeight.put("Tools Weight", tools);
                ArmorAndWeaponsWeight.put("Arrows Weight", arrow);
                ArmorAndWeaponsWeight.put("Horse Armor Weight", horse);
                BlocksWeight.put("Blocks Weight", blocks);
                BlocksWeight.put("Working Tables and Furnaces Weight", work);
                BlocksWeight.put("Boats Weight", boat);
                BlocksWeight.put("Glasses Weight", glass);
                BlocksWeight.put("Beds Weight", bed);
                BlocksWeight.put("Terracottas Weight", terracotta);
                BlocksWeight.put("Wools Weight", wools);
                BlocksWeight.put("Logs, Planks and Saplings Weight", wood);
                BlocksWeight.put("Concretes Weight", concrete);
                BlocksWeight.put("Carpets Weight", carpet);
                BlocksWeight.put("Leaves Weight", leaves);
                BlocksWeight.put("Shulker Boxes Weight", shulker);
                BlocksWeight.put("Plates Weight", plate);
                BlocksWeight.put("Buttons Weight", button);
                BlocksWeight.put("Doors Weight", door);
                BlocksWeight.put("Fences Weight", fence);
                BlocksWeight.put("Signs Weight", sign);
                ItemsWeight.put("Candles Weight", candle);
                ItemsWeight.put("Heads Weight", head);
                ItemsWeight.put("Banners Weight", banner);
                ItemsWeight.put("Food Items Weight", food);
                ItemsWeight.put("Records Weight", records);
                ItemsWeight.put("Misc Items Weight", items);
                ItemsWeight.put("Ingots and Ores Weight", ingots);
                ItemsWeight.put("Redstone Items Weight", redstone);
                ItemsWeight.put("Eggs Weight", egg);
                ItemsWeight.put("Dyes Weight", dyes);
                ItemsWeight.put("Flowers Weight", flowers);
                try (PrintWriter out = new PrintWriter(new FileWriter(blocksWeight))) {
                    out.write(BlocksWeight.toString(2));
                }catch (Exception e){
                    e.printStackTrace();
                }try (PrintWriter out = new PrintWriter(new FileWriter(toolsWeight))) {
                    out.write(ArmorAndWeaponsWeight.toString(2));
                }catch (Exception e){
                    e.printStackTrace();
                }try (PrintWriter out = new PrintWriter(new FileWriter(miscWeight))) {
                    out.write(ItemsWeight.toString(2));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void readJsonFile() {
        InputStream is;
        try {
            is = new FileInputStream(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Blocks Weight.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        JSONTokener tokener = new JSONTokener(is);
        JSONObject blocksweight = new JSONObject(tokener);
        addWeightForBlocks(blocksweight);
        try {
            is = new FileInputStream(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Misc Items Weight.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        tokener = new JSONTokener(is);
        JSONObject miscweight = new JSONObject(tokener);
        addWeightForItems(miscweight);
        try {
            is = new FileInputStream(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Tools And Weapons Weight.json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        tokener = new JSONTokener(is);
        JSONObject armor = new JSONObject(tokener);
        addWeightForArmor(armor);
        addCustomItemsWeight();
    }

    private void addWeightForArmor(JSONObject armor) {
        JSONArray horse = armor.getJSONArray("Horse Armor Weight");
        JSONArray tools = armor.getJSONArray("Tools Weight");
        JSONArray arrow = armor.getJSONArray("Arrows Weight");
        JSONArray armorw = armor.getJSONArray("Armor Weight");
        addGlobalItemsWeight(horse);
        addGlobalItemsWeight(tools);
        addGlobalItemsWeight(arrow);
        addGlobalItemsWeight(armorw);
    }

    private void addWeightForItems(JSONObject miscweight) {
        try {
            JSONArray heads = miscweight.getJSONArray("Heads Weight");
            JSONArray misc = miscweight.getJSONArray("Misc Items Weight");
            JSONArray banners = miscweight.getJSONArray("Banners Weight");
            JSONArray eggs = miscweight.getJSONArray("Eggs Weight");
            JSONArray ingots = miscweight.getJSONArray("Ingots and Ores Weight");
            JSONArray food = miscweight.getJSONArray("Food Items Weight");
            JSONArray flowers = miscweight.getJSONArray("Flowers Weight");
            JSONArray records = miscweight.getJSONArray("Records Weight");
            JSONArray candles = miscweight.getJSONArray("Candles Weight");
            JSONArray redstone = miscweight.getJSONArray("Redstone Items Weight");
            JSONArray dyes = miscweight.getJSONArray("Dyes Weight");
            addGlobalItemsWeight(heads);
            addGlobalItemsWeight(misc);
            addGlobalItemsWeight(banners);
            addGlobalItemsWeight(eggs);
            addGlobalItemsWeight(ingots);
            addGlobalItemsWeight(food);
            addGlobalItemsWeight(flowers);
            addGlobalItemsWeight(records);
            addGlobalItemsWeight(candles);
            addGlobalItemsWeight(redstone);
            addGlobalItemsWeight(dyes);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    private void addWeightForBlocks(JSONObject blocksweight) {
        try {
            JSONArray blocks = blocksweight.getJSONArray("Blocks Weight");
            JSONArray doors = blocksweight.getJSONArray("Doors Weight");
            JSONArray signs = blocksweight.getJSONArray("Signs Weight");
            JSONArray wools = blocksweight.getJSONArray("Wools Weight");
            JSONArray leaves = blocksweight.getJSONArray("Leaves Weight");
            JSONArray glasses = blocksweight.getJSONArray("Glasses Weight");
            JSONArray boats = blocksweight.getJSONArray("Boats Weight");
            JSONArray fence = blocksweight.getJSONArray("Fences Weight");
            JSONArray plates = blocksweight.getJSONArray("Plates Weight");
            JSONArray carpets = blocksweight.getJSONArray("Carpets Weight");
            JSONArray terracotta = blocksweight.getJSONArray("Terracottas Weight");
            JSONArray beds = blocksweight.getJSONArray("Beds Weight");
            JSONArray shulker = blocksweight.getJSONArray("Shulker Boxes Weight");
            JSONArray button = blocksweight.getJSONArray("Buttons Weight");
            JSONArray concretes = blocksweight.getJSONArray("Concretes Weight");
            JSONArray table = blocksweight.getJSONArray("Working Tables and Furnaces Weight");
            JSONArray wood = blocksweight.getJSONArray("Logs, Planks and Saplings Weight");
            addGlobalItemsWeight(blocks);
            addGlobalItemsWeight(doors);
            addGlobalItemsWeight(signs);
            addGlobalItemsWeight(leaves);
            addGlobalItemsWeight(glasses);
            addGlobalItemsWeight(wools);
            addGlobalItemsWeight(boats);
            addGlobalItemsWeight(fence);
            addGlobalItemsWeight(carpets);
            addGlobalItemsWeight(terracotta);
            addGlobalItemsWeight(plates);
            addGlobalItemsWeight(beds);
            addGlobalItemsWeight(shulker);
            addGlobalItemsWeight(button);
            addGlobalItemsWeight(concretes);
            addGlobalItemsWeight(table);
            addGlobalItemsWeight(wood);
        }catch (JSONException e){
            getPlugin().getServer().getConsoleSender().sendMessage(e.getMessage());
        }
    }

    private void addGlobalItemsWeight(JSONArray array) {
        String origin = "Vanilla";
        for(int i = 0; i < array.length(); i++){
            String item = array.get(i).toString();
            String[] item_weight = item.split("=");
            item = item_weight[0];
            Material material = Material.getMaterial(item);
            float weight = getWeight(item_weight, item, origin);
            globalitemsweight.put(material,weight);
        }
    }

    private void addCustomItemsWeight() {
        String origin = "Custom";
        List<String> customitems = getPlugin().getConfig().getStringList("custom-items-weight");
        for (String item : customitems) {
            String[] item_weight = item.split("=");
            item = ChatColor.translateAlternateColorCodes('&', item_weight[0]);
            float weight = getWeight(item_weight[1], item, origin);
            customitemsweight.put(item, weight);
        }
    }


    private float getWeight(String item_weight, String item, String origin) {
        float weight = 0;
        try {
            weight = Float.parseFloat(item_weight);
        }catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
            if(origin.equalsIgnoreCase("Custom") && e.toString().contains("Number"))
                getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Custom item " + ChatColor.BLUE + item + ChatColor.GRAY +" in config.yml couldn't be added in the weight list because of an ERROR.");
            else if(origin.equalsIgnoreCase("Vanilla") && e.toString().contains("Number"))
                getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Item " + item + ChatColor.GRAY+" in Weight files couldn't be added in the weight list because of an ERROR.");
            else if(origin.equalsIgnoreCase("Custom") && e.toString().contains("Array")) {
                getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Custom item " + item + ChatColor.GRAY + " in config.yml couldn't be added in the weight list because of an ERROR.");
                getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "You may have forgotten to write the '" + ChatColor.RED + "=" + ChatColor.GRAY + "' in custom-items-weight.");
            }else{
                getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Item " + ChatColor.BLUE + item + ChatColor.GRAY + " in Weight files couldn't be added in the weight list because of an ERROR.");
                getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "You may have forgotten to write the '" + ChatColor.RED + "=" + ChatColor.GRAY + "' in json file.");
            }
            getPlugin().getServer().getConsoleSender().sendMessage(pluginPrefix + ChatColor.RED +"ERROR" +ChatColor.GRAY + " Message: " + ChatColor.RED + e.getMessage());

            successfullRead = false;
        }
        return weight;
    }


}
