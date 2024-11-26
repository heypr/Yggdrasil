package dev.heypr.yggdrasil.misc.papi.impl;

import dev.heypr.yggdrasil.data.PlayerData;
import dev.heypr.yggdrasil.misc.papi.IPlaceholder;

public final class BoogieManPlaceholder implements IPlaceholder {
    @Override
    public String resolve(PlayerData playerData) {
        return String.valueOf(playerData.isBoogeyman());
    }
}