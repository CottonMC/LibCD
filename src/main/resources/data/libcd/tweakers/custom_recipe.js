var RecipeTweaker = libcd.require("libcd.recipe.RecipeTweaker");

RecipeTweaker.addCustomShaped(libcd,
    [["minecraft:diamond", "minecraft:netherite_ingot", "minecraft:diamond"],
     ["",                  "minecraft:stick",           ""],
     ["",                  "minecraft:stick",           ""]],
    "minecraft:netherite_pickaxe"
    );

function matches(stacks, width, height, player, world) {
    return player.getHealth() > 19 || player.isCreative();
}

function preview(stacks, width, height, player, output) {
    return output;
}

function craft(stacks, player, output) {
    if (!player.isCreative()) {
        player.damage(19);
    }
}
