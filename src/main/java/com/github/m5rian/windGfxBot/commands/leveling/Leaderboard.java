package com.github.m5rian.windGfxBot.commands.leveling;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.database.Database;
import com.github.m5rian.windGfxBot.database.DbMember;
import com.github.m5rian.windGfxBot.utils.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Leaderboard implements CommandHandler {

    @CommandEvent(
            name = "leaderboard",
            aliases = {"lb", "top"}
    )
    public void execute(CommandContext ctx) throws Exception {
        final JSONArray file = Database.get("members"); // Get members

        final List<DbMember> members = new ArrayList<>();

        for (int i = 0; i < file.length(); i++) {
            final JSONObject memberDoc = file.getJSONObject(i);

            final long id = memberDoc.getLong("id"); // Get if of member
            final int xp = memberDoc.getInt("xp"); // Get earned experience of member
            final int level = memberDoc.getInt("level"); // Get level of member

            final DbMember member = new DbMember(id, xp, level);
            members.add(member); // Add member to list
        }

        Collections.sort(members, Comparator.comparing(DbMember::getXp).reversed()); // Sort list

        final EmbedBuilder embed = new EmbedBuilder()
                .setTitle(ctx.getGuild().getName() + "'s leaderboard")
                .setThumbnail(ctx.getGuild().getIconUrl())
                .setColor(Config.colour);
        // Print out top 10 users
        for (int i = 0; i < members.size(); i++) {
            if (i > 10) break; // Only show top 10 people
            final DbMember member = members.get(i); // Get next member

            final String mention = "<@" + member.getId() + ">"; // Get member as mention
            final int level = member.getLevel(); // Get level

            embed.appendDescription(String.format("`#%s` - %s - **%s**%n", i + 1, mention, level));
        }
        ctx.getChannel().sendMessage(embed.build()).queue();

        // Clean members
        members.forEach(originalMember -> ctx.getGuild().retrieveMemberById(originalMember.getId()).queue(
                success -> {
                    // Ignore success
                },
                error -> {
                    final JSONArray dbMembers = Database.get("members");
                    for (int i = 0; i < dbMembers.length(); i++) {
                        final JSONObject dbMember = dbMembers.getJSONObject(i); // Get current member
                        // Found matching member
                        if (dbMember.getLong("id") == originalMember.getId()) {
                            dbMembers.remove(i); // Remove current entry
                            Database.update("members", dbMembers); // Update database
                        }
                    }
                }
        ));

    }

    /*

            List<DbMember> dbMembers = new ArrayList<>();
        Map<Long, Member> members = new HashMap<>();
        AtomicInteger passedMembers = new AtomicInteger(0);
        for (int i = 0; i < file.length(); i++) {
            final JSONObject memberDoc = file.getJSONObject(i);

            final long id = memberDoc.getLong("id"); // Get if of member
            final int xp = memberDoc.getInt("xp"); // Get earned experience of member
            final int level = memberDoc.getInt("level"); // Get level of member

            // Retrieve member
            int finalI = i;
            ctx.getGuild().retrieveMemberById(id).queue(
                    // Member is still in the guild
                    m -> {
                        // Create DbMember out of it
                        final DbMember member = new DbMember(id, xp, level);
                        dbMembers.add(member); // Add com.github.m5rian.windGfxBot.database member to list
                        members.put(m.getIdLong(), m); // Add member to list
                        passedMembers.getAndAdd(1);
                        drawLeaderboard(file, dbMembers, members, file, ctx.getEvent(), passedMembers.get());
                    },
                    // Member isn't in the guild anymore
                    e -> {
                        file.remove(finalI); // Remove member from com.github.m5rian.windGfxBot.database
                        passedMembers.getAndAdd(1);
                        drawLeaderboard(file, dbMembers, members, file, ctx.getEvent(), passedMembers.get());
                    }
            );
        }

    }

    private void drawLeaderboard(JSONArray membersRaw, List<DbMember> dbMembers, Map<Long, Member> members, JSONArray file, MessageReceivedEvent event, int passedMembers) {
        if (membersRaw.length() != passedMembers) return;

        Database.update("members", file); // Sync com.github.m5rian.windGfxBot.database

        Collections.sort(dbMembers, Comparator.comparing(DbMember::getXp).reversed()); // Sort list

        final String guild = event.getGuild().getName(); // Get name of server
        final String icon = event.getGuild().getIconUrl(); // Get url of server icon
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(guild + "'s leaderboard")
                .setThumbnail(icon)
                .setColor(Config.colour);
        // Print out top 10 users
        for (int i = 0; i < members.size(); i++) {
            if (i > 10) break; // No more members in com.github.m5rian.windGfxBot.database
            final DbMember member = dbMembers.get(i); // Get next member

            final String mention = members.get(member.getId()).getAsMention(); // Get member as mention
            final int level = member.getLevel(); // Get level

            embed.appendDescription(String.format("`#%s` - %s - **%s**%n", i + 1, mention, level));
        }
        event.getChannel().sendMessage(embed.build()).queue();
    }

     */

}
