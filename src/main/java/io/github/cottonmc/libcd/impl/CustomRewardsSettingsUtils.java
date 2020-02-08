package io.github.cottonmc.libcd.impl;

import net.minecraft.util.Identifier;

import java.util.Map;

public interface CustomRewardsSettingsUtils {
    Map<Identifier, Object> getAllRewardsSettings();
    void addAllRewardsSettings(Map<Identifier, Object> rewardsSettings);
    Object getRewardSettings(Identifier id);
    void addRewardSettings(Identifier id, Object o);
}
