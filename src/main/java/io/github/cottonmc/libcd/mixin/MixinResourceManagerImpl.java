package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.condition.ConditionalData;
import io.github.cottonmc.libcd.impl.ReloadListenersAccessor;
import io.github.cottonmc.libcd.impl.ResourceSearcher;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class MixinResourceManagerImpl implements ReloadableResourceManager, ReloadListenersAccessor, ResourceSearcher {

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
			if (libcd$contains(metaId)) {
				try {
					Resource meta = getResource(metaId);
					String metaText = IOUtils.toString(meta.getInputStream());
					if (!ConditionalData.shouldLoad(id, metaText)) {
						sortedResources.remove(id);
					}
				} catch (IOException e) {
					CDCommons.logger.error("Error when accessing resource metadata for %s: %s", id.toString(), e.getMessage());
				}
			}
		}
	}

	@Override
	public List<ResourceReloadListener> libcd$getListeners() {
		return listeners;
	}

	public boolean libcd$contains(Identifier id) {
		ResourceManager manager = this.namespaceManagers.get(id.getNamespace());
		return manager != null && ((ResourceSearcher) manager).libcd$contains(id);
	}


}
