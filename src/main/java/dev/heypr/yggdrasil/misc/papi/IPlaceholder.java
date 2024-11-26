package dev.heypr.yggdrasil.misc.papi;

import dev.heypr.yggdrasil.data.PlayerData;

public interface IPlaceholder {
    String resolve(final PlayerData playerData);

    // What it returns when the player data could not be found
    default String nullPlayerData() {
        return "";
    }
}