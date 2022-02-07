package com.github.m5rian.windGfxBot.commands.ticket;

import com.github.m5rian.jdaCommandHandler.Channel;
import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.utils.Config;
import net.dv8tion.jda.api.entities.TextChannel;

public class Close implements CommandHandler {
    @CommandEvent(
            name = "close",
            channel = Channel.GUILD
    )
    public void execute(CommandContext ctx) throws Exception {
        final TextChannel channel = (TextChannel) ctx.getChannel();
        final long ticketsId = Config.get().getJSONObject("categories").getLong("tickets");

        if (ticketsId == channel.getParent().getIdLong()) {
            channel.delete().queue();
        }
    }
}
