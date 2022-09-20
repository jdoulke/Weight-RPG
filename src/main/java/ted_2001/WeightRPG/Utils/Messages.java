package ted_2001.WeightRPG.Utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;
public class Messages {

    private static File file;
    private static FileConfiguration configuration;



    //create the messages.yml file
    public static void create(){

        file = new File(Objects.requireNonNull(getServer().getPluginManager().getPlugin("Weight-RPG")).getDataFolder(), "messages.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        configuration = YamlConfiguration.loadConfiguration(file);

    }

    public static FileConfiguration getMessages(){
        return configuration;
    }

    public static void reloadMessagesConfig(){
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveMessages(){
        try {
            configuration.save(file);
        }catch (IOException e){
            System.out.println("[ERROR] Couldn't save custom_items.yml file.");
        }
    }

    public static void getDefaults(){
        try {
            //add the default options in the messages.yml
            configuration.load(file);
            ArrayList<String> comments = new ArrayList<>();configuration.addDefault("disable-jump-message", "%displayname%: &cI am carrying too many items to &2jump. %percentageweight%");
            configuration.addDefault("disable-jump-message-cooldown", 3);
            configuration.addDefault("receive-item-message", "%displayname% &aYou receive &c%amount% &aof &e%block% &awhich weights &6%totalweight%&a. %percentageweight%");
            configuration.addDefault("receive-item-message-cooldown", 1);
            configuration.addDefault("receive-item-message-enabled", true);
            configuration.addDefault("lost-item-message", "%displayname% &aYou lost &e%block% &awhich weights &6%totalweight%&a. %percentageweight%");
            configuration.addDefault("lost-item-message-cooldown", 1);
            configuration.addDefault("lost-item-message-enabled", true);
            configuration.addDefault("place-block-message", "%displayname% &aYou placed &e%block% &awhich weights &6%itemweight%&a. %percentageweight%");
            configuration.addDefault("place-block-message-cooldown", 2);
            configuration.addDefault("place-block-message-enabled", true);
            comments.clear();
            comments.add("%displayname% &eyour weight is &e%weight% &a/ &c%maxweight%.");
            comments.add("&cPercentage %percentage%%");
            comments.add("%percentageweight%");
            comments.add("&eLevel 1 &avalue is &c%level1%.");
            comments.add("&6Level 2 &avalue is &c%level2%.");
            comments.add("&cLevel 3 &avalue is &c%level3%.");
            comments.add("&aanything else you want.");
            configuration.addDefault("weight-command-message", comments.toArray());
            comments.clear();
            //add comments
            configuration.setComments("receive-item-message-enabled", Collections.singletonList("Enable or disable received items messages. (default is true)"));
            configuration.setComments("lost-item-message", Collections.singletonList("The message that will send to the player when he will drop or lose any item(s)."));
            configuration.setComments("weight-command-message", Collections.singletonList("Message of /weight command. Support all placeholders."));
            configuration.setComments("place-block-message", Collections.singletonList("The message that will send to the player when he will place a block."));
            configuration.setComments("disable-jump-message-cooldown", Collections.singletonList("Couldown in seconds before the next message will send to the player. (default is 3)"));
            comments.add("The message that wil send to the player when he will pick up or receive any item(s).");
            comments.add("You can use all placeholders %block% for items name, %itemweight% for items weight, ");
            comments.add("%itemdisplayname% for items display name, %amount% for items amount and %totalweight% for total items weight.");
            configuration.setComments("receive-item-message", comments);
            configuration.setComments("disable-jump-message", Collections.singletonList("The message that will send to the player when he is carrying too many items to jump. (if disable-jump is enabled"));
        }catch (IOException | InvalidConfigurationException e){
            System.out.println("[ERROR] Couldn't save messages.yml file.");
        }
    }

}
