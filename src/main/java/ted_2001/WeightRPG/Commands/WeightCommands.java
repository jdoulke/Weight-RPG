package ted_2001.WeightRPG.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.ColorUtils;
import ted_2001.WeightRPG.Utils.ItemLoreUtils;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

public class WeightCommands implements CommandExecutor {

    private final JsonFile jsonFile = new JsonFile();
    private final CalculateWeight weightCalculator = new CalculateWeight();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            return handlePlayerCommand(player, args);
        }

        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            return handleConsoleCommand(sender.getServer(), args);
        }

        return true;
    }

    private boolean handlePlayerCommand(Player player, String[] args) {
        if (args.length == 0) {
            showWeight(player);
            return true;
        }

        if (args.length == 1) {
            handleSingleArgument(player, args[0]);
            return true;
        }

        if (args.length == 2) {
            handleTwoArguments(player, args[0], args[1]);
            return true;
        }

        if (args.length == 3) {
            handleThreeArguments(player, args[0], args[1], args[2]);
            return true;
        }

        unknownCommandMessage(player);
        return true;
    }

    private void showWeight(Player player) {
        if (!player.hasPermission("weight.use")) {
            noPermMessage(player);
            return;
        }

        for (String disabledWorld : getPlugin().getConfig().getStringList("disabled-worlds")) {
            if (disabledWorld.equalsIgnoreCase(player.getWorld().getName())) {
                send(player, Messages.getMessages().getString("disable-world-message",
                        getPlugin().getPluginPrefix() + "&eWeight-RPG &cis disabled in this world."));
                return;
            }
        }

        switch (player.getGameMode()) {
            case CREATIVE -> {
                send(player, Messages.getMessages().getString("weight-command-creative-message", ""));
                return;
            }
            case SPECTATOR -> {
                send(player, Messages.getMessages().getString("weight-command-spectator-message", ""));
                return;
            }
            default -> {
            }
        }

        if (player.hasPermission("weight.bypass")) {
            send(player, Messages.getMessages().getString("weight-command-bypass-message", ""));
            return;
        }

        for (String line : Messages.getMessages().getStringList("weight-command-message")) {
            player.sendMessage(ColorUtils.translateColorCodes(weightCalculator.formatMessage(line, player)));
        }
    }

    private void handleSingleArgument(Player player, String subCommand) {
        if (subCommand.equalsIgnoreCase("reload")) {
            if (!player.hasPermission("weight.reload")) {
                noPermMessage(player);
                return;
            }

            boolean success = reloadCommand();
            String path = success ? "success-reload-message" : "fail-reload-message";
            String fallback = success
                    ? getPlugin().getPluginPrefix() + "&aConfig and weight files reloaded successfully."
                    : getPlugin().getPluginPrefix() + "&cThere was an error while reloading, check the console.";
            send(player, Messages.getMessages().getString(path, fallback));
            return;
        }

        if (subCommand.equalsIgnoreCase("get")) {
            commandHelp(player, "weight.get", "get-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can see items weight by using the command &e/weight get <item>.");
        } else if (subCommand.equalsIgnoreCase("set")) {
            commandHelp(player, "weight.set", "set-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can set the weight value of an item using the command &e/weight set <item> <value>.");
        } else if (subCommand.equalsIgnoreCase("add")) {
            commandHelp(player, "weight.add", "add-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can add an item on the weight files using the command &e/weight add <item> <value>.");
        } else if (subCommand.equalsIgnoreCase("custom")) {
            commandHelp(player, "weight.custom", "custom-add-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can add a custom weight to the item in your hand using &e/weight custom add <value>.");
        } else if (subCommand.equalsIgnoreCase("boost")) {
            commandHelp(player, "weight.boost", "boost-add-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can add a boost weight to the item in your hand using &e/weight boost add <value>.");
        } else if (subCommand.equalsIgnoreCase("help")) {
            if (!player.hasPermission("weight.help")) {
                noPermMessage(player);
                return;
            }
            for (String line : Messages.getMessages().getStringList("help-command-message")) {
                player.sendMessage(ColorUtils.translateColorCodes(weightCalculator.formatMessage(line, player)));
            }
        } else {
            unknownCommandMessage(player);
        }
    }

    private void handleTwoArguments(Player player, String subCommand, String argument) {
        if (subCommand.equalsIgnoreCase("get")) {
            if (!player.hasPermission("weight.get")) {
                noPermMessage(player);
                return;
            }
            showItemWeight(player, argument);
            return;
        }

        if (subCommand.equalsIgnoreCase("set")) {
            commandHelp(player, "weight.set", "set-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can set the weight value of an item using the command &e/weight set <item> <value>.");
            return;
        }

        if (subCommand.equalsIgnoreCase("add")) {
            commandHelp(player, "weight.add", "add-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can add an item on the weight files using the command &e/weight add <item> <value>.");
            return;
        }

        if (subCommand.equalsIgnoreCase("custom") && argument.equalsIgnoreCase("add")) {
            commandHelp(player, "weight.custom", "custom-add-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can add a custom weight to the item in your hand using &e/weight custom add <value>.");
            return;
        }

        if (subCommand.equalsIgnoreCase("boost") && argument.equalsIgnoreCase("add")) {
            commandHelp(player, "weight.boost", "boost-add-command-message",
                    getPlugin().getPluginPrefix() + "&aYou can add a boost weight to the item in your hand using &e/weight boost add <value>.");
            return;
        }

        unknownCommandMessage(player);
    }

    private void handleThreeArguments(Player player, String subCommand, String argument, String rawValue) {
        if (subCommand.equalsIgnoreCase("set")) {
            if (!player.hasPermission("weight.set")) {
                noPermMessage(player);
                return;
            }
            setItemWeight(player, argument, rawValue);
            return;
        }

        if (subCommand.equalsIgnoreCase("add")) {
            if (!player.hasPermission("weight.add")) {
                noPermMessage(player);
                return;
            }
            addItemWeight(player, argument, rawValue);
            return;
        }

        if (subCommand.equalsIgnoreCase("custom") && argument.equalsIgnoreCase("add")) {
            if (!player.hasPermission("weight.custom")) {
                noPermMessage(player);
                return;
            }
            setHeldItemPersistentWeight(player, rawValue, false);
            return;
        }

        if (subCommand.equalsIgnoreCase("boost") && argument.equalsIgnoreCase("add")) {
            if (!player.hasPermission("weight.boost")) {
                noPermMessage(player);
                return;
            }
            setHeldItemPersistentWeight(player, rawValue, true);
            return;
        }

        unknownCommandMessage(player);
    }

    private void showItemWeight(Player player, String rawItemName) {
        String itemName = rawItemName.toUpperCase(Locale.ROOT);
        Material material = Material.getMaterial(itemName);

        if (material != null && globalItemsWeight.containsKey(material)) {
            String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                    "get-item-success-message",
                    getPlugin().getPluginPrefix() + "&aThe weight of &e" + itemName + " &ais &e"
                            + String.format("%.2f", globalItemsWeight.get(material))), player);
            message = message.replace("%item%", material.toString())
                    .replace("%itemweight%", String.format("%.2f", globalItemsWeight.get(material)));
            player.sendMessage(ColorUtils.translateColorCodes(message));
            return;
        }

        String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                "get-item-fail-message",
                getPlugin().getPluginPrefix() + "&cCouldn't find &e" + itemName + " &cin the weight files."), player);
        player.sendMessage(ColorUtils.translateColorCodes(message.replace("%item%", itemName)));
    }

    private void setItemWeight(Player player, String rawItemName, String rawValue) {
        String itemName = rawItemName.toUpperCase(Locale.ROOT);
        Material material = Material.getMaterial(itemName);
        Float value = parseFiniteFloat(rawValue);
        if (material == null) {
            sendInputError(player, "&cInvalid item: &e" + itemName);
            return;
        }
        if (value == null) {
            sendInputError(player, "&cInvalid number: &e" + rawValue);
            return;
        }

        File weightsDir = new File(getPlugin().getDataFolder(), "Weights");
        File[] files = {
                new File(weightsDir, "Blocks Weight.json"),
                new File(weightsDir, "Tools And Weapons Weight.json"),
                new File(weightsDir, "Misc Items Weight.json")
        };

        for (File file : files) {
            try {
                JSONObject root = readJson(file);
                if (!replaceWeight(root, itemName, rawValue)) {
                    continue;
                }
                writeJson(file, root);
                jsonFile.reloadWeightMaps();

                String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                        "set-item-success-message",
                        getPlugin().getPluginPrefix() + "&aYou successfully set the weight of &e" + itemName
                                + " &ato &e" + rawValue + " &a."), player);
                message = message.replace("%item%", itemName).replace("%itemweight%", rawValue);
                player.sendMessage(ColorUtils.translateColorCodes(message));
                return;
            } catch (IOException | RuntimeException exception) {
                getPlugin().getLogger().severe("Unable to update " + file.getName() + ": " + exception.getMessage());
                sendEditFailure(player);
                return;
            }
        }

        String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                "set-item-fail-message",
                getPlugin().getPluginPrefix() + "&cCouldn't find the &e" + itemName + " &cin any of the weight files."), player);
        player.sendMessage(ColorUtils.translateColorCodes(message.replace("%item%", itemName)));
    }

    private void addItemWeight(Player player, String rawItemName, String rawValue) {
        String itemName = rawItemName.toUpperCase(Locale.ROOT);
        Material material = Material.getMaterial(itemName);
        Float value = parseFiniteFloat(rawValue);
        if (material == null) {
            sendInputError(player, "&cInvalid item: &e" + itemName);
            return;
        }
        if (value == null) {
            sendInputError(player, "&cInvalid number: &e" + rawValue);
            return;
        }

        Float existingWeight = globalItemsWeight.get(material);
        if (existingWeight != null) {
            String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                    "add-item-found-message",
                    getPlugin().getPluginPrefix() + "&cThis item already exists in the weight files and it's weight value is &e"
                            + existingWeight + "&c."), player);
            player.sendMessage(ColorUtils.translateColorCodes(
                    message.replace("%itemweight%", String.valueOf(existingWeight))));
            return;
        }

        File file = new File(new File(getPlugin().getDataFolder(), "Weights"), "Misc Items Weight.json");
        try {
            JSONObject root = readJson(file);
            JSONArray additionalItems = root.optJSONArray("Additional Items");
            if (additionalItems == null) {
                additionalItems = new JSONArray();
                root.put("Additional Items", additionalItems);
            }
            additionalItems.put(itemName + "=" + rawValue);
            writeJson(file, root);
            jsonFile.reloadWeightMaps();

            String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                    "add-item-success-message",
                    getPlugin().getPluginPrefix() + "&aYou successfully added the weight of &e" + itemName
                            + " &ato &b" + rawValue + " &a."), player);
            message = message.replace("%item%", itemName)
                    .replace("%itemweight%", String.valueOf(globalItemsWeight.getOrDefault(material, value)));
            player.sendMessage(ColorUtils.translateColorCodes(message));
        } catch (IOException | RuntimeException exception) {
            getPlugin().getLogger().severe("Unable to update " + file.getName() + ": " + exception.getMessage());
            sendEditFailure(player);
        }
    }

    private void setHeldItemPersistentWeight(Player player, String rawValue, boolean boost) {
        Float value = parseFiniteFloat(rawValue);
        if (value == null) {
            sendInputError(player, "&cInvalid number.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            String path = boost ? "boost-add-item-fail-message" : "custom-add-item-fail-message";
            send(player, Messages.getMessages().getString(path,
                    getPlugin().getPluginPrefix() + "&cYou must hold an item to set its weight."));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sendInputError(player, "&cThis item cannot store custom weight data.");
            return;
        }

        NamespacedKey weightKey = new NamespacedKey(getPlugin(), "weight");
        NamespacedKey boostKey = new NamespacedKey(getPlugin(), "boost");
        if (boost) {
            meta.getPersistentDataContainer().remove(weightKey);
            meta.getPersistentDataContainer().set(boostKey, PersistentDataType.FLOAT, value);
        } else {
            meta.getPersistentDataContainer().remove(boostKey);
            meta.getPersistentDataContainer().set(weightKey, PersistentDataType.FLOAT, value);
        }
        item.setItemMeta(meta);

        if (boost) {
            ItemLoreUtils.updateBoostItemLore(item, value);
        } else {
            ItemLoreUtils.updateItemLore(item, value);
        }
        weightCalculator.calculateWeight(player);

        String path = boost ? "boost-add-item-success-message" : "custom-add-item-success-message";
        String fallback = boost
                ? getPlugin().getPluginPrefix() + "&aSet boost weight &e" + rawValue + " &afor item."
                : getPlugin().getPluginPrefix() + "&aSet custom weight &e" + rawValue + " &afor item.";
        String message = weightCalculator.formatMessage(Messages.getMessages().getString(path, fallback), player);
        message = message.replace(boost ? "%boostweight%" : "%itemweight%", rawValue);
        player.sendMessage(ColorUtils.translateColorCodes(message));
    }

    private boolean handleConsoleCommand(Server server, String[] args) {
        if (args.length == 0) {
            server.getConsoleSender().sendMessage(getPlugin().getPluginPrefix()
                    + ChatColor.GRAY + "This command can only be executed by a player.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            boolean success = reloadCommand();
            server.getConsoleSender().sendMessage(getPlugin().getPluginPrefix() + ChatColor.GRAY
                    + (success ? "Config and weight files reloaded successfully."
                    : "There was an error while reloading."));
            return true;
        }

        return true;
    }

    private boolean reloadCommand() {
        File config = new File(getPlugin().getDataFolder(), "config.yml");
        File messages = new File(getPlugin().getDataFolder(), "messages.yml");
        File weightsDir = new File(getPlugin().getDataFolder(), "Weights");
        File blocksWeight = new File(weightsDir, "Blocks Weight.json");
        File toolsWeight = new File(weightsDir, "Tools And Weapons Weight.json");
        File miscWeight = new File(weightsDir, "Misc Items Weight.json");

        if (config.exists()) {
            getPlugin().reloadConfig();
        } else {
            getPlugin().saveDefaultConfig();
        }

        if (messages.exists()) {
            Messages.reloadMessagesConfig();
        } else {
            Messages.create();
        }

        CalculateWeight.refreshThresholdValues();

        if (getPlugin().task != null && !getPlugin().task.isCancelled()) {
            getPlugin().task.cancel();
        }
        getPlugin().scheduler();

        if (!blocksWeight.exists() || !toolsWeight.exists() || !miscWeight.exists()) {
            jsonFile.saveJsonFile();
        }

        boolean success = jsonFile.reloadWeightMaps();
        for (Player onlinePlayer : getPlugin().getServer().getOnlinePlayers()) {
            weightCalculator.calculateWeight(onlinePlayer);
        }
        getPlugin().reloadPluginPrefix();
        return success;
    }

    private JSONObject readJson(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            return new JSONObject(new JSONTokener(reader));
        }
    }

    private void writeJson(File file, JSONObject root) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.write(root.toString(2));
            if (writer.checkError()) {
                throw new IOException("Failed while writing " + file.getName());
            }
        }
    }

    private boolean replaceWeight(JSONObject root, String itemName, String rawValue) {
        Iterator<String> keys = root.keys();
        while (keys.hasNext()) {
            Object section = root.opt(keys.next());
            if (!(section instanceof JSONArray values)) {
                continue;
            }

            for (int i = 0; i < values.length(); i++) {
                String entry = values.optString(i, null);
                if (entry == null) {
                    continue;
                }
                String[] parts = entry.split("=", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(itemName)) {
                    values.put(i, itemName + "=" + rawValue);
                    return true;
                }
            }
        }
        return false;
    }

    private Float parseFiniteFloat(String value) {
        try {
            float parsed = Float.parseFloat(value);
            return Float.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void commandHelp(Player player, String permission, String path, String fallback) {
        if (!player.hasPermission(permission)) {
            noPermMessage(player);
            return;
        }
        send(player, Messages.getMessages().getString(path, fallback));
    }

    private void send(Player player, String message) {
        player.sendMessage(ColorUtils.translateColorCodes(weightCalculator.formatMessage(message, player)));
    }

    private void sendInputError(Player player, String message) {
        player.sendMessage(ColorUtils.translateColorCodes(getPlugin().getPluginPrefix() + message));
    }

    private void sendEditFailure(Player player) {
        send(player, Messages.getMessages().getString("fail-reload-message",
                getPlugin().getPluginPrefix() + "&cThere was an error while updating the weight file, check the console."));
    }

    private void unknownCommandMessage(Player player) {
        send(player, Messages.getMessages().getString("unknown-command",
                getPlugin().getPluginPrefix() + "&cCouldn't find this command."));
    }

    private void noPermMessage(Player player) {
        send(player, Messages.getMessages().getString("no-permission-message",
                getPlugin().getPluginPrefix() + "&cYou do not have permission to use this command."));
    }
}
