package com.github.m5rian.windGfxBot.commands.moderation;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.database.Database;
import com.github.m5rian.windGfxBot.database.Documents;
import com.github.m5rian.windGfxBot.utils.Config;
import com.github.m5rian.windGfxBot.utils.Permissions.Staff;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Ban implements CommandHandler {
    private final ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);

    @CommandEvent(
            name = "ban",
            requires = Staff.class
    )
    public void execute(CommandContext ctx) throws Exception {
        final JSONArray banCases = Config.get().getJSONArray("banCases"); // Get all ban cases

        // Ban cases
        if (ctx.getArguments().length == 0) {
            final StringBuilder cases = new StringBuilder(">>> ");

            for (int i = 0; i < banCases.length(); i++) {
                final JSONObject banCase = banCases.getJSONObject(i); // Get current ban case

                final String title = banCase.getString("title"); // Get title of ban case
                final long durationInMillis = banCase.getLong("durationInMillis"); // Get ban duration
                String duration = formatMillis(durationInMillis); // Format milliseconds nicely
                if (durationInMillis == 0) duration = "permanent"; // Ban is permanent
                String description = banCase.getString("description"); // Get description of ban case
                if (!description.equals("")) description += "\n"; // Add a new line if the description has content

                cases.append(String.format("**%s** for `%s`%n%s%n", title, duration, description));
            }

            final EmbedBuilder usage = new EmbedBuilder()
                    .setTitle(ctx.getGuild().getName() + "'s ban cases")
                    .setColor(Config.colour)
                    .setDescription(cases);
            ctx.getChannel().sendMessage(usage.build()).queue();
            return;
        }


        final List<Member> members = ctx.getEvent().getMessage().getMentionedMembers(); // Get mentioned members
        if (members.size() == 0) {
            final EmbedBuilder error = new EmbedBuilder().setTitle("No member mentioned");
            ctx.getChannel().sendMessage(error.build()).queue();
            return;
        }
        final Member member = members.get(0); // Get first member

        if (ctx.getArguments().length < 2) {
            final EmbedBuilder error = new EmbedBuilder().setTitle("No case specified");
            ctx.getChannel().sendMessage(error.build()).queue();
            return;
        }

        final String providedCase = ctx.getArgumentsRaw().split("\\s+", 2)[1]; // Remove member from arguments
        for (int i = 0; i < banCases.length(); i++) {
            final JSONObject banCase = banCases.getJSONObject(i); // Get current ban case

            final String title = banCase.getString("title"); // Get title of ban case

            // Ban case is called
            if (providedCase.equalsIgnoreCase(title)) {
                final long durationInMillis = banCase.getLong("durationInMillis"); // Get mute duration
                //final String description = banCase.getString("description"); // Get description of mute case

                final Role muteRole = ctx.getGuild().getRoleById(Config.get().getJSONObject("roles").getLong("muted")); // Get mute role
                ctx.getGuild().addRoleToMember(member, muteRole).queue(); // Add muted role
                if (durationInMillis == 0L) return; // Ban is permanent

                final long unmuteTimeInMillis = System.currentTimeMillis() + durationInMillis; // Get unmute time in milliseconds
                final JSONObject mute = new Documents().mute(member, title, unmuteTimeInMillis); // Create mute JSONObject
                final JSONArray mutes = Database.get("mutes"); // Get mutes
                mutes.put(mute); // Add ban to bans
                Database.update("mutes", mutes); // Update database

                timer.schedule(() -> {
                    ctx.getGuild().unban(String.valueOf(mute.getLong("id"))).queue(); // Unban member

                    JSONArray newMutes = Database.get("bans"); // Get current mutes
                    newMutes = remove(newMutes, mute); // Remove mute
                    Database.update("bans", newMutes); // Update database
                }, durationInMillis, TimeUnit.MILLISECONDS);
            }
        }
    }

    private String formatMillis(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
        int days = (int) (millis / (1000 * 60 * 60 * 24));

        return String.format("%sd %sh %sm %ss", days, hours, minutes, seconds);
    }

    private JSONArray remove(JSONArray source, JSONObject object) {
        for (int i = 0; i < source.length(); i++) {
            final JSONObject currentObject = source.getJSONObject(i);
            if (currentObject.getLong("id") == object.getLong("id")) {
                source.remove(i);
            }
        }
        return source;
    }
}
