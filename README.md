<img src="icon.png" align="right" width="180px"/>

# LibConditionalData


[>> Downloads <<](https://github.com/CottonMC/LibCD/releases)

*Improved data processing*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See the [LICENSE file](LICENSE) for more details.

LibCapableData, or LibCD, is a suite of hooks to make data packs easier for both mod and pack developers. Conditions allow for conditional loading of data pack elements, and Tweakers allow programmatic adding and modifying of recipes, loot tables, and more.

## Conditions
Conditions are a framework for data-driven conditional loading of data pack elements. They're added with a `.mcmeta` system like resource pack metadata is. Tags are also given a `libcd` block for conditional entries outside of all-or-nothing loading.

## Tweakers
While JSON is better for most cases of simple data, sometimes pack devs want to do something more complex or add a lot of very similar systems. On top of that, there's currently no way to append to forms of data like loot tables, with only replacement as an option for JSONs. Tweakers fix this by adding a programmatic system for manipulation of data-driven systems. The system is built on top of JSR-223, so it has support for JavaScript by default, and any language with a scripting adapter for it.
