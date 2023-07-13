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
                    results.add("get");
                return sortedResults(args[0]);
            }
        }else if(args.length == 2) {
            if(command.getLabel().equalsIgnoreCase("weight")){
                String arg0 = args[0];
                if(arg0.equalsIgnoreCase("get")){
                    Material[] allΙtems = Material.values();
                    for (Material item : allΙtems) {
                        String tempitem = item.toString();
                        if (sender.hasPermission("weight.get." + tempitem))
                            results.add(tempitem);
                    }
                    return sortedResults(args[1]);
                }else if(arg0.equalsIgnoreCase("set")){
                    if(sender.hasPermission("weight.set")){
                        Material[] allItems = Material.values();
                        for (Material item : allItems) {
                            String tempitem = item.toString();
                            results.add(tempitem);
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
