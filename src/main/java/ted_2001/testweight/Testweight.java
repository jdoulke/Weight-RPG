package ted_2001.testweight;

import org.bukkit.plugin.java.JavaPlugin;
import ted_2001.testweight.Listeners.inventoryClose;

public final class Testweight extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new inventoryClose(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
