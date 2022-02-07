package com.github.m5rian.windGfxBot.commands.administrator;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.utils.Config;
import com.github.m5rian.windGfxBot.utils.Permissions.Administrator;
import org.json.JSONObject;

public class Prefix implements CommandHandler {

    @CommandEvent(
            name = "prefix",
            requires = Administrator.class
    )
    public void execute(CommandContext ctx) throws Exception {
        // Command usage
        if (ctx.getArguments().length != 1) {
            ctx.getChannel().sendMessage(String.format("```asciidoc%n[Missing arguments]%n= %sprefix <new prefix>```", Config.get().getString("prefix"))).queue();
            return;
        }

        final String prefix = ctx.getArguments()[0]; // Get new prefix
        final JSONObject config = Config.get(); // Get config file
        config.put("prefix", prefix); // Update prefix
        Config.update(config); // Update config file
        ctx.getChannel().sendMessage(String.format("```cs%n'Prefix changed!'%n```%n > **Prefix changed to `%s`**", prefix)).queue();
    }
}
