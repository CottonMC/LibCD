package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.LibConditionalData;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinResourceManagerImpl implements ReloadableResourceManager {

	@Shadow @Final private static Logger LOGGER;

	@Inject(method = "findResources", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void checkConditioalRecipes(String parent, Predicate<String> loadFilter, CallbackInfoReturnable cir,
										Set<Identifier> foundResources, Iterator itr, List<Identifier> sortedResources) {
		List<Identifier> sortedCopy = new ArrayList<>(sortedResources);
		for (Identifier id : sortedCopy) {
			//don't try to load for things that use mcmetas already!
			if (id.getPath().contains(".mcmeta") || id.getPath().contains(".png")) continue;
			Identifier metaId = new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
			if (containsResource(metaId)) {
				try {
					Resource meta = getResource(metaId);
					String metaText = IOUtils.toString(meta.getInputStream());
					if (!LibConditionalData.shouldLoad(id, metaText)) {
						sortedResources.remove(id);
					}
				} catch (IOException e) {
					LOGGER.error("Error when accessing recipe metadata for {}: {}", id.toString(), e.getMessage());
				}
			}
		}
	}
}
