package com.github.m5rian.windGfxBot.commands.administrator;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.utils.Config;
import com.github.m5rian.windGfxBot.utils.Permissions.Administrator;
import net.dv8tion.jda.api.EmbedBuilder;

public class Embed implements CommandHandler {

    @CommandEvent(
            name = "embed",
            requires = Administrator.class
    )
    public void onCommand(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length == 0) {
            ctx.getChannel().sendMessage(String.format("```asciidoc%n[Missing arguments]%n= %sembed <description>```", Config.get().getString("prefix"))).queue();
            return;
        }

        final EmbedBuilder embed = new EmbedBuilder()
                .setColor(Config.colour)
                .setDescription(ctx.getArgumentsRaw());
        ctx.getChannel().sendMessageEmbeds(embed.build()).queue();
    }
}
