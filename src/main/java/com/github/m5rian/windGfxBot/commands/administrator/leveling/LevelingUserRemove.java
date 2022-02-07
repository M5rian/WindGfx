package com.github.m5rian.windGfxBot.commands.administrator.leveling;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.database.Database;
import com.github.m5rian.windGfxBot.listeners.Leveling;
import com.github.m5rian.windGfxBot.utils.Config;
import com.github.m5rian.windGfxBot.utils.Permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class LevelingUserRemove implements CommandHandler {

    @CommandEvent(
            name = "leveling user remove",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 2) {
            ctx.getChannel().sendMessage(String.format("```asciidoc%n[Missing arguments]%n= %sleveling user remove <member> <levels>```", Config.get().getString("prefix"))).queue();
            return;
        }

        final List<Member> mentionedMembers = ctx.getEvent().getMessage().getMentionedMembers(); // Get mentioned members
        // No member mentioned
        if (mentionedMembers.isEmpty()) {
            EmbedBuilder error = new EmbedBuilder()
                    .setTitle("No member mentioned")
                    .setColor(0xFF5555);
            ctx.getChannel().sendMessage(error.build()).queue();
        }

        // Second argument is not a digit
        if (!ctx.getArguments()[1].matches("\\d+")) {
            EmbedBuilder error = new EmbedBuilder()
                    .setTitle("The leveling is not a digit")
                    .setColor(0xFF5555);
            ctx.getChannel().sendMessage(error.build()).queue();
        }

        final JSONArray members = Database.get("members"); // Get members from the com.github.m5rian.windGfxBot.database
        for (int i = 0; i < members.length(); i++) {
            final JSONObject jsonMember = members.getJSONObject(i); // Get current JSONObject

            final long memberId = jsonMember.getLong("id"); // Get id
            if (memberId != mentionedMembers.get(0).getIdLong()) continue; // Wrong member

            final int levelToRemove = Integer.parseInt(ctx.getArguments()[1]); // Get level to remove
            final int xpToRemove = Leveling.getXp(levelToRemove); // Calculate the xp to remove

            final int newLevel = Leveling.getLevel(jsonMember.getLong("xp") - xpToRemove);
            final long newXp = jsonMember.getLong("xp") - xpToRemove;

            jsonMember.put("xp", newXp);
            jsonMember.put("level", newLevel);

            Database.update("members", members);

            ctx.getEvent().getMessage().addReaction("\\\u2714").queue();
        }

    }
}
