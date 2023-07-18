package ted_2001.WeightRPG.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Tabcompleter is a custom TabCompleter implementation for the "weight" command. It provides tab-completion
// suggestions for the weight command based on the sender's permissions and the command arguments.
public class Tabcompleter implements TabCompleter {

    // A list to store the tab-completion results.
    List<String> results = new ArrayList<>();

    // Called when the sender (player or console) is attempting to tab-complete the "weight" command.
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length ==1){
            if(command.getLabel().equalsIgnoreCase("weight")){

                // Check sender's permissions and add relevant subcommands to the tab-completion results.
                if(sender.hasPermission("weight.reload"))
                    results.add("reload");
                if(sender.hasPermission("weight.get"))
                    results.add("get");
                if(sender.hasPermission("weight.set"))
                    results.add("set");
                if(sender.hasPermission("weight.add"))
                    results.add("add");

                // Return the sorted tab-completion results based on the user's input argument.
                return sortedResults(args[0]);
            }
        }
        else if(args.length == 2) {
            if(command.getLabel().equalsIgnoreCase("weight")){

                String arg0 = args[0];

                if(arg0.equalsIgnoreCase("set") || arg0.equalsIgnoreCase("add") || arg0.equalsIgnoreCase("get")){
                    // Check sender's permissions and add possible item names to the tab-completion results.
                    if(sender.hasPermission("weight.set") || sender.hasPermission("weight.add") || sender.hasPermission("weight.get")){
                        Material[] allItems = Material.values();
                        for (Material item : allItems) {
                            String tempItem = item.toString();
                            results.add(tempItem);
                        }

                    // Return the sorted tab-completion results based on the user's input argument.
                    return sortedResults(args[1]);
                    }
                }
            }
        }
        // If the conditions above are not met, return null to provide no tab-completion results.
        return null;

    }

    // Sorts and returns the tab-completion results based on the given argument.
    public List < String > sortedResults(String arg) {
        // Create a new list to store the sorted tab-completions.
        final List<String> completions = new ArrayList<>();

        // Copy partial matches of the user input argument from the original results list to the completions list.
        StringUtil.copyPartialMatches(arg, results, completions);

        // Sort the completions list alphabetically.
        Collections.sort(completions);

        // Clear the original results list and add the sorted completions back to it.
        results.clear();
        for (String s : completions)
            results.add(s);

        // Return the sorted tab-completions.
        return results;
    }


}
