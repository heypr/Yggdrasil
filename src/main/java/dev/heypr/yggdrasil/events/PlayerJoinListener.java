package dev.heypr.yggdrasil.events;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

public class PlayerJoinListener implements Listener {

    private final Yggdrasil plugin;

    public PlayerJoinListener(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isSessionRunning) {
            player.sendTitle(ChatColor.RED + "Game not started", "Please wait for the game to start", 10, 70, 20);
            return;
        }
        if (!plugin.getPlayerData().containsKey(player.getUniqueId())) {
            player.sendTitle(ChatColor.RED + "Game in progress", ChatColor.RED + "You are not part of the game", 10, 70, 20);
            player.sendMessage(ChatColor.GREEN + "Game in progress. Request an admin to add you to the game using /addplayer <player>");

            plugin.getServer().getOperators().forEach((op) -> {
                if (!(op.isOnline())) return;
                op.getPlayer().sendMessage(ChatColor.DARK_RED + "[URGENT] Player " + player.getName() + " does not have game data. They will be treated as a dead player. Add them to the list of players using /addplayer <player>");
                return;
            });
            return;
        }

        final int lives = plugin.getPlayerData().get(player.getUniqueId()).getLives();
        final File skinFile = Colors.getSkinFile(plugin, player, lives);

        if (skinFile != null)
            plugin.skinsManager.skin(player, skinFile);

        player.sendActionBar(Component.text("Lives: " + lives));
        Component livesComp = Component.text(" (" + plugin.getPlayerData().get(player.getUniqueId()).getLives() + " lives)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(128, 128, 128));

        player.playerListName(player.name().append(livesComp));

        plugin.getScheduler().runTaskLater(plugin, () -> player.playerListName(player.name().append(livesComp)), 20L); // To fix it incase the skin thing removes it

        switch (plugin.getPlayerData().get(player.getUniqueId()).getLives()) {
            case 5, 6:
                player.playerListName(player.name().color(TextColor.color(0, 170, 0)).append(livesComp));
                break;
            case 3, 4:
                player.playerListName(player.name().color(TextColor.color(85, 255, 85)).append(livesComp));
                break;
            case 2:
                player.playerListName(player.name().color(TextColor.color(255, 255, 85)).append(livesComp));
                break;
            case 1:
                player.playerListName(player.name().color(TextColor.color(255, 85, 85)).append(livesComp));
                break;
            case 0:
                player.playerListName(player.name().color(TextColor.color(170, 170, 170)).append(livesComp));

                player.setGameMode(GameMode.ADVENTURE);

                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 500));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 500));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 500));
                break;
        }
    }
}
