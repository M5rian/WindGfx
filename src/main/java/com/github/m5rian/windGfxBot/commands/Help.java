package com.github.m5rian.windGfxBot.commands;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.jdaCommandHandler.exceptions.NotRegisteredException;
import com.github.m5rian.windGfxBot.WindGfx;
import com.github.m5rian.windGfxBot.utils.Config;
import com.github.m5rian.windGfxBot.utils.Permissions.Administrator;
import com.github.m5rian.windGfxBot.utils.Permissions.Staff;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Arrays;

public class Help implements CommandHandler {

    @CommandEvent(
            name = "help",
            aliases = {"commands"}
    )
    public void onHelpCommand(CommandContext ctx) throws NotRegisteredException {
        final String[] memberCommands = {"`help`", "`rank`", "`leaderboard`", "`open`", "`close`"};
        final String[] moderatorCommands = {"`ban`", "`mute`"};
        final String[] adminCommands = {"`prefix`", "`embed`", "`leveling user remove`"};

        final StringBuilder members = new StringBuilder();
        Arrays.stream(memberCommands).forEachOrdered(command -> members.append(command).append(" "));

        final StringBuilder moderators = new StringBuilder();
        Arrays.stream(moderatorCommands).forEachOrdered(command -> moderators.append(command).append(" "));

        final StringBuilder administrators = new StringBuilder();
        Arrays.stream(adminCommands).forEachOrdered(command -> administrators.append(command).append(" "));

        EmbedBuilder helpMenu = new EmbedBuilder()
                .setTitle("help")
                .setColor(Config.colour)
                .setThumbnail(ctx.getGuild().getIconUrl())
                .setDescription("Here is a list with all available commands:")
                .addField("members", members.toString(), false);
        // Moderator
        if (WindGfx.commandService.hasPermissions(ctx.getMember(), Staff.class)) {
            helpMenu.addField("moderators", moderators.toString(), false);
        }
        // Administrator
        if (WindGfx.commandService.hasPermissions(ctx.getMember(), Administrator.class)) {
            helpMenu.addField("administrators", administrators.toString(), false);
        }

        ctx.getChannel().sendMessage(helpMenu.build()).queue();
    }

}
