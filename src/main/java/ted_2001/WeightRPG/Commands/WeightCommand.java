package ted_2001.WeightRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;


import java.io.File;
import java.util.List;

import static ted_2001.WeightRPG.Utils.JsonFile.customitemsweight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalitemsweight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;
import static ted_2001.WeightRPG.Utils.CalculateWeight.weightThresholdValues;


public class WeightCommand implements CommandExecutor {

    private final JsonFile js = new JsonFile();
    CalculateWeight w= new CalculateWeight();
    private final String pluginPrefix = ChatColor.GRAY + "[" + ChatColor.YELLOW + "Weight-RPG" +ChatColor.GRAY + "] ";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player ){
            Player p = (Player) sender;
            if(p.hasPermission("weight.use")){
                if(args.length == 0){
                    List<String> disabledworlds = getPlugin().getConfig().getStringList("disabled-worlds");
                    for (String disabledworld : disabledworlds) {
                        if (disabledworld.equalsIgnoreCase((p.getWorld().getName()))) {
                            p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED +"is disabled in this world.");
                            return false;
                        }
                    }
                    String PlayerGamemode = p.getGameMode().toString();
                    if(PlayerGamemode.equalsIgnoreCase("CREATIVE") ) {
                        p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED +"is disabled on "+ ChatColor.GREEN + "Creative Mode.");
                         return false;
                    }
                    if(PlayerGamemode.equalsIgnoreCase("SPECTATOR") ) {
                        p.sendMessage(ChatColor.YELLOW + "Weight-RPG " + ChatColor.RED +"is disabled on "+ ChatColor.GREEN + "Spectator Mode.");
                        return false;
                    }
                    List<String> message = Messages.getMessages().getStringList("weight-command-message");
                    for (String s : message)
                        p.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', w.messageSender(s, p)));
                }
            }else
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            if(args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    if(p.hasPermission("weight.reload")) {
                        reloadCommand();
                        if(js.successfullRead)
                            p.sendMessage(pluginPrefix + ChatColor.GREEN + "Config and weight files reloaded succefully.");
                        else
                            p.sendMessage(pluginPrefix + ChatColor.RED + "There was an error while reloading, check the console.");
                        js.successfullRead = true;
                    }else
                        p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                }else if(arg0.equalsIgnoreCase("get")) {
                    if(p.hasPermission("weight.get")) {
                        p.sendMessage(pluginPrefix + ChatColor.GREEN + "You can see items weight by using the command /weight get <item>.");
                    }else
                        p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                }else
                    p.sendMessage(pluginPrefix + ChatColor.RED + "Couldn't find this command.");
            }if(args.length == 2) {
                String arg0 = args[0];
                String arg1 = args[1].toUpperCase();
                if(arg0.equalsIgnoreCase("get")) {
                    if(p.hasPermission("weight.get." + arg1)) {
                        Material item = Material.getMaterial(arg1);
                        if(globalitemsweight.get(item) != null)
                            p.sendMessage(pluginPrefix + ChatColor.YELLOW + arg1 +ChatColor.GREEN + " weighs " + ChatColor.RED + String.format("%.1f", globalitemsweight.get(item)));
                        else
                            p.sendMessage(pluginPrefix + ChatColor.RED + "Couldn't find " + ChatColor.YELLOW + arg1 + ChatColor.RED + " in the weight files.");
                    }
                }
            }
        }else if(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            Server c = sender.getServer();
            if (args.length == 0)
                c.getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "This command can only be executed by a player.");
            if (args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    reloadCommand();
                    if(js.successfullRead)
                        c.getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "Config and weight files reloaded succefully.");
                    else
                        c.getConsoleSender().sendMessage(pluginPrefix + ChatColor.GRAY + "There was an error while reloading.");
                    js.successfullRead = true;
                }
            }
        }
        return false;
    }

    private void reloadCommand() {
        File config = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\config.yml");
        File messages = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\messages.yml");
        File blocksWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Weights\\Blocks Weight.json");
        File toolsWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Weights\\Tools And Weapons Weight.json");
        File miscWeight = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Weights\\Misc Items Weight.json");
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
    }

}
