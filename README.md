Weight-RPG
Inventory weight mechanic just like in RPGs!

Description

Have you ever played RPG games like The Elder Scrolls, Fallout, or The Witcher? Did you notice that you can’t carry too many items? Do you ever wonder if this feature was in Minecraft how more realistic would it be? Well now with the Weight-RPG plugin you can! Weight-RPG allows you to configure the weight of all Minecraft items. Upon reaching a specific inventory weight, the player will move slowly or even be unable to move and jump altogether.​

Features

    Assign weight value to every item.
    Custom weight for custom items by their names.
    Three weight levels so you can ‘punish’ players differently in each one. You can disable any level you want.
    Custom weight values and penalties for every weight level before it takes effect.
    You can disable jump and movement at any level you want for the player.
    100% customizable messages in messages.yml file.
    Messages for pick-up, drop, or place items.
    15 custom placeholders to use in messages.
    Messages can be sent via action-bar or chat.
    You can disable the plugin in certain worlds.

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


Command and Permissions

    /weight
    Description: show player weight and other info from messages.yml.
    Permission: weight.use
    /weight reload
    Description: reload all files and apply changes to the server.
    Permission: weight.reload

Use the ‘weight.bypass’ permission to exclude a player from the weight calculations.

