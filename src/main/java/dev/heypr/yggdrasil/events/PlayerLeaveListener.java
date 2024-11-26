package dev.heypr.yggdrasil.events;

import dev.heypr.yggdrasil.Yggdrasil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;


public class PlayerLeaveListener implements Listener {

    private final Yggdrasil plugin;

    public PlayerLeaveListener(Yggdrasil plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("all")
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getPlayerData().containsKey(player.getUniqueId())) {
            plugin.getServer().getOperators().forEach((op) -> {
                if (!(op.isOnline())) return;
                op.getPlayer().sendMessage(ChatColor.DARK_RED + "[URGENT] Player " + player.getName() + " does not have game data. They will be treated as a dead player. Add them to the list of players using /addplayer <player>");
                return;
            });
            return;
        }

//        NamespacedKey livesKey = new NamespacedKey(plugin, "lives");
//        NamespacedKey boogeymanKey = new NamespacedKey(plugin, "boogeyman");
//
//        player.getPersistentDataContainer().set(livesKey, PersistentDataType.INTEGER, plugin.getPlayerData().get(player.getUniqueId()).getLives());
//        player.getPersistentDataContainer().set(boogeymanKey, PersistentDataType.BOOLEAN, plugin.getPlayerData().get(player.getUniqueId()).isBoogeyman());
    }
}
