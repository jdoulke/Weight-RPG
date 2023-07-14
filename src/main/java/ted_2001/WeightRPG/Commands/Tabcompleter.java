package ted_2001.WeightRPG.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tabcompleter implements TabCompleter {

    List<String> results = new ArrayList<>();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length ==1){
            if(command.getLabel().equalsIgnoreCase("weight")){
                if(sender.hasPermission("weight.reload"))
                    results.add("reload");
                if(sender.hasPermission("weight.get"))
                    results.add("get");
                if(sender.hasPermission("weight.set"))
                    results.add("set");
                if(sender.hasPermission("weight.add"))
                    results.add("add");
                return sortedResults(args[0]);
            }
        }else if(args.length == 2) {
            if(command.getLabel().equalsIgnoreCase("weight")){
                String arg0 = args[0];
                if(arg0.equalsIgnoreCase("get")){
                    Material[] allItems = Material.values();
                    for (Material item : allItems) {
                        String tempItem = item.toString();
                        if (sender.hasPermission("weight.get." + tempItem))
                            results.add(tempItem);
                    }
                    return sortedResults(args[1]);
                }else if(arg0.equalsIgnoreCase("set") || arg0.equalsIgnoreCase("add")){
                    if(sender.hasPermission("weight.set") || sender.hasPermission("weight.add")){
                        Material[] allItems = Material.values();
                        for (Material item : allItems) {
                            String tempItem = item.toString();
                            results.add(tempItem);
                        }
                    return sortedResults(args[1]);
                    }
                }
            }
        }
        return null;

    }

    public List < String > sortedResults(String arg) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, results, completions);
        Collections.sort(completions);
        results.clear();
        for (String s : completions) {
            results.add(s);
        }
        return results;
    }


}
