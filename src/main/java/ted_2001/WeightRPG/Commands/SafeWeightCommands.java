package ted_2001.WeightRPG.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import ted_2001.WeightRPG.Utils.CalculateWeight;
import ted_2001.WeightRPG.Utils.ColorUtils;
import ted_2001.WeightRPG.Utils.JsonFile;
import ted_2001.WeightRPG.Utils.Messages;
import ted_2001.WeightRPG.Utils.WeightDataLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;

import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Safe front-end for weight-file mutations and reloads.
 * Every unrelated command is delegated unchanged to the legacy executor.
 */
public final class SafeWeightCommands implements CommandExecutor {

    private final WeightCommands legacy = new WeightCommands();
    private final JsonFile jsonFile = new JsonFile();
    private final WeightDataLoader dataLoader = new WeightDataLoader();
    private final CalculateWeight weightCalculator = new CalculateWeight();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player player) {
                if (!player.hasPermission("weight.reload")) {
                    return legacy.onCommand(sender, command, label, args);
                }
                boolean success = reloadPluginData();
                String key = success ? "success-reload-message" : "fail-reload-message";
                String fallback = success
                        ? getPlugin().getPluginPrefix() + "&aConfig and weight files reloaded successfully."
                        : getPlugin().getPluginPrefix() + "&cThere was an error while reloading, check the console.";
                String message = weightCalculator.formatMessage(Messages.getMessages().getString(key, fallback), player);
                player.sendMessage(ColorUtils.translateColorCodes(message));
                return false;
            }

            if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
                boolean success = reloadPluginData();
                sender.sendMessage(getPlugin().getPluginPrefix()
                        + (success ? "Config and weight files reloaded successfully."
                        : "There was an error while reloading; check the console."));
                return false;
            }
        }

        if (!(sender instanceof Player player) || args.length != 3) {
            return legacy.onCommand(sender, command, label, args);
        }

        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("set") && player.hasPermission("weight.set")) {
            return handleSet(player, args[1], args[2]);
        }
        if (subCommand.equalsIgnoreCase("add") && player.hasPermission("weight.add")) {
            return handleAdd(player, args[1], args[2]);
        }

        return legacy.onCommand(sender, command, label, args);
    }

    private boolean handleSet(Player player, String rawItemName, String rawWeight) {
        String itemName = rawItemName.toUpperCase(Locale.ROOT);
        if (!isValidMaterial(itemName) || !isFiniteWeight(rawWeight)) {
            sendInputError(player, itemName, rawWeight);
            return false;
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
                if (!replaceWeight(root, itemName, rawWeight)) {
                    continue;
                }

                writeJson(file, root);
                boolean loaded = dataLoader.reloadLiveMaps();
                if (!loaded) {
                    getPlugin().getLogger().warning("Weight maps were reloaded with one or more invalid entries after /weight set.");
                }
                sendSetSuccess(player, itemName, rawWeight);
                return false;
            } catch (IOException | RuntimeException exception) {
                getPlugin().getLogger().severe("Unable to update " + file.getName() + ": " + exception.getMessage());
                sendEditFailure(player);
                return false;
            }
        }

        String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                "set-item-fail-message",
                getPlugin().getPluginPrefix() + "&cCouldn't find &e" + itemName + " &cin any of the weight files."), player);
        player.sendMessage(ColorUtils.translateColorCodes(message.replace("%item%", itemName)));
        return false;
    }

    private boolean handleAdd(Player player, String rawItemName, String rawWeight) {
        String itemName = rawItemName.toUpperCase(Locale.ROOT);
        Material material = Material.getMaterial(itemName);
        if (material == null || !isFiniteWeight(rawWeight)) {
            sendInputError(player, itemName, rawWeight);
            return false;
        }

        Float existingWeight = globalItemsWeight.get(material);
        if (existingWeight != null) {
            String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                    "add-item-found-message",
                    getPlugin().getPluginPrefix() + "&cThis item already exists in the weight files and it's weight value is &e " + existingWeight + "&c."), player);
            message = message.replace("%itemweight%", String.valueOf(existingWeight));
            player.sendMessage(ColorUtils.translateColorCodes(message));
            return false;
        }

        File miscFile = new File(new File(getPlugin().getDataFolder(), "Weights"), "Misc Items Weight.json");
        try {
            JSONObject root = readJson(miscFile);
            JSONArray additionalItems = root.has("Additional Items")
                    ? root.getJSONArray("Additional Items")
                    : new JSONArray();

            additionalItems.put(itemName + "=" + rawWeight);
            root.put("Additional Items", additionalItems);
            writeJson(miscFile, root);
            boolean loaded = dataLoader.reloadLiveMaps();
            if (!loaded) {
                getPlugin().getLogger().warning("Weight maps were reloaded with one or more invalid entries after /weight add.");
            }

            String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                    "add-item-success-message",
                    getPlugin().getPluginPrefix() + "&aYou successfully added the weight of &e" + itemName
                            + " &ato &b" + rawWeight + " &a."), player);
            message = message
                    .replace("%item%", itemName)
                    .replace("%itemweight%", String.valueOf(globalItemsWeight.getOrDefault(material, Float.parseFloat(rawWeight))));
            player.sendMessage(ColorUtils.translateColorCodes(message));
        } catch (IOException | RuntimeException exception) {
            getPlugin().getLogger().severe("Unable to update " + miscFile.getName() + ": " + exception.getMessage());
            sendEditFailure(player);
        }
        return false;
    }

    private boolean reloadPluginData() {
        File config = new File(getPlugin().getDataFolder(), "config.yml");
        File messages = new File(getPlugin().getDataFolder(), "messages.yml");
        File weightsDir = new File(getPlugin().getDataFolder(), "Weights");
        File blocksWeight = new File(weightsDir, "Blocks Weight.json");
        File toolsWeight = new File(weightsDir, "Tools And Weapons Weight.json");
        File miscWeight = new File(weightsDir, "Misc Items Weight.json");

        if (config.exists()) {
            getPlugin().reloadConfig();
        } else {
            getPlugin().getConfig().options().copyDefaults();
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

        boolean success = dataLoader.reloadLiveMaps();
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

    private boolean replaceWeight(JSONObject root, String itemName, String rawWeight) {
        Iterator<String> keys = root.keys();
        while (keys.hasNext()) {
            Object value = root.opt(keys.next());
            if (!(value instanceof JSONArray array)) {
                continue;
            }

            for (int i = 0; i < array.length(); i++) {
                String entry = array.optString(i, null);
                if (entry == null) {
                    continue;
                }
                String[] parts = entry.split("=", 2);
                if (parts.length == 2 && parts[0].equalsIgnoreCase(itemName)) {
                    array.put(i, itemName + "=" + rawWeight);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isValidMaterial(String itemName) {
        return Material.getMaterial(itemName) != null;
    }

    private boolean isFiniteWeight(String rawWeight) {
        try {
            return Float.isFinite(Float.parseFloat(rawWeight));
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private void sendInputError(Player player, String itemName, String rawWeight) {
        if (!isValidMaterial(itemName)) {
            player.sendMessage(ColorUtils.translateColorCodes(
                    getPlugin().getPluginPrefix() + "&cInvalid item: &e" + itemName));
        } else {
            player.sendMessage(ColorUtils.translateColorCodes(
                    getPlugin().getPluginPrefix() + "&cInvalid number: &e" + rawWeight));
        }
    }

    private void sendSetSuccess(Player player, String itemName, String rawWeight) {
        String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                "set-item-success-message",
                getPlugin().getPluginPrefix() + "&aYou successfully set the weight of &e" + itemName
                        + " &ato &e" + rawWeight + " &a."), player);
        message = message.replace("%item%", itemName).replace("%itemweight%", rawWeight);
        player.sendMessage(ColorUtils.translateColorCodes(message));
    }

    private void sendEditFailure(Player player) {
        String message = weightCalculator.formatMessage(Messages.getMessages().getString(
                "fail-reload-message",
                getPlugin().getPluginPrefix() + "&cThere was an error while updating the weight file, check the console."), player);
        player.sendMessage(ColorUtils.translateColorCodes(message));
    }
}
