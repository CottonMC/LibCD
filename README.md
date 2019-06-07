<img src="icon.png" align="right" width="180px"/>

# LibConditionalData


[>> Downloads <<](https://github.com/CottonMC/LibConditionalData/releases)

*Improved data processing*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

LibConditionalData, or LibCD, adds hooks to conditionally load data pack elements based on a metadata file. It's focused on being simple and extensible, along with being compatible with *any* resource type added by other mods.

## Client Use
For conditional resources, add a file `<target resource with extension>.mcmeta`. This will be parsed as JSON to check whether the resource should be loaded. Each key-value pair will specify a condition that *must* be met for the recipe to be loaded. There are four conditions pre-included, and other mods may add their own:

- `libcd:mod_loaded` (passed a String): Will return true if a mod with the given mod ID is loaded.
- `libcd:item_exists` (passed a String): Will return true if an item with the given item ID is loaded.
- `libcd:not` (passed a single-element JsonObject) Will return true if the condition listed in the given object is *not* true.
- `libcd:any_of` (passed a JsonObject): Will return true if *any* condition listed in the given object is true.

**WARNING**: Currently, conditions silently return false if they are passed an improper parameter. This will hopefully be changed in the future, but if you think something should be loaded and it isn't, check your conditions.

## Developer Use
LibCD allows any mod to register their own conditions, to prevent recipe loadin based on config or more advanced logic. LibCD is available on the CurseForge maven.

To add a new condition, call `LibConditionalData.registerCondition()`, passing an Identifier for the condition's name and a `Predicate<Object> for the use of the condition.

The Object passed to the predicate will be a boxed primitive (Integer, Float, Boolean, etc.), a String, a List<JsonElement>, a JsonObject, or null. Use an `instanceof` check to be sure what you're being passed.
