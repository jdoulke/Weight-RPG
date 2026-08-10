package ted_2001.WeightRPG.Listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ted_2001.WeightRPG.Utils.ColorUtils;

import java.util.Locale;

import static ted_2001.WeightRPG.WeightRPG.getPlugin;

/**
 * Validates weight-changing commands before the legacy command executor writes values to disk.
 * Valid commands are left completely untouched.
 */
public final class WeightCommandValidationListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWeightCommand(PlayerCommandPreprocessEvent event) {
        String raw = event.getMessage();
        if (raw == null || raw.length() < 2) {
            return;
        }

        String[] args = raw.substring(1).trim().split("\\s+");
        if (args.length != 4 || !args[0].equalsIgnoreCase("weight")) {
            return;
        }

        String subCommand = args[1].toLowerCase(Locale.ROOT);
        if (!subCommand.equals("set") && !subCommand.equals("add")
                && !subCommand.equals("custom") && !subCommand.equals("boost")) {
            return;
        }

        if ((subCommand.equals("set") || subCommand.equals("add"))
                && Material.matchMaterial(args[2]) == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ColorUtils.translateColorCodes(
                    getPlugin().getPluginPrefix() + "&cInvalid item: &e" + args[2]));
            return;
        }

        // custom/boost use: /weight custom|boost add <value>
        if ((subCommand.equals("custom") || subCommand.equals("boost"))
                && !args[2].equalsIgnoreCase("add")) {
            return;
        }

        try {
            float value = Float.parseFloat(args[3]);
            if (!Float.isFinite(value)) {
                throw new NumberFormatException("Non-finite value");
            }
        } catch (NumberFormatException ignored) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ColorUtils.translateColorCodes(
                    getPlugin().getPluginPrefix() + "&cInvalid number."));
        }
    }
}
