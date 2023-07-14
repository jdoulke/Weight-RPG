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



    public static HashMap<Material, Float> globalItemsWeight = new HashMap<>();
    public static HashMap<String, Float> customItemsWeight = new HashMap<>();
    private final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Weight-RPG" +ChatColor.GRAY + "] ";

    public boolean successfullyRead = true;
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
                Material[] allItems = Material.values();
                for(int i =1;i < allItems.length;i++){
                    String tempItem = allItems[i].toString();
                    if(allItems[i].isRecord()) {
                        records.put(rcount, tempItem + "=0.5");
                        rcount++;
                    }else if(tempItem.contains("ARROW")) {
                        arrow.put(arrowcount, tempItem + "=0.1");
                        arrowcount++;
                    }else if(tempItem.contains("LEAVES")) {
                        leaves.put(leavescount, tempItem + "=0.01");
                        leavescount++;
                    }else if(tempItem.contains("WOOL")) {
                        wools.put(woolscount, tempItem + "=1");
                        woolscount++;
                    }else if(tempItem.contains("BED")) {
                        bed.put(bedcount, tempItem + "=8");
                        bedcount++;
                    }else if(tempItem.contains("GLASS") && !tempItem.contains("SPYGLASS")) {
                        if(tempItem.contains("PANE"))
                            glass.put(glasscount, tempItem + "=0.4");
                        else
                            glass.put(glasscount, tempItem + "=1");
                        glasscount++;
                    }else if(tempItem.contains("CONCRETE")) {
                        concrete.put(concretecount, tempItem + "=3");
                        concretecount++;
                    }else if(tempItem.contains("TERRACOTTA")) {
                        terracotta.put(terracottacount, tempItem + "=3");
                        terracottacount++;
                    }else if(tempItem.contains("CARPET")) {
                        carpet.put(carpetcount, tempItem + "=0.5");
                        carpetcount++;
                    }else if(tempItem.contains("BUTTON")) {
                        button.put(buttoncount, tempItem + "=0.1");
                        buttoncount++;
                    }else if(tempItem.contains("BOAT")) {
                        boat.put(boatcount, tempItem + "=15");
                        boatcount++;
                    }else if(tempItem.contains("PLATE") && !tempItem.contains("CHESTPLATE")) {
                        plate.put(platecount, tempItem + "=0.3");
                        platecount++;
                    }else if(tempItem.contains("BANNER") && !tempItem.contains("WALL")) {
                        banner.put(bannercount, tempItem + "=1.5");
                        bannercount++;
                    }else if(tempItem.contains("SHULKER_BOX")) {
                        shulker.put(shulkercount, tempItem + "=8");
                        shulkercount++;
                    }else if(tempItem.contains("DOOR")) {
                        if(tempItem.contains("TRAP"))
                            door.put(doorcount, tempItem + "=4");
                        else
                            door.put(doorcount, tempItem + "=8");
                        doorcount++;
                    }else if(tempItem.contains("EGG") && !tempItem.contains("LEGGINGS")) {
                        egg.put(eggcount, tempItem + "=0.1");
                        eggcount++;
                    }else if(tempItem.contains("DYE")) {
                        dyes.put(dyescount, tempItem + "=0.01");
                        dyescount++;
                    }else if(tempItem.contains("FENCE")) {
                        fence.put(fencecount, tempItem + "=1");
                        fencecount++;
                    }else if(tempItem.contains("HORSE_ARMOR")) {
                        horse.put(horsecount, tempItem + "=30");
                        horsecount++;
                    }else if(tempItem.contains("CANDLE") && !tempItem.contains("CAKE")) {
                        candle.put(candlecount, tempItem + "=0.2");
                        candlecount++;
                    }else if(tempItem.contains("SIGN") && !tempItem.contains("WALL")) {
                        sign.put(signcount, tempItem + "=4");
                        signcount++;
                    }else if(tempItem.contains("HEAD") || tempItem.contains("SKULL")) {
                        head.put(headcount, tempItem + "=7");
                        headcount++;
                    }else if(tempItem.contains("BOOTS") || tempItem.contains("LEGGINGS") || tempItem.contains("CHESTPLATE") || tempItem.contains("HELMET")) {
                        if(tempItem.contains("DIAMOND"))
                            armor.put(armorcount, tempItem + "=20");
                        else if(tempItem.contains("GOLD"))
                            armor.put(armorcount, tempItem + "=18");
                        else if(tempItem.contains("NETHERITE"))
                            armor.put(armorcount, tempItem + "=25");
                        else if(tempItem.contains("IRON") || tempItem.contains("CHAINMAIL"))
                            armor.put(armorcount, tempItem + "=15");
                        else
                            armor.put(armorcount, tempItem + "=5");
                        armorcount++;
                    }else if((tempItem.contains("REDSTONE") || tempItem.contains("RAIL") || tempItem.contains("PISTON") || tempItem.contains("DETECTOR") || tempItem.contains("DROPPER") ||
                                tempItem.contains("DISPENSER") || tempItem.contains("OBSERVER") || tempItem.contains("HOPPER") || tempItem.contains("MINECART") || tempItem.contains("REPEATER")||
                                tempItem.contains("COMPARATOR")|| tempItem.contains("TARGET") || tempItem.contains("HOOK"))&& !tempItem.contains("WALL") && !tempItem.contains("MOVING") &&
                                !tempItem.contains("ORE")) {
                            redstone.put(redstonecount, tempItem + "=1");
                            redstonecount++;
                    }else if((tempItem.contains("TABLE") && !tempItem.contains("BOOK")) || tempItem.contains("FURNACE") || tempItem.contains("SMOKER") || tempItem.contains("ANVIL") ||
                            (tempItem.contains("CHEST") && !tempItem.contains("PLATE")) || tempItem.contains("JUKEBOX") || tempItem.contains("LOOM") ||
                            tempItem.contains("COMPOSTER") || tempItem.contains("BARREL") || tempItem.contains("GRIDSTONE") || tempItem.contains("STONECUTTER") ||
                            tempItem.contains("STAND") || tempItem.equalsIgnoreCase("CAULDRON")) {
                        work.put(workcount, tempItem + "=12");
                        workcount++;
                    }else if(tempItem.contains("COOK") || tempItem.contains("APPLE") || (tempItem.contains("CARROT") && !tempItem.contains("STICK")  )|| tempItem.contains("BERRIES") ||
                            tempItem.contains("STEW") || tempItem.contains("BEETROOT") || tempItem.contains("MUTTON") || tempItem.contains("POTATO") || tempItem.contains("CHICKEN") ||
                            tempItem.contains("BEEF") || tempItem.contains("MELON") || (tempItem.contains("CAKE") && !tempItem.contains("CANDLE"))  || tempItem.equalsIgnoreCase("TROPICAL_FISH") || tempItem.equalsIgnoreCase("PUFFERFISH") ||
                            tempItem.equalsIgnoreCase("SALMON") || tempItem.equalsIgnoreCase("COD") || tempItem.contains("BREAD") || tempItem.contains("PORKCHOP") ||
                            tempItem.equalsIgnoreCase("RABBIT")) {
                        food.put(foodcount, tempItem + "=0.5");
                        foodcount++;
                    }else if((!tempItem.contains("COPPER") && tempItem.contains("AXE") )|| tempItem.contains("SWORD") || tempItem.contains("SHOVEL") || tempItem.contains("HOE") || tempItem.contains("SPYGLASS") ||
                            tempItem.contains("FISHING") || tempItem.contains("COMPASS") || tempItem.contains("FLINT_AND_STEEL") || tempItem.equalsIgnoreCase("BOW") || tempItem.contains("NAME_TAG")
                            || tempItem.contains("CROSSBOW") || tempItem.contains("CLOCK") || (tempItem.contains("BOOK") && !tempItem.contains("SHELF"))|| tempItem.contains("PAPER") || tempItem.contains("BUCKET") ||
                            tempItem.contains("SHIELD") || tempItem.contains("TRIDENT") || tempItem.contains("SHEARS") || tempItem.contains("LEAD") || tempItem.contains("MAP")) {
                        if(tempItem.contains("DIAMOND"))
                            tools.put(tcount, tempItem + "=8");
                        else if (tempItem.contains("WOODEN"))
                            tools.put(tcount, tempItem + "=4");
                        else if (tempItem.contains("STONE"))
                            tools.put(tcount, tempItem + "=5");
                        else if (tempItem.contains("IRON"))
                            tools.put(tcount, tempItem + "=6");
                        else if (tempItem.contains("NETHERITE"))
                            tools.put(tcount, tempItem + "=10");
                        else if (tempItem.contains("GOLD"))
                            tools.put(tcount, tempItem + "=7");
                        else if (tempItem.contains("BOW"))
                            tools.put(tcount, tempItem + "=12");
                        else if (tempItem.contains("PAPER") || tempItem.contains("MAP"))
                            tools.put(tcount, tempItem + "=0.2");
                        else if (tempItem.contains("BOOK"))
                            tools.put(tcount, tempItem + "=1");
                        else
                            tools.put(tcount, tempItem + "=5");
                        tcount++;
                    }else if(tempItem.contains("LOG") || tempItem.contains("PLANKS") || tempItem.contains("SAPLING") || tempItem.contains("WOOD")) {
                        if(tempItem.contains("LOG") || tempItem.contains("WOOD"))
                            wood.put(woodcount, tempItem + "=4");
                        else if(tempItem.contains("PLANKS"))
                            wood.put(woodcount, tempItem + "=1");
                        else
                            wood.put(woodcount, tempItem + "=0.2");
                        woodcount++;
                    }else if((tempItem.contains("CORAL") && !tempItem.contains("BLOCK")) || tempItem.contains("POTTED") || tempItem.contains("KELP") || tempItem.contains("VINES") ||
                            tempItem.contains("ROOTS") || tempItem.contains("TULIP") || tempItem.contains("DAISY") || tempItem.contains("FUNGUS") || (tempItem.contains("MUSHROOM") && !tempItem.contains("BLOCK"))||
                            tempItem.contains("BLUET") || tempItem.contains("ORCHID") || tempItem.contains("POPPY") || tempItem.contains("SEAGRASS") || tempItem.contains("AZALEA") ||
                            (tempItem.contains("GRASS") && !tempItem.contains("BLOCK")) || tempItem.contains("BUSH") || tempItem.contains("FERN") || tempItem.contains("ALLIUM") || tempItem.contains("DANDELION") ||
                            tempItem.contains("CORNFLOWER") || tempItem.contains("LILY") || tempItem.contains("ROSE") || tempItem.equalsIgnoreCase("SUGAR_CANE") || tempItem.contains("SPROUTS") || tempItem.contains("DRIPLEAF") ||
                            tempItem.contains("BAMBOO") || tempItem.equalsIgnoreCase("GLOW_LICHEN") || tempItem.contains("PEONY")  || tempItem.contains("SEEDS") || tempItem.contains("PLANT") || tempItem.contains("FLOWER") || tempItem.contains("VINE") || tempItem.contains("LILAC"))  {
                        flowers.put(flowerscount, tempItem + "=0.05");
                        flowerscount++;
                    }else if(tempItem.contains("BLOCK") || tempItem.contains("GRANITE") || tempItem.contains("DIORITE") || tempItem.contains("ANDESITE") ||
                            tempItem.contains("DEEPSLATE") || tempItem.contains("STONE") || tempItem.contains("SLAB") || tempItem.contains("BRICK") ||
                            tempItem.contains("STAIRS") || tempItem.contains("WAXED") || tempItem.contains("DIRT") || tempItem.contains("SAND") ||
                            (tempItem.contains("COPPER") && !tempItem.contains("INGOT")) || tempItem.contains("ORE") || tempItem.contains("PODZOL") ||
                            tempItem.contains("NYLIUM") || tempItem.contains("GRAVEL") || tempItem.contains("SPONGE") || tempItem.contains("HYPHAE") ||
                            tempItem.contains("OBSIDIAN") || tempItem.contains("BEEHIVE") || tempItem.contains("BEE_NEST") || tempItem.contains("BUD") ||
                            tempItem.contains("PILLAR") || tempItem.contains("FARMLAND") || tempItem.contains("CACTUS") || tempItem.contains("PUMPKIN") ||
                            tempItem.contains("BASALT") || tempItem.contains("JACK_O") || tempItem.contains("NETHERRACK") || tempItem.contains("SOUL_SOIL") ||
                            (tempItem.contains("PRISMARINE") && tempItem.contains("SHARD") && tempItem.contains("CRYSTALS")) || tempItem.contains("ICE") ||
                            tempItem.contains("SEA_LANTERN") || tempItem.contains("SCAFFOLDING") || (tempItem.contains("STEM") && !tempItem.contains("MELON")) ||  tempItem.contains("ANCHOR") ||
                            tempItem.contains("CAMPFIRE") || tempItem.contains("SHROOMLIGHT") || tempItem.equalsIgnoreCase("MYCELIUM") || tempItem.contains("BOOKSHELF") ||
                            tempItem.contains("CALCITE") || tempItem.contains("TUFF") || tempItem.contains("ANCIENT") || tempItem.contains("QUARTZ") ||
                            (tempItem.contains("CLAY") && !tempItem.contains("BALL")) || tempItem.contains("MUD") || tempItem.contains("SCULK") || tempItem.contains("FROGLIGHT") ){
                        blocks.put(bcount, tempItem + "=2");
                        bcount++;
                    }else if(tempItem.contains("INGOT") || tempItem.equalsIgnoreCase("DIAMOND") || tempItem.equalsIgnoreCase("EMERALD") ||
                            tempItem.contains("COAL") || tempItem.contains("LAPIS_LAZULI") || tempItem.contains("RAW_IRON") ||
                            tempItem.contains("RAW_GOLD") || tempItem.contains("RAW_COPPER") || tempItem.contains("NETHERITE_SCRAP") ) {
                        ingots.put(ingotscount, tempItem + "=1.5");
                        ingotscount++;
                    }else{
                        if(!tempItem.contains("AIR") && !tempItem.contains("WALL") && !tempItem.equalsIgnoreCase("FIRE") && !tempItem.equalsIgnoreCase("SOUL_FIRE")
                            && !tempItem.equalsIgnoreCase("LAVA") && !tempItem.equalsIgnoreCase("WATER")  && !tempItem.contains("POWDER_SNOW") && !tempItem.contains("CAKE") &&
                            !tempItem.contains("COLUMN")  && !tempItem.contains("GATEWAY")  && !tempItem.contains("PORTAL")  && !tempItem.contains("STEM") && !tempItem.contains("LIGHT") && !tempItem.contains("VOID") &&
                            !tempItem.contains("BUNDLE") && !tempItem.contains("CAULDRON") ) {
                            items.put(icount, tempItem + "=1");
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

    private void addWeightForItems(JSONObject miscWeightObject) {
        try {
            JSONArray heads = miscWeightObject.getJSONArray("Heads Weight");
            JSONArray misc = miscWeightObject.getJSONArray("Misc Items Weight");
            JSONArray banners = miscWeightObject.getJSONArray("Banners Weight");
            JSONArray eggs = miscWeightObject.getJSONArray("Eggs Weight");
            JSONArray ingots = miscWeightObject.getJSONArray("Ingots and Ores Weight");
            JSONArray food = miscWeightObject.getJSONArray("Food Items Weight");
            JSONArray flowers = miscWeightObject.getJSONArray("Flowers Weight");
            JSONArray records = miscWeightObject.getJSONArray("Records Weight");
            JSONArray candles = miscWeightObject.getJSONArray("Candles Weight");
            JSONArray redstone = miscWeightObject.getJSONArray("Redstone Items Weight");
            JSONArray dyes = miscWeightObject.getJSONArray("Dyes Weight");
            if (miscWeightObject.has("Additional Items")) {
                JSONArray addedItems = miscWeightObject.getJSONArray("Additional Items");
                addGlobalItemsWeight(addedItems);
            }
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
            float weight = getWeight(item_weight[1], item, origin);
            globalItemsWeight.put(material,weight);
        }
    }

    private void addCustomItemsWeight() {
        String origin = "Custom";
        List<String> customitems = getPlugin().getConfig().getStringList("custom-items-weight");
        for (String item : customitems) {
            String[] item_weight = item.split("=");
            item = ChatColor.translateAlternateColorCodes('&', item_weight[0]);
            float weight = getWeight(item_weight[1], item, origin);
            customItemsWeight.put(item, weight);
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

            successfullyRead = false;
        }
        return weight;
    }


}
