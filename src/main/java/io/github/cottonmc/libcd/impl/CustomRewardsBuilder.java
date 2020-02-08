package io.github.cottonmc.libcd.impl;

import net.minecraft.util.Identifier;

import java.util.Map;

public interface CustomRewardsBuilder {
    CustomRewardsBuilder add(Identifier id, Object settings);
    CustomRewardsBuilder addAll(Map<Identifier, Object> rewardsSettings);
}
