package com.github.m5rian.windGfxBot;

import com.github.m5rian.jdaCommandHandler.CommandListener;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandMessageFactory;
import com.github.m5rian.jdaCommandHandler.commandMessages.CommandUsageFactory;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandService;
import com.github.m5rian.jdaCommandHandler.commandServices.DefaultCommandServiceBuilder;
import com.github.m5rian.windGfxBot.commands.Help;
import com.github.m5rian.windGfxBot.commands.administrator.Embed;
import com.github.m5rian.windGfxBot.commands.administrator.Prefix;
import com.github.m5rian.windGfxBot.commands.administrator.leveling.LevelingUserRemove;
import com.github.m5rian.windGfxBot.commands.leveling.Leaderboard;
import com.github.m5rian.windGfxBot.commands.leveling.Rank;
import com.github.m5rian.windGfxBot.commands.moderation.Ban;
import com.github.m5rian.windGfxBot.commands.moderation.Mute;
import com.github.m5rian.windGfxBot.commands.ticket.Close;
import com.github.m5rian.windGfxBot.commands.ticket.Open;
import com.github.m5rian.windGfxBot.database.Database;
import com.github.m5rian.windGfxBot.listeners.EventsListener;
import com.github.m5rian.windGfxBot.utils.Config;
import com.github.m5rian.windGfxBot.utils.Permissions.Administrator;
import com.github.m5rian.windGfxBot.utils.Permissions.Owner;
import com.github.m5rian.windGfxBot.utils.Permissions.Staff;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WindGfx {
    public static void main(String[] args) throws LoginException, IOException {
        new WindGfx();
    }

    public static DefaultCommandService commandService = new DefaultCommandServiceBuilder()
            .setDefaultPrefix(Config.get().getString("prefix")) // Set a default prefix, which is used in dms
            .setVariablePrefix(guild -> Config.get().getString("prefix")) // Set a variable prefix, which you can change
            .setInfoFactory(new CommandMessageFactory()
                    .setAuthorAvatar(ctx -> ctx.getAuthor().getEffectiveAvatarUrl())
                    .setAuthor(ctx -> ctx.getCommand().name())
                    .setHyperLink(ctx -> "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
            .setUsageFactory(new CommandUsageFactory()
                    .setDefaultMessage(ctx -> "Aliases are:")
                    .addUsageAsText((ctx, info) -> String.join(",", info.aliases())))
            .build();
    public static JDA jda;

    public WindGfx() throws LoginException, IOException {
        checkFolder();

        jda = JDABuilder.createDefault(Config.get().getString("token"))
                .addEventListeners(
                        new CommandListener(commandService),
                        new EventsListener())
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();

        // Register all permissions
        commandService.registerPermission(
                new Owner(),
                new Administrator(),
                new Staff()
        );

        changeActivity(jda);
        registerCommands(commandService); //  commands
    }


    private void changeActivity(JDA jda) {
        final ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);

        timer.scheduleAtFixedRate(() -> {
            final JSONObject activities = Config.get().getJSONObject("activities"); // Get all activities

            final JSONArray playing = activities.getJSONArray("playing"); // Get playing texts

            final int random = new Random().nextInt(playing.length()); // Get random number
            final Activity activity = Activity.playing(playing.getString(random)); // Create activity
            jda.getPresence().setActivity(activity);
        }, 0, 15, TimeUnit.MINUTES);
    }

    private void registerCommands(DefaultCommandService commandService) {
        commandService.registerCommandClasses(
                new Help(),
                // Administrator
                new Prefix(),
                new Embed(),
                new LevelingUserRemove(),
                // Moderation
                new Mute(),
                new Ban(),
                // Leveling
                new Rank(),
                new Leaderboard(),
                // Ticket system
                new Open(),
                new Close(),

                new com.github.m5rian.windGfxBot.commands.Rank()
        );
    }

    private void checkFolder() throws IOException {
        final File folder = new File(Database.path);
        if (!folder.exists()) { // Check if folder exists
            folder.mkdirs(); // Create folder

            final InputStream membersInputStream = WindGfx.class.getResourceAsStream("members.json"); // Get member file
            final File membersFile = new File(folder.getPath() + File.separator + "members.json"); // Get new file path
            FileUtils.copyInputStreamToFile(membersInputStream, membersFile); // Copy member file

            final InputStream bansInputStream = WindGfx.class.getResourceAsStream("bans.json"); // Get member file
            final File bansFile = new File(folder.getPath() + File.separator + "bans.json"); // Get new file path
            FileUtils.copyInputStreamToFile(bansInputStream, bansFile); // Copy member file
        }
    }
}