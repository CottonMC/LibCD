var RecipeTweaker = libcd.require("libcd.recipe.RecipeTweaker");

//add a custom shaped recipe that lets you make a netherite pick without netherite - but for a price
RecipeTweaker.addCustomShaped(libcd,
    [["minecraft:soul_sand", "#minecraft:stone_tool_materials", "minecraft:soul_sand"],
     ["",                  "minecraft:stick",           ""],
     ["",                  "minecraft:stick",           ""]],
    "minecraft:netherite_pickaxe"
    );

//make sure that the player has enough health to craft
function matches(stacks, width, height, player, world) {
    return player.getHealth() >= 20 || player.isCreative();
}

//set a custom-colored and non-italicized name
function preview(stacks, width, height, player, output) {
    return output.setFormattedName("{\"italic\":false,\"color\":\"#4A2A23\",\"text\":\"Souled Netherite Pickaxe\"}");
}

function craft(stacks, player, output) {
    if (!player.isCreative()) {
        player.damage(19);
    }
}
