package com.github.m5rian.windGfxBot.commands.leveling;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.database.Database;
import com.github.m5rian.windGfxBot.database.DbMember;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Rank implements CommandHandler {
    @CommandEvent(
            name = "rank"
    )
    public void execute(CommandContext ctx) throws Exception {
        if (ctx.getArguments().length != 0) return;
        final JSONArray file = Database.get("members"); // Get members

        List<DbMember> dbMembers = new ArrayList<>();
        for (int i = 0; i < file.length(); i++) {
            final JSONObject memberDoc = file.getJSONObject(i);

            final long id = memberDoc.getLong("id"); // Get if of member
            final int xp = memberDoc.getInt("xp"); // Get earned experience of member
            final int level = memberDoc.getInt("level"); // Get level of member

            final DbMember dbMember = new DbMember(id, xp, level);
            dbMembers.add(dbMember);
        }

        Collections.sort(dbMembers, Comparator.comparing(DbMember::getXp).reversed());

        for (int i = 0; i < dbMembers.size(); i++) {
            final DbMember dbMember = dbMembers.get(i); // Current member
            if (dbMember.getId() != ctx.getMember().getIdLong()) continue;

            EmbedBuilder rank = new EmbedBuilder()
                    .setTitle("Rank")
                    .setColor(ctx.getMember().getColor())
                    .setThumbnail(ctx.getAuthor().getEffectiveAvatarUrl())
                    .addField("Rank", String.format("`%s`", i + 1), false)
                    .addField("Level", String.format("`%s`", dbMember.getLevel()), true)
                    .addField("Xp", String.format("`%s`", dbMember.getXp()), true);
            ctx.getChannel().sendMessage(rank.build()).queue();
        }
    }
}
