package dev.heypr.yggdrasil.misc;

import dev.heypr.yggdrasil.Yggdrasil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public final class Colors {
    public static ChatColor getColor(int lives) {
        switch (lives) {
            case 2:
                return ChatColor.YELLOW;
            case 4, 3:
                return ChatColor.GREEN;
            case 6, 5:
                return ChatColor.DARK_GREEN;
        }

        return null;
    }

    public static File getSkinFile(final Yggdrasil plugin, Player player, final int lives) {
        final UUID uuid = player.getUniqueId();
        final File dataFolder = plugin.getDataFolder();
        final File userFolder = new File(dataFolder, uuid.toString());

        if (!userFolder.exists() || !userFolder.isDirectory())
            return null;

        final ChatColor color = getColor(lives);
        final String colorName = color.name().toLowerCase().replace("dark_", "");
        final File skinFile = new File(userFolder, colorName + ".png");

        if (skinFile == null || !skinFile.exists())
            return null;

        return skinFile;
    }
}