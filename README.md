Weight-RPG is a Minecraft plugin that adds a new mechanic to the game.

Inventory weight mechanic just like in RPGs!

Description

Have you ever played RPG games like The Elder Scrolls, Fallout, or The Witcher? Did you notice that you can’t carry too many items? Do you ever wonder if this feature was in Minecraft how more realistic would it be? Well now with the Weight-RPG plugin you can! Weight-RPG allows you to configure the weight of all Minecraft items. Upon reaching a specific inventory weight, the player will move slowly or even be unable to move and jump altogether.​

Features

    Assign weight value to every item.
    Custom weight for custom items by their names.
    Boost Items that grant additional weight capacity.
    Three weight levels so you can ‘punish’ players differently in each one. You can disable any level you want.
    You can use permissions for weight level.
    You can set item's weight using the command /weight set <item> <weight value>
    Players with permission weight.notify get a message for items that aren't in weight files. You can add them manually or you can use the command /weight add <item> <weight value>.
    Custom weight values and penalties for every weight level before it takes effect.
    You can disable jump and movement at any level you want for the player.
    You can disable player of picking-up items at any level.
    100% customizable messages in messages.yml file.
    HEX color support.
    Messages for pick-up, drop, or place items.
    16 custom placeholders to use in messages.
    Messages can be sent via action-bar or chat.
    You can disable the plugin in certain worlds.
    Players can have a cooldown for dropping items. This is to prevent players from throwing items to have maximum speed. You can disable this feature and modify the cooldown in seconds in the config file.
    You can view item's weight using /weight get <item> command.
    WorldGuard support. You can use the 'weight-rpg' flag to deny using the plugin in certain regions. (WorldGuard version 7.0 or later).
    PlaceholderAPI support. you can use all these placeholders in any plugin that supports PlaceholderAPI (2.10.0 version or above).

How it works

The plugin is checking the player's inventory every 3 seconds (configurable in config.yml) and when he drops, picks up, or places an item. The Items' weight values are in the Weight files.
(Blocks Weight.json, Tools and Weapons Weight.json, Misc Items Weight.json)
There you will find all Minecraft items and their weight value.
You need to re-assign all the item values to your needs. But don't worry it's one-time work. After that don't forget to use '/weight reload' to apply the changes to the server. The plugin now calculates the player's inventory and if the value is bigger than your weight value on the config file it will apply penalties to the player.
It is working only when the player is on Survival or Adventure mode.​

Default item assignment
First, when the plugin loads for the first time it will create 2 files (config.yml, messages.yml) and 1 folder. In the config file, you can configure the plugin. In the messages file, you can customize messages to your needs. In the Weights folder, you will find 3 additional files. There you will assign the weight values for the items, (ex DIRT=5) you must follow the format “item=value” and for float values use dot ‘.’ not comma ‘,’. There are 3 files for easier assignment. You must assign the weight values DON’T USE THE DEFAULT ONES.​

Custom item assignment
After you configure the config file and assign all the weight values to items, use the command ‘/weight reload’ to apply the changes to the server. That’s it. To assign a weight value to a custom item, go to the config file in the “custom-items-weight” section and write the name with color codes like the examples.
&c&lThunder Sword=20 (Use the same format as mentioned above.)​
Boost Items Functionality
These special items can grant players additional weight-carrying capacity. When players have Boost Items in their inventory, their maximum weight capacity will increase, allowing them to carry more items without reaching the weight threshold. To define Boost Items and their respective weight enhancements, navigate to the config.yml file. Under the "boost-items" section, add the names of the Boost Items and their associated weight values. The format to be used is "itemname=boost_weight," similar to the custom item assignment.​


Command and Permissions

    /weight
    Permission: weight.use
    Description: show player weight and other info from messages.yml.
    /weight reload
    Permission: weight.reload
    Description: reload all files and apply changes to the server.
    /weight get <item>
    Permission: weight.get and weight.get.<item> or weight.get.*
    Description: show the item's weight.
    /weight set <item> <weight value>
    Permission: weight.set
    Description: set the item's weight.
    /weight add <item> <weight value>
    Permission: weight.add
    Description: add item to the weight files. The item will be on Misc Items Weight file on Additional Items section.
    Permission: weight.notify
    Description: Players with this permission will be notified when an item isn't on the weight files.
    You can use permission mode to calculate the weight of players. You need to add these 3 permissions. weight.level1.x, weight.level2.x, weight.level3.x . Where x is your amount of weight in the level.

    Use the ‘weight.bypass’ permission to exclude a player from the weight calculations.

