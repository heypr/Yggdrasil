package dev.heypr.yggdrasil.misc.papi.impl;

import dev.heypr.yggdrasil.data.PlayerData;
import dev.heypr.yggdrasil.misc.papi.Placeholder;

public final class LivesPlaceholder implements Placeholder {
    @Override
    public String resolve(PlayerData playerData) {
        return String.valueOf(playerData.getLives());
    }
}