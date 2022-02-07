package com.github.m5rian.windGfxBot.listeners;

import com.github.m5rian.windGfxBot.commands.automod.DiscordInvites;
import com.github.m5rian.windGfxBot.utils.Config;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class EventsListener extends ListenerAdapter {
    private final ReactionRoles reactionRoles = new ReactionRoles();
    private final Leveling leveling = new Leveling();
    private final VoiceCall voiceCall = new VoiceCall();
    private final Welcome welcome = new Welcome();

    private final long guildId = Config.get().getLong("guildId");

    //JDA Events
    public void onReady(@Nonnull ReadyEvent event) {
        try {
            new Unbans().onReady(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Message Events
    //Guild (TextChannel) Message Events
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        try {
            new DiscordInvites().onMessage(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        try {
            reactionRoles.onReactionAdd(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onGuildMessageReactionRemove(@Nonnull GuildMessageReactionRemoveEvent event) {
        try {
            reactionRoles.onReactionRemove(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Combined Message Events (Combines Guild and Private message into 1 event)
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        try {
            if (event.getAuthor().isBot()) return;
            if (event.getGuild().getIdLong() != guildId) return;

            leveling.onMessage(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Guild Member Events
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        try {
            if (event.getGuild().getIdLong() != guildId) return;

            welcome.onJoin(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //Guild Voice Events
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        try {
            if (event.getGuild().getIdLong() != guildId) return;

            voiceCall.onJoin(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        try {
            if (event.getGuild().getIdLong() != guildId) return;

            voiceCall.onMove(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        try {
            if (event.getGuild().getIdLong() != guildId) return;

            voiceCall.onLeave(event);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
