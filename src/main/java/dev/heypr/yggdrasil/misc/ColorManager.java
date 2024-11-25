package dev.heypr.yggdrasil.misc;

import dev.heypr.yggdrasil.Yggdrasil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public final class ColorManager {
    public enum Colors {
        DARK_GREEN(TextColor.color(0, 170, 0)),
        GREEN(TextColor.color(85, 255, 85)),
        YELLOW(TextColor.color(255, 255, 85)),
        RED(TextColor.color(255, 85, 85)),
        GRAY(TextColor.color(170, 170, 170));

        private final TextColor rgb;

        Colors(final TextColor rgb) {
            this.rgb = rgb;
        }

        public TextColor getRgb() {
            return this.rgb;
        }

        public static Colors from(final int lives) {
            switch (lives) {
                case 5, 6:
                    return Colors.DARK_GREEN;
                case 3, 4:
                    return Colors.GREEN;
                case 2:
                    return Colors.YELLOW;
                case 1:
                    return Colors.RED;
                case 0:
                    return Colors.GRAY;
            }

            return null;
        }
    }

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

        final Colors colors = Colors.from(lives);
        final String colorName = colors.name().toLowerCase().replace("dark_", "");
        final File skinFile = new File(userFolder, colorName + ".png");

        if (skinFile == null || !skinFile.exists())
            return null;

        return skinFile;
    }

    public static void setTabListName(final Yggdrasil plugin, final Player player, final int lives) {
        final Component livesComp = Component.text(" (" + plugin.getPlayerData().get(player.getUniqueId()).getLives() + " lives)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(128, 128, 128));
        final ColorManager.Colors colors = ColorManager.Colors.from(lives);

        player.playerListName(player.name().color(colors.getRgb()).append(livesComp));
    }
}