package com.github.m5rian.windGfxBot.listeners;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import com.github.m5rian.windGfxBot.utils.Config;

public class VoiceCall {
    private final long voiceCallId = Config.get().getJSONObject("categories").getLong("voiceCall");

    public void onJoin(GuildVoiceJoinEvent event) {
        // Voice channel needs to be the lobby
        if (event.getChannelJoined().getIdLong() == voiceCallId) {
            final String name = event.getMember().getEffectiveName(); // Get name of member

            // Create voice channel
            event.getChannelJoined().getParent().createVoiceChannel(name).queue(voiceChannel -> {
                event.getGuild().moveVoiceMember(event.getMember(), voiceChannel).queue(); // Move member
            });
        }
    }

    public void onLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel leftChannel = event.getChannelLeft(); // Get left voice channel
        VoiceChannel lobby = event.getGuild().getVoiceChannelById(voiceCallId); // Get lobby voice channel

        // Left voice channel is not in the category of the lobby voice channel
        if (lobby.getParent() != leftChannel.getParent()) return;
        // Left voice channel is not the lobby
        if (leftChannel.getIdLong() != lobby.getIdLong()) {
            // Voice channel is empty
            if (leftChannel.getMembers().size() == 0) {
                leftChannel.delete().queue(); // Delete voice channel
            }
        }
    }

    public void onMove(GuildVoiceMoveEvent event) {
        VoiceChannel leftChannel = event.getChannelLeft(); // Get left voice channel
        VoiceChannel lobby = event.getGuild().getVoiceChannelById(voiceCallId); // Get lobby voice channel

        // Left voice channel is not in the category of the lobby voice channel
        if (lobby.getParent() != leftChannel.getParent()) return;
        // Left voice channel is not the lobby
        if (leftChannel.getIdLong() != lobby.getIdLong()) {
            // Voice channel is empty
            if (leftChannel.getMembers().size() == 0) {
                leftChannel.delete().queue(); // Delete voice channel
            }
        }
    }
}
