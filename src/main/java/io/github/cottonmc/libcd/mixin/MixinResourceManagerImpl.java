package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.condition.ConditionalData;
import io.github.cottonmc.libcd.impl.ReloadListenersAccessor;
import io.github.cottonmc.libcd.impl.ResourceSearcher;
import net.minecraft.resource.*;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinResourceManagerImpl implements ReloadableResourceManager, ReloadListenersAccessor, ResourceSearcher {

	@Shadow @Final private static Logger LOGGER;

	@Shadow @Final private List<ResourceReloadListener> listeners;

	@Shadow public abstract List<Resource> getAllResources(Identifier id) throws IOException;

	@Shadow @Final private Map<String, NamespaceResourceManager> namespaceManagers;

	@Inject(method = "findResources", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void checkConditioalRecipes(String parent, Predicate<String> loadFilter, CallbackInfoReturnable cir,
										Set<Identifier> foundResources, List<Identifier> sortedResources) {
		List<Identifier> sortedCopy = new ArrayList<>(sortedResources);
		for (Identifier id : sortedCopy) {
			//don't try to load for things that use mcmetas already!
			if (id.getPath().contains(".mcmeta") || id.getPath().contains(".png")) continue;
			Identifier metaId = new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
			if (libcd_contains(metaId)) {
				System.out.println(id.toString() + " has mcmeta file " + metaId.toString());
				try {
					Resource meta = getResource(metaId);
					String metaText = IOUtils.toString(meta.getInputStream());
					if (!ConditionalData.shouldLoad(id, metaText)) {
						System.out.println(metaId.toString() + " cancels loading of " + id.toString());
						sortedResources.remove(id);
					}
				} catch (IOException e) {
					LOGGER.error("Error when accessing resource metadata for {}: {}", id.toString(), e.getMessage());
				}
			}
		}
	}

	@Override
	public List<ResourceReloadListener> libcd_getListeners() {
		return listeners;
	}

	public boolean libcd_contains(Identifier id) {
		ResourceManager manager = this.namespaceManagers.get(id.getNamespace());
		return manager != null && ((ResourceSearcher) manager).libcd_contains(id);
	}


}
