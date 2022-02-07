package com.github.m5rian.windGfxBot.listeners;

import com.github.m5rian.windGfxBot.database.Database;
import com.github.m5rian.windGfxBot.database.Documents;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import com.github.m5rian.windGfxBot.utils.Config;

import java.util.Random;

public class Leveling {
    private final long levelingChannelId = Config.get().getJSONObject("channels").getLong("leveling");

    public void onMessage(MessageReceivedEvent e) {
        final JSONArray file = Database.get("members"); // Get members document

        // Member already exists in com.github.m5rian.windGfxBot.database
        for (int i = 0; i < file.length(); i++) {
            final JSONObject member = file.getJSONObject(i); // Get current member
            // Member found
            if (member.getLong("id") == e.getMember().getIdLong()) {
                final JSONObject newMember = updateMember(e, member);
                file.remove(i); // Remove old member
                file.put(newMember); // Add updated member
                Database.update("members", file); // Update com.github.m5rian.windGfxBot.database
                return;
            }
        }

        // Add member to com.github.m5rian.windGfxBot.database
        final JSONObject memberObject = new Documents().member(e.getMember()); // Create new member document
        final JSONObject newMember = updateMember(e, memberObject);
        file.put(newMember); // Add member to com.github.m5rian.windGfxBot.database
        Database.update("members", file); // Update com.github.m5rian.windGfxBot.database

    }

    private JSONObject updateMember(MessageReceivedEvent e, JSONObject memberObject) {
        final int xp = new Random().nextInt(3) + 5; // Get random number between 5 and 8

        final int currentXp = memberObject.getInt("xp"); // Get current xp
        final int currentLevel = memberObject.getInt("level"); // Get current level

        final int newXp = currentXp + xp; // Get new xp
        final int newLevel = getLevel(currentXp + xp); // Get new level

        if (newLevel > currentLevel) {
            MessageChannel channel = e.getChannel();
            if (levelingChannelId != 0) channel = e.getGuild().getTextChannelById(levelingChannelId);

            EmbedBuilder levelUp = new EmbedBuilder()
                    .setTitle("\uD83C\uDF2C Level up! \uD83C\uDF2A")
                    .setColor(e.getMember().getColor())
                    .setDescription(String.format("You are now level **%s**!", newLevel));
            channel.sendMessage(e.getMember().getAsMention()).embed(levelUp.build()).queue();
        }

        memberObject.put("xp", newXp);
        memberObject.put("level", newLevel);

        return memberObject;
    }

    public static int getLevel(long xp) {
        // Parabola
        double dividedNumber = xp / 15;
        double power = Math.sqrt(dividedNumber); // take root

        return (int) Math.round(power); // Round
    }


    public static int getXp(int level) {
        // Parabola
        double squaredNumber = Math.pow(level, 2); // Square number
        double multiplied = squaredNumber * 15;

        return (int) Math.round(multiplied); // Round
    }
}
