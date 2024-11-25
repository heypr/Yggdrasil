package dev.heypr.yggdrasil.events;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private final Yggdrasil plugin;

    public PlayerDeathListener(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!plugin.getPlayerData().containsKey(player.getUniqueId())) {
            plugin.getServer().getOperators().forEach((op) -> {
                if (!(op.isOnline())) return;
                op.getPlayer().sendMessage(ChatColor.DARK_RED + "[URGENT] Player " + player.getName() + " does not have game data. They will be treated as a dead player. Add them to the list of players using /addplayer <player>");
                return;
            });
            return;
        }

        UUID uuid = player.getUniqueId();
        PlayerData data = plugin.getPlayerData().get(uuid);
        if (data.getLives() == 0) return;
        data.decreaseLives(1);
        player.sendActionBar(Component.text("Lives: " + data.getLives()));
        Component lives = Component.text(" (" + plugin.getPlayerData().get(player.getUniqueId()).getLives() + " lives)").decoration(TextDecoration.ITALIC, false).color(TextColor.color(128, 128, 128));
        player.playerListName(player.name().append(lives));
        switch (data.getLives()) {
            case 5, 6:
                player.playerListName(player.name().color(TextColor.color(0, 170, 0)).append(lives));
                break;
            case 3, 4:
                player.playerListName(player.name().color(TextColor.color(85, 255, 85)).append(lives));
                break;
            case 2:
                player.playerListName(player.name().color(TextColor.color(255, 255, 85)).append(lives));
                break;
            case 1:
                player.playerListName(player.name().color(TextColor.color(255, 85, 85)).append(lives));
                break;
            case 0:
                player.playerListName(player.name().color(TextColor.color(170, 170, 170)).append(lives));

                player.setGameMode(GameMode.ADVENTURE);

                player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, PotionEffect.INFINITE_DURATION, 500));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, PotionEffect.INFINITE_DURATION, 500));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PotionEffect.INFINITE_DURATION, 500));
                break;
        }
    }
}
