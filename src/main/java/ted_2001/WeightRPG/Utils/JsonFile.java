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
                Material[] allΙtems = Material.values();
                for(int i =1;i < allΙtems.length;i++){
                    String tempΙtem = allΙtems[i].toString();
                    if(allΙtems[i].isRecord()) {
                        records.put(rcount, tempΙtem + "=0.5");
                        rcount++;
                    }else if(tempΙtem.contains("ARROW")) {
                        arrow.put(arrowcount, tempΙtem + "=0.1");
                        arrowcount++;
                    }else if(tempΙtem.contains("LEAVES")) {
                        leaves.put(leavescount, tempΙtem + "=0.01");
                        leavescount++;
                    }else if(tempΙtem.contains("WOOL")) {
                        wools.put(woolscount, tempΙtem + "=1");
                        woolscount++;
                    }else if(tempΙtem.contains("BED")) {
                        bed.put(bedcount, tempΙtem + "=8");
                        bedcount++;
                    }else if(tempΙtem.contains("GLASS") && !tempΙtem.contains("SPYGLASS")) {
                        if(tempΙtem.contains("PANE"))
                            glass.put(glasscount, tempΙtem + "=0.4");
                        else
                            glass.put(glasscount, tempΙtem + "=1");
                        glasscount++;
                    }else if(tempΙtem.contains("CONCRETE")) {
                        concrete.put(concretecount, tempΙtem + "=3");
                        concretecount++;
                    }else if(tempΙtem.contains("TERRACOTTA")) {
                        terracotta.put(terracottacount, tempΙtem + "=3");
                        terracottacount++;
                    }else if(tempΙtem.contains("CARPET")) {
                        carpet.put(carpetcount, tempΙtem + "=0.5");
                        carpetcount++;
                    }else if(tempΙtem.contains("BUTTON")) {
                        button.put(buttoncount, tempΙtem + "=0.1");
                        buttoncount++;
                    }else if(tempΙtem.contains("BOAT")) {
                        boat.put(boatcount, tempΙtem + "=15");
                        boatcount++;
                    }else if(tempΙtem.contains("PLATE") && !tempΙtem.contains("CHESTPLATE")) {
                        plate.put(platecount, tempΙtem + "=0.3");
                        platecount++;
                    }else if(tempΙtem.contains("BANNER") && !tempΙtem.contains("WALL")) {
                        banner.put(bannercount, tempΙtem + "=1.5");
                        bannercount++;
                    }else if(tempΙtem.contains("SHULKER_BOX")) {
                        shulker.put(shulkercount, tempΙtem + "=8");
                        shulkercount++;
                    }else if(tempΙtem.contains("DOOR")) {
                        if(tempΙtem.contains("TRAP"))
                            door.put(doorcount, tempΙtem + "=4");
                        else
                            door.put(doorcount, tempΙtem + "=8");
                        doorcount++;
                    }else if(tempΙtem.contains("EGG") && !tempΙtem.contains("LEGGINGS")) {
                        egg.put(eggcount, tempΙtem + "=0.1");
                        eggcount++;
                    }else if(tempΙtem.contains("DYE")) {
                        dyes.put(dyescount, tempΙtem + "=0.01");
                        dyescount++;
                    }else if(tempΙtem.contains("FENCE")) {
                        fence.put(fencecount, tempΙtem + "=1");
                        fencecount++;
                    }else if(tempΙtem.contains("HORSE_ARMOR")) {
                        horse.put(horsecount, tempΙtem + "=30");
                        horsecount++;
                    }else if(tempΙtem.contains("CANDLE") && !tempΙtem.contains("CAKE")) {
                        candle.put(candlecount, tempΙtem + "=0.2");
                        candlecount++;
                    }else if(tempΙtem.contains("SIGN") && !tempΙtem.contains("WALL")) {
                        sign.put(signcount, tempΙtem + "=4");
                        signcount++;
                    }else if(tempΙtem.contains("HEAD") || tempΙtem.contains("SKULL")) {
                        head.put(headcount, tempΙtem + "=7");
                        headcount++;
                    }else if(tempΙtem.contains("BOOTS") || tempΙtem.contains("LEGGINGS") || tempΙtem.contains("CHESTPLATE") || tempΙtem.contains("HELMET")) {
                        if(tempΙtem.contains("DIAMOND"))
                            armor.put(armorcount, tempΙtem + "=20");
                        else if(tempΙtem.contains("GOLD"))
                            armor.put(armorcount, tempΙtem + "=18");
                        else if(tempΙtem.contains("NETHERITE"))
                            armor.put(armorcount, tempΙtem + "=25");
                        else if(tempΙtem.contains("IRON") || tempΙtem.contains("CHAINMAIL"))
                            armor.put(armorcount, tempΙtem + "=15");
                        else
                            armor.put(armorcount, tempΙtem + "=5");
                        armorcount++;
                    }else if((tempΙtem.contains("REDSTONE") || tempΙtem.contains("RAIL") || tempΙtem.contains("PISTON") || tempΙtem.contains("DETECTOR") || tempΙtem.contains("DROPPER") ||
                                tempΙtem.contains("DISPENSER") || tempΙtem.contains("OBSERVER") || tempΙtem.contains("HOPPER") || tempΙtem.contains("MINECART") || tempΙtem.contains("REPEATER")||
                                tempΙtem.contains("COMPARATOR")|| tempΙtem.contains("TARGET") || tempΙtem.contains("HOOK"))&& !tempΙtem.contains("WALL") && !tempΙtem.contains("MOVING") &&
                                !tempΙtem.contains("ORE")) {
                            redstone.put(redstonecount, tempΙtem + "=1");
                            redstonecount++;
                    }else if((tempΙtem.contains("TABLE") && !tempΙtem.contains("BOOK")) || tempΙtem.contains("FURNACE") || tempΙtem.contains("SMOKER") || tempΙtem.contains("ANVIL") ||
                            (tempΙtem.contains("CHEST") && !tempΙtem.contains("PLATE")) || tempΙtem.contains("JUKEBOX") || tempΙtem.contains("LOOM") ||
                            tempΙtem.contains("COMPOSTER") || tempΙtem.contains("BARREL") || tempΙtem.contains("GRIDSTONE") || tempΙtem.contains("STONECUTTER") ||
                            tempΙtem.contains("STAND") || tempΙtem.equalsIgnoreCase("CAULDRON")) {
                        work.put(workcount, tempΙtem + "=12");
                        workcount++;
                    }else if(tempΙtem.contains("COOK") || tempΙtem.contains("APPLE") || (tempΙtem.contains("CARROT") && !tempΙtem.contains("STICK")  )|| tempΙtem.contains("BERRIES") ||
                            tempΙtem.contains("STEW") || tempΙtem.contains("BEETROOT") || tempΙtem.contains("MUTTON") || tempΙtem.contains("POTATO") || tempΙtem.contains("CHICKEN") ||
                            tempΙtem.contains("BEEF") || tempΙtem.contains("MELON") || (tempΙtem.contains("CAKE") && !tempΙtem.contains("CANDLE"))  || tempΙtem.equalsIgnoreCase("TROPICAL_FISH") || tempΙtem.equalsIgnoreCase("PUFFERFISH") ||
                            tempΙtem.equalsIgnoreCase("SALMON") || tempΙtem.equalsIgnoreCase("COD") || tempΙtem.contains("BREAD") || tempΙtem.contains("PORKCHOP") ||
                            tempΙtem.equalsIgnoreCase("RABBIT")) {
                        food.put(foodcount, tempΙtem + "=0.5");
                        foodcount++;
                    }else if((!tempΙtem.contains("COPPER") && tempΙtem.contains("AXE") )|| tempΙtem.contains("SWORD") || tempΙtem.contains("SHOVEL") || tempΙtem.contains("HOE") || tempΙtem.contains("SPYGLASS") ||
                            tempΙtem.contains("FISHING") || tempΙtem.contains("COMPASS") || tempΙtem.contains("FLINT_AND_STEEL") || tempΙtem.equalsIgnoreCase("BOW") || tempΙtem.contains("NAME_TAG")
                            || tempΙtem.contains("CROSSBOW") || tempΙtem.contains("CLOCK") || (tempΙtem.contains("BOOK") && !tempΙtem.contains("SHELF"))|| tempΙtem.contains("PAPER") || tempΙtem.contains("BUCKET") ||
                            tempΙtem.contains("SHIELD") || tempΙtem.contains("TRIDENT") || tempΙtem.contains("SHEARS") || tempΙtem.contains("LEAD") || tempΙtem.contains("MAP")) {
                        if(tempΙtem.contains("DIAMOND"))
                            tools.put(tcount, tempΙtem + "=8");
                        else if (tempΙtem.contains("WOODEN"))
                            tools.put(tcount, tempΙtem + "=4");
                        else if (tempΙtem.contains("STONE"))
                            tools.put(tcount, tempΙtem + "=5");
                        else if (tempΙtem.contains("IRON"))
                            tools.put(tcount, tempΙtem + "=6");
                        else if (tempΙtem.contains("NETHERITE"))
                            tools.put(tcount, tempΙtem + "=10");
                        else if (tempΙtem.contains("GOLD"))
                            tools.put(tcount, tempΙtem + "=7");
                        else if (tempΙtem.contains("BOW"))
                            tools.put(tcount, tempΙtem + "=12");
                        else if (tempΙtem.contains("PAPER") || tempΙtem.contains("MAP"))
                            tools.put(tcount, tempΙtem + "=0.2");
                        else if (tempΙtem.contains("BOOK"))
                            tools.put(tcount, tempΙtem + "=1");
                        else
                            tools.put(tcount, tempΙtem + "=5");
                        tcount++;
                    }else if(tempΙtem.contains("LOG") || tempΙtem.contains("PLANKS") || tempΙtem.contains("SAPLING") || tempΙtem.contains("WOOD")) {
                        if(tempΙtem.contains("LOG") || tempΙtem.contains("WOOD"))
                            wood.put(woodcount, tempΙtem + "=4");
                        else if(tempΙtem.contains("PLANKS"))
                            wood.put(woodcount, tempΙtem + "=1");
                        else
                            wood.put(woodcount, tempΙtem + "=0.2");
                        woodcount++;
                    }else if((tempΙtem.contains("CORAL") && !tempΙtem.contains("BLOCK")) || tempΙtem.contains("POTTED") || tempΙtem.contains("KELP") || tempΙtem.contains("VINES") ||
                            tempΙtem.contains("ROOTS") || tempΙtem.contains("TULIP") || tempΙtem.contains("DAISY") || tempΙtem.contains("FUNGUS") || (tempΙtem.contains("MUSHROOM") && !tempΙtem.contains("BLOCK"))||
                            tempΙtem.contains("BLUET") || tempΙtem.contains("ORCHID") || tempΙtem.contains("POPPY") || tempΙtem.contains("SEAGRASS") || tempΙtem.contains("AZALEA") ||
                            (tempΙtem.contains("GRASS") && !tempΙtem.contains("BLOCK")) || tempΙtem.contains("BUSH") || tempΙtem.contains("FERN") || tempΙtem.contains("ALLIUM") || tempΙtem.contains("DANDELION") ||
                            tempΙtem.contains("CORNFLOWER") || tempΙtem.contains("LILY") || tempΙtem.contains("ROSE") || tempΙtem.equalsIgnoreCase("SUGAR_CANE") || tempΙtem.contains("SPROUTS") || tempΙtem.contains("DRIPLEAF") ||
                            tempΙtem.contains("BAMBOO") || tempΙtem.equalsIgnoreCase("GLOW_LICHEN") || tempΙtem.contains("PEONY")  || tempΙtem.contains("SEEDS") || tempΙtem.contains("PLANT") || tempΙtem.contains("FLOWER") || tempΙtem.contains("VINE") || tempΙtem.contains("LILAC"))  {
                        flowers.put(flowerscount, tempΙtem + "=0.05");
                        flowerscount++;
                    }else if(tempΙtem.contains("BLOCK") || tempΙtem.contains("GRANITE") || tempΙtem.contains("DIORITE") || tempΙtem.contains("ANDESITE") ||
                            tempΙtem.contains("DEEPSLATE") || tempΙtem.contains("STONE") || tempΙtem.contains("SLAB") || tempΙtem.contains("BRICK") ||
                            tempΙtem.contains("STAIRS") || tempΙtem.contains("WAXED") || tempΙtem.contains("DIRT") || tempΙtem.contains("SAND") ||
                            (tempΙtem.contains("COPPER") && !tempΙtem.contains("INGOT")) || tempΙtem.contains("ORE") || tempΙtem.contains("PODZOL") ||
                            tempΙtem.contains("NYLIUM") || tempΙtem.contains("GRAVEL") || tempΙtem.contains("SPONGE") || tempΙtem.contains("HYPHAE") ||
                            tempΙtem.contains("OBSIDIAN") || tempΙtem.contains("BEEHIVE") || tempΙtem.contains("BEE_NEST") || tempΙtem.contains("BUD") ||
                            tempΙtem.contains("PILLAR") || tempΙtem.contains("FARMLAND") || tempΙtem.contains("CACTUS") || tempΙtem.contains("PUMPKIN") ||
                            tempΙtem.contains("BASALT") || tempΙtem.contains("JACK_O") || tempΙtem.contains("NETHERRACK") || tempΙtem.contains("SOUL_SOIL") ||
                            (tempΙtem.contains("PRISMARINE") && tempΙtem.contains("SHARD") && tempΙtem.contains("CRYSTALS")) || tempΙtem.contains("ICE") ||
                            tempΙtem.contains("SEA_LANTERN") || tempΙtem.contains("SCAFFOLDING") || (tempΙtem.contains("STEM") && !tempΙtem.contains("MELON")) ||  tempΙtem.contains("ANCHOR") ||
                            tempΙtem.contains("CAMPFIRE") || tempΙtem.contains("SHROOMLIGHT") || tempΙtem.equalsIgnoreCase("MYCELIUM") || tempΙtem.contains("BOOKSHELF") ||
                            tempΙtem.contains("CALCITE") || tempΙtem.contains("TUFF") || tempΙtem.contains("ANCIENT") || tempΙtem.contains("QUARTZ") ||
                            (tempΙtem.contains("CLAY") && !tempΙtem.contains("BALL")) || tempΙtem.contains("MUD") || tempΙtem.contains("SCULK") || tempΙtem.contains("FROGLIGHT") ){
                        blocks.put(bcount, tempΙtem + "=2");
                        bcount++;
                    }else if(tempΙtem.contains("INGOT") || tempΙtem.equalsIgnoreCase("DIAMOND") || tempΙtem.equalsIgnoreCase("EMERALD") ||
                            tempΙtem.contains("COAL") || tempΙtem.contains("LAPIS_LAZULI") || tempΙtem.contains("RAW_IRON") ||
                            tempΙtem.contains("RAW_GOLD") || tempΙtem.contains("RAW_COPPER") || tempΙtem.contains("NETHERITE_SCRAP") ) {
                        ingots.put(ingotscount, tempΙtem + "=1.5");
                        ingotscount++;
                    }else{
                        if(!tempΙtem.contains("AIR") && !tempΙtem.contains("WALL") && !tempΙtem.equalsIgnoreCase("FIRE") && !tempΙtem.equalsIgnoreCase("SOUL_FIRE")
                            && !tempΙtem.equalsIgnoreCase("LAVA") && !tempΙtem.equalsIgnoreCase("WATER")  && !tempΙtem.contains("POWDER_SNOW") && !tempΙtem.contains("CAKE") &&
                            !tempΙtem.contains("COLUMN")  && !tempΙtem.contains("GATEWAY")  && !tempΙtem.contains("PORTAL")  && !tempΙtem.contains("STEM") && !tempΙtem.contains("LIGHT") && !tempΙtem.contains("VOID") &&
                            !tempΙtem.contains("BUNDLE") && !tempΙtem.contains("CAULDRON") ) {
                            items.put(icount, tempΙtem + "=1");
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
            float weight = getWeight(item_weight[1], item, origin);
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
