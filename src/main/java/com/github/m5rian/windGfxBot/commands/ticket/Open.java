package com.github.m5rian.windGfxBot.commands.ticket;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.utils.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.json.JSONArray;

import java.util.EnumSet;

public class Open implements CommandHandler {
    @CommandEvent(
            name = "open",
            aliases = {"ticket"}
    )
    public void execute(CommandContext ctx) throws Exception {
        // Get ticket category
        final long ticketsId = Config.get().getJSONObject("categories").getLong("tickets");
        final Category ticketCategory = ctx.getGuild().getCategoryById(ticketsId);
        // Get ids of all staff roles
        final JSONArray staffIds = Config.get().getJSONObject("roles").getJSONArray("staff");

        final EnumSet<Permission> read = EnumSet.of(Permission.MESSAGE_READ);
        final EnumSet<Permission> nothing = EnumSet.noneOf(Permission.class);

        ChannelAction<TextChannel> channelAction = ticketCategory.createTextChannel(ctx.getAuthor().getName())
                // Deny access for @everyone
                .addRolePermissionOverride(
                        ctx.getGuild().getPublicRole().getIdLong(),
                        nothing,
                        read
                )
                // Allow access for ticket author
                .addMemberPermissionOverride(
                        ctx.getMember().getIdLong(),
                        read,
                        nothing
                );
        for (int i = 0; i < staffIds.length(); i++) {
            final long staffId = staffIds.getLong(i);
            channelAction = channelAction.addRolePermissionOverride(staffId, read, nothing);
        }
        channelAction.queue();

        ctx.getEvent().getMessage().addReaction("\u2714").queue();
    }
}
