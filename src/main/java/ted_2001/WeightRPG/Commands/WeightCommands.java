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
import ted_2001.WeightRPG.Utils.ColorUtils;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;


import java.io.*;
import java.util.Iterator;
import java.util.List;

import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;
import static ted_2001.WeightRPG.Utils.CalculateWeight.weightThresholdValues;


public class WeightCommands implements CommandExecutor {

    private final JsonFile js = new JsonFile();
    CalculateWeight w = new CalculateWeight();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(sender instanceof Player ){

            Player p = (Player) sender;

            // Check if any arguments were provided with the command.
            if(args.length == 0){

                if(p.hasPermission("weight.use")){

                    // Check if the world the player is currently in is disabled for the Weight-RPG plugin.
                    List<String> disabledWorlds = getPlugin().getConfig().getStringList("disabled-worlds");
                    for (String disabledWorld : disabledWorlds) {
                        if (disabledWorld.equalsIgnoreCase((p.getWorld().getName()))) {
                            // The world is disabled for the Weight-RPG plugin inform the player.
                            String disableWordMessage = w.formatMessage(Messages.getMessages().getString("disable-world-message", getPlugin().getPluginPrefix() + "&eWeight-RPG &cis disabled in this world."), p);
                            p.sendMessage(ColorUtils.translateColorCodes(disableWordMessage));
                            return false;
                        }
                    }

                    // Check the player's current game mode.
                    String PlayerGamemode = p.getGameMode().toString();
                    if(PlayerGamemode.equalsIgnoreCase("CREATIVE") ) {
                        // The player is in Creative mode inform the player.
                        String creativeMessage = Messages.getMessages().getString("weight-command-creative-message");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.formatMessage(creativeMessage, p)));
                         return false;
                    }

                    if(PlayerGamemode.equalsIgnoreCase("SPECTATOR") ) {
                        // The player is in Creative mode inform the player.
                        String spectatorMessage = Messages.getMessages().getString("weight-command-spectator-message");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.formatMessage(spectatorMessage, p)));
                        return false;
                    }

                    if(p.hasPermission("weight.bypass")){
                        // The player has the "weight.bypass" permission inform the player.
                        String bypassMessage = Messages.getMessages().getString("weight-command-bypass-message");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.formatMessage(bypassMessage, p)));
                        return false;
                    }

                    // The player has the "weight.use" permission and none of the special cases apply.
                    // Send the default Weight-RPG command messages to the player.
                    List<String> message = Messages.getMessages().getStringList("weight-command-message");
                    for (String s : message)
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.formatMessage(s, p)));

                }else
                    // The player does not have the "weight.use" permission. Send a message indicating no permission.
                    noPermMessage(p);
            }

            // Handles the "args.length == 1" scenario where there is only one argument in the command.
            // This block checks the value of the first argument and performs the corresponding action.
            if(args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    // The first argument is "reload".
                    // Check if the player has the "weight.reload" permission to execute the reload command.
                    if(p.hasPermission("weight.reload")) {
                        // Player has permission. Perform the reload.
                        reloadCommand();

                        // Check if the config and weight files were reloaded successfully.
                        if (js.successfullyRead) {
                            String successMessage = w.formatMessage(Messages.getMessages().getString("success-reload-message", getPlugin().getPluginPrefix() + "&aConfig and weight files reloaded successfully."), p);
                            p.sendMessage(ColorUtils.translateColorCodes(successMessage));
                        } else {
                            String errorMessage = w.formatMessage(Messages.getMessages().getString("fail-reload-message", getPlugin().getPluginPrefix() + "&cThere was an error while reloading, check the console."), p);
                            p.sendMessage(ColorUtils.translateColorCodes(errorMessage));
                        }

                        // Reset the successfullyRead flag for future reload attempts.
                        js.successfullyRead = true;
                    }else
                        // Player does not have permission to execute the reload command. Send him a no permission message.
                        noPermMessage(p);

                }else if(arg0.equalsIgnoreCase("get")) {

                    // The first argument is "get".
                    // Check if the player has the "weight.get" permission to execute the get command.
                    if (p.hasPermission("weight.get")) {
                        // Player has permission. Inform the player how to use the "/weight get" command.
                        String getMessage = w.formatMessage(Messages.getMessages().getString("get-command-message", getPlugin().getPluginPrefix() + "&aYou can see items weight by using the command &e/weight get <item>."), p);
                        p.sendMessage(ColorUtils.translateColorCodes(getMessage));
                    } else
                        // Player does not have permission to execute the get command. Send him a no permission message.
                        noPermMessage(p);

                }else if(arg0.equalsIgnoreCase("set")){

                    // The first argument is "set".
                    // Check if the player has the "weight.set" permission to execute the set command.
                    if (p.hasPermission("weight.set")) {
                        // Player has permission. Inform the player how to use the "/weight set" command.
                        String setMessage = w.formatMessage(Messages.getMessages().getString("set-command-message", getPlugin().getPluginPrefix() + "&aYou can set the weight value of an item using the command &e/weight set <item> <value>."), p);
                        p.sendMessage(ColorUtils.translateColorCodes(setMessage));
                    } else
                        // Player does not have permission to execute the set command. Send him a no permission message.
                        noPermMessage(p);

                }else if(arg0.equalsIgnoreCase("add")){
                    // The first argument is "add".
                    // Check if the player has the "weight.add" permission to execute the add command.
                    if (p.hasPermission("weight.add")) {
                        // Player has permission. Inform the player how to use the "/weight add" command.
                        String addMessage = w.formatMessage(Messages.getMessages().getString("add-command-message", getPlugin().getPluginPrefix() + "&aYou can add an item on the weight files using the command &e/weight add <item> <value>." +
                                "&aYou will find the record on the Misc Items Weight file under Additional Items section."), p);
                        p.sendMessage(ColorUtils.translateColorCodes(addMessage));
                    } else
                        // Player does not have permission to execute the add command. Send him a no permission message.
                        noPermMessage(p);
                } else if (arg0.equalsIgnoreCase("help")) {
                    // The first argument is "help".
                    // Check if the player has the "weight.help" permission to execute the help command.
                    if (p.hasPermission("weight.help")) {
                        // Player has permission. Show the message from messages.yml file.
                        List<String> message = Messages.getMessages().getStringList("help-command-message");
                        for (String s : message)
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', w.formatMessage(s, p)));
                    } else
                        // Player does not have permission to execute the add command. Send him a no permission message.
                        noPermMessage(p);
                } else {
                    // The first argument is none of the recognized commands.
                    unknownCommandMessage(p);
                }
            }

            // Handles the "args.length == 2" scenario where there are two arguments in the command.
            // This block checks the value of the first and second arguments and performs the corresponding action.
            if(args.length == 2) {

                String arg0 = args[0];
                String arg1 = args[1].toUpperCase();

                // Command: /weight get <item>
                if(arg0.equalsIgnoreCase("get")) {
                    // Check if the player has the permission to execute the "get" command.
                    if(p.hasPermission("weight.get")) {
                        // Convert the specified item name to a Material object.
                        Material item = Material.getMaterial(arg1);

                        if (item != null && globalItemsWeight.containsKey(item)) {
                            // The weight of the specified item is found in the weight files. Send the weight information to the player.
                            String getMessage = w.formatMessage(Messages.getMessages().getString("get-item-success-message", getPlugin().getPluginPrefix() + "&aThe weight of &e" + arg1 + " &ais &e" + String.format("%.2f", globalItemsWeight.get(item))), p);
                            getMessage = getMessage.replaceAll("%item%", item.toString());
                            getMessage = getMessage.replaceAll("%itemweight%", String.format("%.2f", globalItemsWeight.getOrDefault(item, 0f)));
                            p.sendMessage(ColorUtils.translateColorCodes(getMessage));
                        } else {
                            // The specified item is not found in the weight files. Inform the player about it.
                            String getMessage = w.formatMessage(Messages.getMessages().getString("get-item-fail-message", getPlugin().getPluginPrefix() + "&cCouldn't find &e" + arg1 + " &cin the weight files."), p);
                            getMessage = getMessage.replaceAll("%item%", arg1);
                            p.sendMessage(ColorUtils.translateColorCodes(getMessage));
                        }
                    }else
                        noPermMessage(p);
                }
                // Command: /weight set <item> <value>
                else if(arg0.equalsIgnoreCase("set")){
                    // Check if the player has the "weight.set" permission to execute the set command.
                    if (p.hasPermission("weight.set")) {
                        // Player has permission. Inform the player how to use the "/weight set" command.
                        String getMessage = w.formatMessage(Messages.getMessages().getString("set-command-message", getPlugin().getPluginPrefix() + "&aYou can set the weight value of an item using the command &e/weight set <item> <value>."), p);
                        p.sendMessage(ColorUtils.translateColorCodes(getMessage));
                    } else
                        noPermMessage(p);
                }
                // Command: /weight add <item> <value>
                else if(arg0.equalsIgnoreCase("add")){
                    // Check if the player has the "weight.add" permission to execute the add command.
                    if (p.hasPermission("weight.add")) {
                        // Player has permission. Inform the player how to use the "/weight add" command.
                        String addMessage = w.formatMessage(Messages.getMessages().getString("add-command-message", getPlugin().getPluginPrefix() + "&aYou can add an item on the weight files using the command &e/weight add <item> <value>." +
                                "&aYou will find the record on the Misc Items Weight file under Additional Items section."), p);
                        p.sendMessage(ColorUtils.translateColorCodes(addMessage));
                    } else
                        noPermMessage(p);
                }else
                    // The first argument is none of the recognized commands.
                    unknownCommandMessage(p);
            }

            // Handles the "args.length == 3" scenario where there are three arguments in the command.
            // This block checks the value of the first argument (weightCommand) and performs the corresponding action,
            if(args.length == 3){

                String weightCommand = args[0];
                String itemName = args[1].toUpperCase();
                String weightValue = args[2];

                // Command: /weight set <item> <value>
                if(weightCommand.equalsIgnoreCase("set")){
                    if(p.hasPermission("weight.set")) {

                        // FileReader instances to read weight files.
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

                        // JSON objects to store weight data for blocks, tools/weapons, and miscellaneous items.
                        JSONObject blockWeightObject = new JSONObject(new JSONTokener(blocksWeightFile));
                        JSONObject toolsWeightObject = new JSONObject(new JSONTokener(toolsWeightFile));
                        JSONObject miscWeightObject = new JSONObject(new JSONTokener(miscWeightFile));

                        // Iterate through the blockWeightObject to find the specified item and update its weight value.
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
                                    // Send a success message to the player after updating the weight value.
                                    String setMessage = w.formatMessage(Messages.getMessages().getString("set-item-success-message", getPlugin().getPluginPrefix() + "&aYou successfully set the weight of &e" + itemName + " &ato &e" + weightValue + " &a."), p);
                                    setMessage = setMessage.replaceAll("%item%", itemName);
                                    setMessage = setMessage.replaceAll("%itemweight%", weightValue);
                                    p.sendMessage(ColorUtils.translateColorCodes(setMessage));
                                    return writeAndCloseJsonFile(blockWeightObject, blocksWeightWriter);
                                }
                            }
                        }

                        // Iterate through the toolsWeightObject to find the specified item and update its weight value.
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
                                    String setMessage = w.formatMessage(Messages.getMessages().getString("set-item-success-message", getPlugin().getPluginPrefix() + "&aYou successfully set the weight of &e" + itemName + " &ato &e" + weightValue + " &a."), p);
                                    setMessage = setMessage.replaceAll("%item%", itemName);
                                    setMessage = setMessage.replaceAll("%itemweight%", weightValue);
                                    p.sendMessage(ColorUtils.translateColorCodes(setMessage));
                                    return writeAndCloseJsonFile(toolsWeightObject, toolsWeightWriter);
                                }
                            }
                        }

                        // Iterate through the miscWeightObject to find the specified item and update its weight value.
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
                                    String setMessage = w.formatMessage(Messages.getMessages().getString("set-item-success-message", getPlugin().getPluginPrefix() + "&aYou successfully set the weight of &e" + itemName + " &ato &e" + weightValue + " &a."), p);
                                    setMessage = setMessage.replaceAll("%item%", itemName);
                                    setMessage = setMessage.replaceAll("%itemweight%", weightValue);
                                    p.sendMessage(ColorUtils.translateColorCodes(setMessage));
                                    return writeAndCloseJsonFile(miscWeightObject, miscWeightWriter);
                                }
                            }
                        }
                        // If the specified item is not found in any of the weight files, inform the player about it.
                        String setMessage = w.formatMessage(Messages.getMessages().getString("set-item-fail-message", getPlugin().getPluginPrefix() + "&cCouldn't find the &e" + itemName + " &cin any of the weight files."), p);
                        setMessage = setMessage.replaceAll("%item%", itemName);
                        p.sendMessage(ColorUtils.translateColorCodes(setMessage));

                    }else
                        noPermMessage(p);
                }

                else if(weightCommand.equalsIgnoreCase("add")) {
                    if (p.hasPermission("weight.add")) {

                        FileReader miscWeightFileReader;
                        FileWriter miscWeightFileWriter;

                        // Check if the specified item already exists in the globalItemsWeight map which means is already on weight files.
                        if(globalItemsWeight.get(Material.getMaterial(itemName)) != null) {
                            String addMessage = w.formatMessage(Messages.getMessages().getString("add-item-found-message", getPlugin().getPluginPrefix() + "&cThis item already exists in the weight files and it's weight value is &e " + globalItemsWeight.get(Material.getMaterial(itemName)) + "&c."), p);
                            addMessage = addMessage.replaceAll("%itemweight%", String.valueOf(globalItemsWeight.get(Material.getMaterial(itemName))));
                            p.sendMessage(ColorUtils.translateColorCodes(addMessage));
                            return false;
                        }

                        // Read the "Misc Items Weight" JSON file.
                        try {
                            miscWeightFileReader = new FileReader(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Misc Items Weight.json");
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        JSONObject miscWeightObject = new JSONObject(new JSONTokener(miscWeightFileReader));
                        JSONArray addedItems;

                        // Check if the "Additional Items" key exists in the JSON object.
                        // If yes, append the new item and weight value to the existing JSONArray.
                        // If not, create a new JSONArray and add the item and weight value to it.
                        if (miscWeightObject.has("Additional Items")) {
                            addedItems = miscWeightObject.getJSONArray("Additional Items");
                            addedItems.put(itemName + "=" + weightValue);
                        } else {
                            addedItems = new JSONArray();
                            addedItems.put(itemName + "=" + weightValue);
                            miscWeightObject.put("Additional Items", addedItems);
                        }

                        // Write the updated JSON object back to the "Misc Items Weight" file.
                        try {
                            miscWeightFileWriter = new FileWriter(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "Weights" + File.separator + "Misc Items Weight.json");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        writeAndCloseJsonFile(miscWeightObject, miscWeightFileWriter);

                        // Send a success message to the player after adding the new item and weight value.
                        String addMessage = w.formatMessage(Messages.getMessages().getString("add-item-success-message", getPlugin().getPluginPrefix() + "&aYou successfully added the weight of &e" + itemName + " &ato &b" + weightValue + " &a."), p);
                        addMessage = addMessage.replaceAll("%item%", itemName);
                        addMessage = addMessage.replaceAll("%itemweight%", String.valueOf(globalItemsWeight.get(Material.getMaterial(itemName))));
                        p.sendMessage(ColorUtils.translateColorCodes(addMessage));

                        return false;

                    }else
                        noPermMessage(p);
                }else
                    unknownCommandMessage(p);
            }

            // Handles the case when the number of arguments (args.length) is greater than 3, which indicates an invalid command format.
            // Sends an error message to the player notifying them that the command could not be found due to the incorrect format.
            if(args.length > 3)
                unknownCommandMessage(p);
        }

        // Handles the case when the sender of the command is the console or a remote console.
        // The console and remote console can only execute specific commands for reloading.
        else if(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {

            Server server = sender.getServer();

            // If no arguments are provided, notify the console that this command can only be executed by a player.
            if (args.length == 0)
                server.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GRAY + "This command can only be executed by a player.");

            // If one argument is provided, check if it's a "reload" command and handle the reload process.
            if (args.length == 1) {

                String arg0 = args[0];

                if (arg0.equalsIgnoreCase("reload")) {

                    reloadCommand();
                    // Notify the console about the success or failure of the reload process.
                    if(js.successfullyRead)
                        server.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GRAY + "Config and weight files reloaded successfully.");
                    else
                        server.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()  + ChatColor.GRAY + "There was an error while reloading.");
                    js.successfullyRead = true;

                }
            }
        }
        return false;
    }

    private void unknownCommandMessage(Player p) {
        String unknownMessage = w.formatMessage(Messages.getMessages().getString("unknown-command", getPlugin().getPluginPrefix() + "&a&cCouldn't find this command."), p);
        p.sendMessage(ColorUtils.translateColorCodes(unknownMessage));
    }

    // Writes the provided JSON object to the specified file writer, formats the JSON content with indentation,
    // and closes the file writer.
    private boolean writeAndCloseJsonFile(JSONObject jsonObject, FileWriter fileWriter) {

        PrintWriter output = new PrintWriter(fileWriter);
        output.write(jsonObject.toString(2));

        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Clear the stored global and custom items weights.
        globalItemsWeight.clear();
        customItemsWeight.clear();

        // Read the JSON file again to update the weights after the changes.
        js.readJsonFile();

        return false;
    }

    // Reloads the configuration files and weight files of the plugin, updates weight threshold values, and performs
    // necessary updates and calculations after a plugin reload.
    private void reloadCommand() {

        String separator = File.separator;

        // Get references to the configuration and messages files.
        File config = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "config.yml");
        File messages = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "messages.yml");

        // Get references to the weight files for blocks, tools and weapons, and miscellaneous items.
        File blocksWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "Weights" + separator + "Blocks Weight.json");
        File toolsWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "Weights" + separator + "Tools And Weapons Weight.json");
        File miscWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + separator + "Weights" + separator + "Misc Items Weight.json");

        // Clear the stored custom and global item weights.
        customItemsWeight.clear();
        globalItemsWeight.clear();

        // If the config file exists, reload it. Otherwise, create a new one with default values.
        if(config.exists())
            getPlugin().reloadConfig();
        else {
            getPlugin().getConfig().options().copyDefaults();
            getPlugin().saveDefaultConfig();
        }

        // If the messages file exists, reload it. Otherwise, create a new one with default messages.
        if(messages.exists())
            Messages.reloadMessagesConfig();
        else
            Messages.create();

        // Update the weight threshold values from the plugin's config file.
        weightThresholdValues[0] = (float) getPlugin().getConfig().getDouble("weight-level-1.value");
        weightThresholdValues[1] = (float) getPlugin().getConfig().getDouble("weight-level-2.value");
        weightThresholdValues[2] = (float) getPlugin().getConfig().getDouble("weight-level-3.value");

        // Cancel the existing weight calculation task and schedule a new one.
        getPlugin().task.cancel();
        getPlugin().scheduler();

        // If any of the weight files (blocks, tools, or misc) do not exist, save the default weight file.
        if (!blocksWeight.exists() || !toolsWeight.exists() || !miscWeight.exists())
                js.saveJsonFile();

        // Read the JSON weight files to update the weight values.
        js.readJsonFile();

        // Calculate the weight for all online players after the update.
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
        for (Player plist : players) {
            w.calculateWeight(plist);
        }

        // Reload the plugin prefix for any potential changes in the configuration.
        getPlugin().reloadPluginPrefix();
    }

    // Sends a "no permission" message to the specified player when they attempt to use a command without the necessary permission.
    private void noPermMessage(Player p ){
        String noPermissionMessage = Messages.getMessages().getString("no-permission-message", getPlugin().getPluginPrefix() +
                "&cYou do not have permission to use this command.");
        p.sendMessage(ColorUtils.translateColorCodes((w.formatMessage(noPermissionMessage, p))));
    }

}
