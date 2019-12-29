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