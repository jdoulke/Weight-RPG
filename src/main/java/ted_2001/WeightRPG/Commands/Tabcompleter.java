package ted_2001.WeightRPG.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Tabcompleter implements TabCompleter {

    private static final List<String> MATERIAL_NAMES = Arrays.stream(Material.values())
            .map(Material::name)
            .sorted()
            .toList();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getLabel().equalsIgnoreCase("weight")) {
            return null;
        }

        if (args.length == 1) {
            List<String> candidates = new ArrayList<>(7);
            if (sender.hasPermission("weight.reload")) candidates.add("reload");
            if (sender.hasPermission("weight.get")) candidates.add("get");
            if (sender.hasPermission("weight.set")) candidates.add("set");
            if (sender.hasPermission("weight.add")) candidates.add("add");
            if (sender.hasPermission("weight.custom")) candidates.add("custom");
            if (sender.hasPermission("weight.boost")) candidates.add("boost");
            if (sender.hasPermission("weight.help")) candidates.add("help");
            return sortedResults(args[0], candidates);
        }

        if (args.length == 2) {
            String subCommand = args[0];

            if (subCommand.equalsIgnoreCase("set") && sender.hasPermission("weight.set")) {
                return sortedResults(args[1], MATERIAL_NAMES);
            }
            if (subCommand.equalsIgnoreCase("add") && sender.hasPermission("weight.add")) {
                return sortedResults(args[1], MATERIAL_NAMES);
            }
            if (subCommand.equalsIgnoreCase("get") && sender.hasPermission("weight.get")) {
                return sortedResults(args[1], MATERIAL_NAMES);
            }
            if (subCommand.equalsIgnoreCase("custom") && sender.hasPermission("weight.custom")) {
                return sortedResults(args[1], List.of("add"));
            }
            if (subCommand.equalsIgnoreCase("boost") && sender.hasPermission("weight.boost")) {
                return sortedResults(args[1], List.of("add"));
            }
        }

        return null;
    }

    private List<String> sortedResults(String argument, List<String> candidates) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(argument, candidates, completions);
        Collections.sort(completions);
        return completions;
    }
}
