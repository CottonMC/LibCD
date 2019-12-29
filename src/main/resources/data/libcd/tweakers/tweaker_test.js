//warning for production environments
log.info("WARNING! LibCD Tweaker sample script is running!");
log.info("If you are not in a dev environment, please report this!");

//imports - use `libcd.require`
var RecipeTweaker = libcd.require("libcd.recipe.RecipeTweaker");
var TweakerUtils = libcd.require("libcd.util.TweakerUtils");

//A shorthand to get a stick input
var stick = "minecraft:stick";
//A shorthand to get a crafting table output
var table = TweakerUtils.createItemStack("minecraft:crafting_table");

//remove the vanilla crafting table recipe, so we can use ours instead
RecipeTweaker.removeRecipe("minecraft:crafting_table");

//remove all recipes for gold nuggets - make them entirely uncraftable
RecipeTweaker.removeRecipesFor("minecraft:gold_nugget");

//remove all polished andesite slab recipes obtained by stonecutting
RecipeTweaker.removeRecipesFor("minecraft:polished_andesite_slab", "minecraft:stonecutting");

//create a recipe for a piece of tall grass and a piece of bonemeal or white dye -> a two-high piece of tall grass
RecipeTweaker.addShapeless(["minecraft:grass", RecipeTweaker.makeIngredient("fuzzy", "minecraft:bone_meal", "minecraft:white_dye")], TweakerUtils.createItemStack("minecraft:tall_grass"));

//create a recipe for a stone pickaxe and a diamond -> a diamond pickaxe with 1 durability left
RecipeTweaker.addShapeless(["minecraft:stone_pickaxe", "minecraft:diamond"], TweakerUtils.createItemStack("minecraft:diamond_pickaxe{Damage:1560}"));

//create a recipe for a stone pickaxe, a diamond, and an iron ingot -> a diamond pickaxe with 131 durability (the amount a stone pick has) left, along with an enchantment, lore and a custom name
//re-declaring is optional, but good for safety.

/*
NOTE: The current form imports code from script `libcd:import_test.js`, which contains the following code:
```js
var TweakerUtils = libcd.require("libcd.util.TweakerUtils");
var newPickStack = TweakerUtils.createItemStack("minecraft:diamond_pickaxe");
newPickStack = TweakerUtils.setDamage(newPickStack, 1430);
newPickStack = TweakerUtils.setName(newPickStack, "Patched Pickaxe");
newPickStack = TweakerUtils.enchant(newPickStack, "minecraft:fortune", 1);
newPickStack = TweakerUtils.addLore(newPickStack, [
    "A stone pick strengthened",
    "with iron and diamond.",
    "Not very durable, but",
    "should be able to mine",
    "better than usual."
]);
```
*/
//import other test script
var imported = libcd.importScript("libcd:import_test.js");
//get the var defined in the other script to use as our output stack
RecipeTweaker.addShapeless(["minecraft:stone_pickaxe", "minecraft:diamond", "minecraft:iron_ingot"], imported.getVar("newPickStack"));

//create a recipe for a stick above a stone block with two oak planks on the right -> a crafting table
RecipeTweaker.addShaped([[stick, "minecraft:oak_planks"], ["minecraft:stone", "minecraft:oak_planks"]], table);

//create a recipe for three gravel in an upside-down L -> 1 piece of flint
RecipeTweaker.addShaped([["minecraft:gravel",   "minecraft:gravel"],
    ["minecraft:gravel", null]],
    "minecraft:flint@5");

//create a pattern/dictionary set for a shaped recipe
var pattern = [ '/ /',
                '/_/',
                '/ /'];
var dict = {
    "/": stick,
    //put a # at the front of an ID to make it a tag instead of an item
    "_": "#minecraft:wooden_slabs"
};

//create a recipe for sticks on the sides and any wooden slab in the middle -> six ladders
RecipeTweaker.addDictShaped(pattern, dict, "minecraft:ladder@6");

//create a recipe to smelt a bottle of water -> a bottle of dragon's breath
//use a getter syntax (two IDs separated by ->) for special getters like potions
RecipeTweaker.addSmelting("minecraft:potion->minecraft:water", "minecraft:dragon_breath", 200, 0.35);

//create a recipe to blast a piece of coal -> a diamond. Takes 4x the fuel and time of a standard blasting operation
RecipeTweaker.addBlasting("minecraft:coal", "minecraft:diamond", 400, 0.7);

//create a recipe to smoke a log -> a block of charcoal. Takes 4x the fuel and time of a standard smoking operation
RecipeTweaker.addSmoking("#minecraft:logs", "minecraft:coal_block", 400, 0.7);

//create a recipe to bake an apple -> a golden apple on a campfire
RecipeTweaker.addCampfire("minecraft:apple", "minecraft:golden_apple", 600, 0.7);

//get all the IDs of items in the #minecraft:logs tag
var logs = TweakerUtils.getItemsInTag("minecraft:logs");

//create recipes to convert any type of log -> any other type of log in a stonecutter, iterating over the logs tag
for (var i = 0; i < logs.length; i++) {
    RecipeTweaker.addStonecutting("#minecraft:logs", logs[i]);
}

var builder = RecipeTweaker.builder("minecraft:smelting")
    .ingredient("ingredient", "minecraft:diamond")
    .itemStack("result", "minecraft:obsidian@8")
    .property("cookingtime", 200)
    .property("experience", 0.35);

RecipeTweaker.addRecipe(builder.build());

builder = RecipeTweaker.builder("minecraft:stonecutting")
    .ingredient("ingredient", "minecraft:diamond_block")
    .itemStack("result", "minecraft:diamond@9");

RecipeTweaker.addRecipe(builder.build());

//import the stuff we need for loot tables
var LootTweaker = libcd.require("libcd.loot.LootTweaker");
var Conditions = libcd.require("libcd.loot.Conditions");
var Functions = libcd.require("libcd.loot.Functions");
var Entries = libcd.require("libcd.loot.Entries");

//start messing with loot tables!
var sheepTable = LootTweaker.getTable("minecraft:entities/sheep");
var mainPool = sheepTable.getPool(0);
mainPool.removeEntry("minecraft:item", "minecraft:mutton");
var prismarineEntry = Entries.item("minecraft:prismarine_shard")
    .addFunctions(Functions.countExact(5))
    .addConditions(Conditions.killedByPlayer());
mainPool.addEntries(prismarineEntry);