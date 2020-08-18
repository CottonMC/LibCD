package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.ResourceSearcher;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(NamespaceResourceManager.class)
public abstract class MixinNamespaceResourceManager implements ResourceManager, ResourceSearcher {
	@Shadow @Final protected List<ResourcePack> packList;

	@Shadow @Final private ResourceType type;

	@Shadow protected abstract boolean isPathAbsolute(Identifier id);

	public boolean libcd$contains(Identifier id) {
		if (!this.isPathAbsolute(id)) {
			return false;
		} else {
			for(int i = this.packList.size() - 1; i >= 0; --i) {
				ResourcePack pack = this.packList.get(i);
				if (pack.contains(this.type, id)) {
					return true;
				}
			}

			return false;
		}
	}

//	@Inject(method = "findResources", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
//	private void checkConditioalRecipes(String parent, Predicate<String> loadFilter, CallbackInfoReturnable<Collection<Identifier>> cir,
//										List<Identifier> sortedResources) {
//		List<Identifier> sortedCopy = new ArrayList<>(sortedResources);
//		for (Identifier id : sortedCopy) {
//			//don't try to load for things that use mcmetas already!
//			if (id.getPath().contains(".mcmeta") || id.getPath().contains(".png")) continue;
//			Identifier metaId = new Identifier(id.getNamespace(), id.getPath() + ".mcmeta");
//			if (libcd$contains(metaId)) {
//				try {
//					Resource meta = getResource(metaId);
//					String metaText = IOUtils.toString(meta.getInputStream(), Charsets.UTF_8);
//					if (!ConditionalData.shouldLoad(id, metaText)) {
//						sortedResources.remove(id);
//					}
//				} catch (IOException e) {
//					CDCommons.logger.error("Error when accessing resource metadata for %s: %s", id.toString(), e.getMessage());
//				}
//			}
//		}
//	}

}
