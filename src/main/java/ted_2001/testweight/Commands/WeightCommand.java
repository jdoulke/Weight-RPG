package ted_2001.testweight.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static org.bukkit.Bukkit.getServer;
import static ted_2001.testweight.Testweight.getPlugin;


public class WeightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player ){
            Player p = (Player) sender;
            if(p.hasPermission("weight.use")){
                if(args.length == 0){
                    showCommandList(p);
                }
                if(args.length == 1){
                    String arg0 = args[0];
                    if(arg0.equalsIgnoreCase("reloadconfig")){
                        getPlugin().reloadConfig();
                        p.sendMessage(ChatColor.GREEN + "You successfully reload config file.");
                    }
                }
            }else
                p.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
        }
        return false;
    }

    private void showCommandList(Player p) {
    }
}
