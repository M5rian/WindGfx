package com.github.m5rian.windGfxBot.commands.automod;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordInvites {

    private final String urlPattern = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    public void onMessage(GuildMessageReceivedEvent event) {
        final String[] args = event.getMessage().getContentRaw().split("\\s+");

        for (String arg : args) {
            // An argument is a url
            if (Message.INVITE_PATTERN.asMatchPredicate().test(arg)) event.getMessage().delete().queue();

        }
    }

}
