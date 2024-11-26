package dev.heypr.yggdrasil.misc.discord;


import dev.heypr.yggdrasil.misc.discord.command.ResponseType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.awt.*;
import java.util.function.Consumer;

public final class BotUtils {
    public static void sendEmbed(final TextChannel channel, final EmbedBuilder builder) {
        BotUtils.sendEmbed(channel, builder, null);
    }

    public static void sendEmbed(final TextChannel channel, final EmbedBuilder builder, final Consumer<MessageCreateAction> callback) {
        final MessageCreateAction action = channel.sendMessageEmbeds(builder.build());

        if (callback != null)
            callback.accept(action);

        action.queue();
        builder.clear();
    }

    public static MessageEmbed getEmbed(final ResponseType type, final String msg) {
        final EmbedBuilder builder = BotUtils.getEmbed(type.getTitle(), msg, type.getColor());
        final MessageEmbed embed = builder.build();

        builder.clear();
        return embed;
    }

    public static EmbedBuilder getEmbed(final String title, final String titleLink, final String description, final String[][] fields, final String footer,
                                        final Color color) {
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title, titleLink);
        builder.setDescription(description);
        for (final String[] field : fields)
            builder.addField(field[0], field[1], true);
        builder.setFooter(footer.isEmpty() || footer == null ? "" : footer);
        builder.setColor(color);
        return builder;
    }

    public static EmbedBuilder getEmbed(final String title, final String description, final Color color) {
        return BotUtils.getEmbed(title, "", description, new String[][] {}, "", color);
    }

    public static EmbedBuilder getEmbed(final String title, final String[][] fields, final Color color) {
        return BotUtils.getEmbed(title, "", "", fields, "", color);
    }

    public static TextChannel channelToTextChannel(final Channel channel) {
        return Bot.bot.getTextChannelById(channel.getId());
    }

    public static boolean hasRole(final Member member, final String roleName) {
        return member.getRoles().stream().filter(role -> role.getName().equals(roleName)).findFirst()
                .orElse(null) != null;
    }

    public static boolean hasPermission(final Member member, final Permission permission) {
        return member.hasPermission(permission);
    }
}