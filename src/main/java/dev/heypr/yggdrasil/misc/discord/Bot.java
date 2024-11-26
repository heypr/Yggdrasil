package dev.heypr.yggdrasil.misc.discord;

import dev.heypr.yggdrasil.Yggdrasil;
import dev.heypr.yggdrasil.misc.discord.command.CommandManager;
import dev.heypr.yggdrasil.misc.discord.listeners.EventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.Arrays;

public final class Bot {
    private final Yggdrasil plugin;

    public static JDA bot;

    public Bot(final Yggdrasil plugin, final String token) {
        this.plugin = plugin;

        if (token == null || token.isEmpty())
            return;

        try {
            CommandManager.registerCommands();

            final JDABuilder jda = JDABuilder.createDefault(token);

            jda.setChunkingFilter(ChunkingFilter.ALL);
            jda.setMemberCachePolicy(MemberCachePolicy.ALL);
            jda.enableIntents(Arrays.asList(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT));
            jda.setStatus(OnlineStatus.ONLINE);

            jda.addEventListeners(new EventListener());
            bot = jda.build();
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }
}