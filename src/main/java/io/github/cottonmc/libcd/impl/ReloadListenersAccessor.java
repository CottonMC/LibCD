package io.github.cottonmc.libcd.impl;

import net.minecraft.resource.ResourceReloadListener;

import java.util.List;

public interface ReloadListenersAccessor {
	List<ResourceReloadListener> libcd$getListeners();
}
