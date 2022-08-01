package ted_2001.WeightRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.JsonFile;


import java.io.File;
import java.util.List;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;


public class WeightCommand implements CommandExecutor {

    private final JsonFile js = new JsonFile();
    CalculateWeight w= new CalculateWeight();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player ){
            Player p = (Player) sender;
            if(p.hasPermission("weight.use")){
                if(args.length == 0){
                    List<String> message = getPlugin().getConfig().getStringList("weight-command-message");
                    for (String s : message)
                        p.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', w.messageSender(s, p)));
                }
            }
            if(args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    if(p.hasPermission("weight.reload")) {
                        reloadcommand();
                        p.sendMessage(ChatColor.GREEN + "You successfully reload config and weight files.");
                    }
                }
            }
        }else if(sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            Server c = sender.getServer();
            if (args.length == 0)
                c.getLogger().info("This command can only execute by a player.");
            if (args.length == 1) {
                String arg0 = args[0];
                if (arg0.equalsIgnoreCase("reload")) {
                    reloadcommand();
                    c.getLogger().info("You successfully reload config and weight files.");
                }
            }
        }
        return false;
    }

    private void reloadcommand() {
        File config = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\config.yml");
        File blocksweight = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Weights\\Blocks Weight.json");
        File toolsweight = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Weights\\Tools And Weapons Weight.json");
        File miscweight = new File(getPlugin().getDataFolder().getAbsolutePath() + "\\Weights\\Misc Items Weight.json");
        if(config.exists()) {
            getPlugin().reloadConfig();
        }else{
            getPlugin().getConfig().options().copyDefaults();
            getPlugin().saveDefaultConfig();
        }
        if (!blocksweight.exists() || !toolsweight.exists() || !miscweight.exists())
                js.saveJsonFile();
        js.readJsonFile();
        List<Player> players = (List<Player>) getPlugin().getServer().getOnlinePlayers();
        for (Player plist : players) {
            w.calculateWeight(plist);
        }
    }

    private void showCommandList(Player p) {
    }
}
