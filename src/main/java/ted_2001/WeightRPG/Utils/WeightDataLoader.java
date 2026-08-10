package ted_2001.WeightRPG.Utils;

import org.bukkit.Material;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static ted_2001.WeightRPG.Utils.JsonFile.boostItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.customItemsWeight;
import static ted_2001.WeightRPG.Utils.JsonFile.globalItemsWeight;
import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Defensive loader for the existing Weight-RPG file format.
 *
 * This intentionally keeps the legacy non-transactional semantics: the live maps are cleared
 * first and then populated in the same file/section order as before. Bad individual entries are
 * logged and skipped instead of crashing the whole load path.
 */
public final class WeightDataLoader {

    private static final String[] BLOCK_SECTIONS = {
            "Blocks Weight",
            "Doors Weight",
            "Signs Weight",
            "Wools Weight",
            "Leaves Weight",
            "Glasses Weight",
            "Boats Weight",
            "Fences Weight",
            "Plates Weight",
            "Carpets Weight",
            "Terracottas Weight",
            "Beds Weight",
            "Shulker Boxes Weight",
            "Buttons Weight",
            "Concretes Weight",
            "Working Tables and Furnaces Weight",
            "Logs, Planks and Saplings Weight"
    };

    private static final String[] MISC_SECTIONS = {
            "Heads Weight",
            "Misc Items Weight",
            "Banners Weight",
            "Eggs Weight",
            "Ingots and Ores Weight",
            "Food Items Weight",
            "Flowers Weight",
            "Records Weight",
            "Candles Weight",
            "Redstone Items Weight",
            "Dyes Weight"
    };

    private static final String[] TOOL_SECTIONS = {
            "Horse Armor Weight",
            "Tools Weight",
            "Arrows Weight",
            "Armor Weight"
    };

    public boolean reloadLiveMaps() {
        globalItemsWeight.clear();
        customItemsWeight.clear();
        boostItemsWeight.clear();

        boolean success = true;
        File weightsDir = new File(getPlugin().getDataFolder(), "Weights");

        success &= loadWeightFile(new File(weightsDir, "Blocks Weight.json"), BLOCK_SECTIONS, false);
        success &= loadWeightFile(new File(weightsDir, "Misc Items Weight.json"), MISC_SECTIONS, true);
        success &= loadWeightFile(new File(weightsDir, "Tools And Weapons Weight.json"), TOOL_SECTIONS, false);
        success &= loadNamedWeights(getPlugin().getConfig().getStringList("custom-items-weight"), false);
        success &= loadNamedWeights(getPlugin().getConfig().getStringList("boost-items"), true);

        return success;
    }

    private boolean loadWeightFile(File file, String[] sections, boolean loadAdditionalItemsFirst) {
        JSONObject root;
        try (FileReader reader = new FileReader(file)) {
            root = new JSONObject(new JSONTokener(reader));
        } catch (IOException | RuntimeException exception) {
            logError(file.getName(), "could not be read", exception.getMessage());
            return false;
        }

        boolean success = true;
        if (loadAdditionalItemsFirst && root.has("Additional Items")) {
            success &= loadSection(root, "Additional Items", file.getName());
        }
        for (String section : sections) {
            success &= loadSection(root, section, file.getName());
        }
        return success;
    }

    private boolean loadSection(JSONObject root, String section, String fileName) {
        JSONArray values = root.optJSONArray(section);
        if (values == null) {
            logError(fileName, "is missing section", section);
            return false;
        }

        boolean success = true;
        for (int i = 0; i < values.length(); i++) {
            String entry = values.optString(i, null);
            if (entry == null || !loadMaterialEntry(entry, fileName, section)) {
                success = false;
            }
        }
        return success;
    }

    private boolean loadMaterialEntry(String entry, String fileName, String section) {
        String[] parts = splitEntry(entry);
        if (parts == null) {
            logBadEntry(fileName, section, entry, "expected ITEM=value");
            return false;
        }

        Material material = Material.getMaterial(parts[0]);
        if (material == null) {
            logBadEntry(fileName, section, entry, "unknown Minecraft material");
            return false;
        }

        Float weight = parseFiniteWeight(parts[1]);
        if (weight == null) {
            logBadEntry(fileName, section, entry, "weight is not a finite number");
            return false;
        }

        globalItemsWeight.put(material, weight);
        return true;
    }

    private boolean loadNamedWeights(List<String> entries, boolean boost) {
        boolean success = true;
        String source = boost ? "boost-items" : "custom-items-weight";

        for (String entry : entries) {
            String[] parts = splitEntry(entry);
            if (parts == null) {
                logBadEntry("config.yml", source, entry, "expected item-name=value");
                success = false;
                continue;
            }

            Float weight = parseFiniteWeight(parts[1]);
            if (weight == null) {
                logBadEntry("config.yml", source, entry, "weight is not a finite number");
                success = false;
                continue;
            }

            String displayName = ColorUtils.translateColorCodes(parts[0]);
            if (boost) {
                boostItemsWeight.put(displayName, weight);
            } else {
                customItemsWeight.put(displayName, weight);
            }
        }
        return success;
    }

    private String[] splitEntry(String entry) {
        if (entry == null) {
            return null;
        }

        int separator = entry.indexOf('=');
        if (separator <= 0 || separator == entry.length() - 1) {
            return null;
        }
        return new String[]{entry.substring(0, separator), entry.substring(separator + 1)};
    }

    private Float parseFiniteWeight(String value) {
        try {
            float parsed = Float.parseFloat(value);
            return Float.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void logBadEntry(String file, String section, String entry, String reason) {
        getPlugin().getLogger().warning("Invalid weight entry in " + file + " / " + section
                + ": '" + entry + "' (" + reason + ")");
    }

    private void logError(String file, String action, String detail) {
        getPlugin().getLogger().severe("Weight file " + file + " " + action
                + (detail == null || detail.isEmpty() ? "." : ": " + detail));
    }
}
