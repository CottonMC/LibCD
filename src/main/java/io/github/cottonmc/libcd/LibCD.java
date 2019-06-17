package io.github.cottonmc.libcd;

import io.github.cottonmc.libcd.condition.ConditionalData;
import io.github.cottonmc.libcd.tweaker.RecipeTweaker;
import io.github.cottonmc.libcd.tweaker.Tweaker;
import io.github.cottonmc.libcd.tweaker.TweakerLoader;
import io.github.cottonmc.libcd.tweaker.TweakerStackGetter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static final Logger logger = LogManager.getLogger();

	@Override
	public void onInitialize() {
		ConditionalData.init();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TweakerLoader());
		Tweaker.addTweaker(RecipeTweaker.INSTANCE);
		TweakerStackGetter.registerGetter(new Identifier("minecraft", "potion"), (id) -> {
			Potion potion = Potion.byId(id.toString());
			if (potion == Potions.EMPTY) return ItemStack.EMPTY;
			return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
		});
	}

	/**
	 * Moved to {@link ConditionalData#registerCondition(Identifier, Predicate)}
	 */
	@Deprecated
	public static void registerCondition(Identifier id, Predicate<Object> condition) {
		ConditionalData.registerCondition(id, condition);
	}
}
