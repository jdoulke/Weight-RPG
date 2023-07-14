package ted_2001.WeightRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;


import java.io.*;
import java.util.Iterator;
import java.util.List;

import static ted_2001.WeightRPG.Utils.JsonFile.customitemsweight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;
import static ted_2001.WeightRPG.Utils.CalculateWeight.weightThresholdValues;


public class WeightCommand implements CommandExecutor {

    private final JsonFile js = new JsonFile();
    CalculateWeight w = new CalculateWeight();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(sender instanceof Player ){
            Player p = (Player) sender;
            if(p.hasPermission("weight.use")){
                if(args.length == 0){
                    List<String> disabledWorlds = getPlugin().getConfig().getStringList("disabled-worlds");
                    for (String disabledWorld : disabledWorlds) {
                        if (disabledWorld.equalsIgnoreCase((p.getWorld().getName()))) {
                            p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED +"is disabled in this world.");
                            return false;
                        }
                    }
                    String PlayerGamemode = p.getGameMode().toString();
                    if(PlayerGamemode.equalsIgnoreCase("CREATIVE") ) {
                        p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED + "is disabled on " + ChatColor.GREEN + "Creative Mode.");
                         return false;
                    }
                    if(PlayerGamemode.equalsIgnoreCase("SPECTATOR") ) {
                        p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED + "is disabled on " + ChatColor.GREEN + "Spectator Mode.");
                        return false;
                    }
                    if(p.hasPermission("weight.bypass")){
                        p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED + "is disabled on " + ChatColor.GREEN + "Bypass Mode.");
                        return false;
                    }
                    List<String> message = Messages.getMessages().getStringList("weight-command-message");
                    for (String s : message)
                        p.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', w.messageSender(s, p)));
                }
            }else
                noPermMessage(p);
            if(args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    if(p.hasPermission("weight.reload")) {
                        reloadCommand();
                        if(js.successfullRead)
                            p.sendMessage(getPlugin().getPluginPrefix() + ChatColor.GREEN + "Config and weight files reloaded successfully.");
                        else
                            p.sendMessage(getPlugin().getPluginPrefix()  + ChatColor.RED + "There was an error while reloading, check the console.");
                        js.successfullRead = true;
                    }else
                        noPermMessage(p);
                }else if(arg0.equalsIgnoreCase("get")) {
                    if(p.hasPermission("weight.get")) {
                        p.sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GREEN + "You can see items weight by using the command " + ChatColor.YELLOW + "/weight get <item>.");
                    }else
                        noPermMessage(p);
                }else if(arg0.equalsIgnoreCase("set")){
                    if(p.hasPermission("weight.set")) {
                        p.sendMessage(getPlugin().getPluginPrefix() + ChatColor.GREEN + "You can set the weight value of an item using the command" + ChatColor.YELLOW + " /weight set <item> <value>.");
                    }else
                        noPermMessage(p);
                }else
                    p.sendMessage(getPlugin().getPluginPrefix()  + ChatColor.RED + "Couldn't find this command.");
            }if(args.length == 2) {
                String arg0 = args[0];
                String arg1 = args[1].toUpperCase();
                if(arg0.equalsIgnoreCase("get")) {
                    if(p.hasPermission("weight.get." + arg1)) {
                        Material item = Material.getMaterial(arg1);
                        if(globalitemsweight.get(item) != null)
                            p.sendMessage(getPlugin().getPluginPrefix()  + ChatColor.YELLOW + arg1 +ChatColor.GREEN + " weighs " + ChatColor.RED + String.format("%.1f", globalitemsweight.get(item)));
                        else
                            p.sendMessage(getPlugin().getPluginPrefix()  + ChatColor.RED + "Couldn't find " + ChatColor.YELLOW + arg1 + ChatColor.RED + " in the weight files.");
                    }
                }else if(arg0.equalsIgnoreCase("set")){
                    if(p.hasPermission("weight.set")) {
                        p.sendMessage(getPlugin().getPluginPrefix() + ChatColor.GREEN + "You can set the weight value of an item using the command" + ChatColor.YELLOW + " /weight set <item> <value>.");
                    }else
                        noPermMessage(p);
                }
            }if(args.length == 3){
                String commandSet = args[0];
                String itemName = args[1].toUpperCase();
                String weightValue = args[2];
                if(commandSet.equalsIgnoreCase("set")){
                    if(p.hasPermission("weight.set")) {

                        FileReader blocksWeightFile;
                        FileReader toolsWeightFile;
                        FileReader miscWeightFile;
                        try {
                            miscWeightFile = new FileReader(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Misc Items Weight.json");
                            toolsWeightFile = new FileReader(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Tools And Weapons Weight.json");
                            blocksWeightFile = new FileReader(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Blocks Weight.json");

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }


                        JSONObject blockWeightObject = new JSONObject(new JSONTokener(blocksWeightFile));
                        JSONObject toolsWeightObject = new JSONObject(new JSONTokener(toolsWeightFile));
                        JSONObject miscWeightObject = new JSONObject(new JSONTokener(miscWeightFile));

                        Iterator<String> keys = blockWeightObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONArray blockWeightArray = blockWeightObject.getJSONArray(key);
                            for (int i = 0; i < blockWeightArray.length(); i++) {
                                String item = blockWeightArray.getString(i);
                                String[] parts = item.split("=");
                                if (parts.length == 2 && parts[0].equalsIgnoreCase(itemName)) {
                                    blockWeightArray.put(i, itemName + "=" + weightValue);
                                    FileWriter blocksWeightWriter;
                                    try {
                                        blocksWeightWriter = new FileWriter(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Blocks Weight.json");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return writeAndCloseJsonFile(blockWeightObject, blocksWeightWriter);
                                }
                            }
                        }

                        Iterator<String> toolsKeys = toolsWeightObject.keys();
                        while (toolsKeys.hasNext()) {
                            String toolsKey = toolsKeys.next();
                            JSONArray toolsWeightArray = toolsWeightObject.getJSONArray(toolsKey);
                            for (int i = 0; i < toolsWeightArray.length(); i++) {
                                String item = toolsWeightArray.getString(i);
                                String[] parts = item.split("=");
                                if (parts.length == 2 && parts[0].equalsIgnoreCase(itemName)) {
                                    toolsWeightArray.put(i, itemName + "=" + weightValue);
                                    FileWriter toolsWeightWriter;
                                    try {
                                        toolsWeightWriter = new FileWriter(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Tools And Weapons Weight.json");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return writeAndCloseJsonFile(toolsWeightObject, toolsWeightWriter);
                                }
                            }
                        }

                        Iterator<String> miscKeys = miscWeightObject.keys();
                        while (miscKeys.hasNext()) {
                            String miscKey = miscKeys.next();
                            JSONArray miscWeightArray = miscWeightObject.getJSONArray(miscKey);
                            for (int i = 0; i < miscWeightArray.length(); i++) {
                                String item = miscWeightArray.getString(i);
                                String[] parts = item.split("=");
                                if (parts.length == 2 && parts[0].equalsIgnoreCase(itemName)) {
                                    miscWeightArray.put(i, itemName + "=" + weightValue);
                                    FileWriter miscWeightWriter;
                                    try {
                                        miscWeightWriter = new FileWriter(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Misc Items Weight.json");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return writeAndCloseJsonFile(miscWeightObject, miscWeightWriter);
                                }
                            }
                        }

                    }else
                        noPermMessage(p);
                }
            }
        }else if(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            Server c = sender.getServer();
            if (args.length == 0)
                c.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GRAY + "This command can only be executed by a player.");
            if (args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    reloadCommand();
                    if(js.successfullRead)
                        c.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GRAY + "Config and weight files reloaded successfully.");
                    else
                        c.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GRAY + "There was an error while reloading.");
                    js.successfullRead = true;
                }
            }
        }
        return false;
    }

    private boolean writeAndCloseJsonFile(JSONObject miscWeightObject, FileWriter miscWeightWriter) {
        miscWeightObject.write(miscWeightWriter);
        try {
            miscWeightWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            miscWeightWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        globalitemsweight.clear();
        customitemsweight.clear();
        js.readJsonFile();
        return false;
    }

    private void reloadCommand() {
        String separator = File.separator;
        File config = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "config.yml");
        File messages = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "messages.yml");
        File blocksWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "Weights" + separator + "Blocks Weight.json");
        File toolsWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "Weights" + separator + "Tools And Weapons Weight.json");
        File miscWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "Weights" + separator + "Misc Items Weight.json");
        customitemsweight.clear();
        globalitemsweight.clear();
        if(config.exists()) {
            getPlugin().reloadConfig();
        }else{
            getPlugin().getConfig().options().copyDefaults();
            getPlugin().saveDefaultConfig();
        }

        weightThresholdValues[0] = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
        weightThresholdValues[1] = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
        weightThresholdValues[2] = (float) getPlugin().getConfig().getDouble("weight-level-3.value");

        getPlugin().task.cancel();
        getPlugin().scheduler();
        if(messages.exists())
            Messages.reloadMessagesConfig();
        else {
            Messages.create();
        }
        if (!blocksWeight.exists() || !toolsWeight.exists() || !miscWeight.exists())
                js.saveJsonFile();
        js.readJsonFile();
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
        for (Player plist : players) {
            w.calculateWeight(plist);
        }
        getPlugin().reloadPluginPrefix();
    }

    private void noPermMessage(Player p ){

        p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");

    }

}
