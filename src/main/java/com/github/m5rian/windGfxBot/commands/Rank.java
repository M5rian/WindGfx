package com.github.m5rian.windGfxBot.commands;

import com.github.m5rian.jdaCommandHandler.CommandHandler;
import com.github.m5rian.jdaCommandHandler.command.CommandContext;
import com.github.m5rian.jdaCommandHandler.command.CommandEvent;
import com.github.m5rian.windGfxBot.utils.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Rank implements CommandHandler {

    @CommandEvent(name = "rank up")
    public void onRankUpCommand(CommandContext ctx) {
        final JSONArray jsonRoles = Config.get().getJSONArray("designer-ranks");
        final List<String> roles = new ArrayList<>();
        for (int i = 0; i < jsonRoles.length(); i++) {
            roles.add(jsonRoles.getString(i));
        }

        if (ctx.getMessage().getMentionedMembers().size() == 0) {
            ctx.getMessage().reply("`!rank up <member>`").queue();
            return;
        }

        final Member member = ctx.getMessage().getMentionedMembers().get(0);

        List<Role> designerRoles = member.getRoles().stream().filter(role -> roles.contains(role.getId())).toList();
        if (designerRoles.size() == 0) {
            final Role role = ctx.getGuild().getRoleById(roles.get(roles.size() - 1));
            ctx.getGuild().addRoleToMember(member, role).queue();
            ctx.getChannel().sendMessage(member.getAsMention() + " reached a new level and is now **" + role.getName() + "**!").queue();
            return;
        }
        designerRoles = designerRoles.subList(0, designerRoles.size());

        final Role currentRole = designerRoles.get(0);
        final Optional<Role> higherRole = ctx.getGuild().getRoles().stream().filter(r -> r.getPosition() == currentRole.getPosition() + 1).findFirst();
        if (higherRole.isEmpty() || !roles.contains(higherRole.get().getId())) {
            ctx.getMessage().reply("You've already reached the best designer rank. Congratulations!").queue();
            return;
        }

        ctx.getGuild().removeRoleFromMember(member, currentRole).queue();
        ctx.getGuild().addRoleToMember(member, higherRole.get()).queue();
        ctx.getChannel().sendMessage(member.getAsMention() + " reached a new level and is now **" + higherRole.get().getName() + "**!").queue();
    }

    @CommandEvent(name = "rank down")
    public void onRankDownCommand(CommandContext ctx) {
        final JSONArray jsonRoles = Config.get().getJSONArray("designer-ranks");
        final List<String> roles = new ArrayList<>();
        for (int i = 0; i < jsonRoles.length(); i++) {
            roles.add(jsonRoles.getString(i));
        }

        if (ctx.getMessage().getMentionedMembers().size() == 0) {
            ctx.getMessage().reply("`!rank down <member>`").queue();
            return;
        }

        final Member member = ctx.getMessage().getMentionedMembers().get(0);

        List<Role> designerRoles = member.getRoles().stream().filter(role -> roles.contains(role.getId())).toList();
        if (designerRoles.size() == 0) {
            ctx.getMessage().reply(member.getEffectiveName() + " doesn't have any designer roles...").queue();
            return;
        }
        designerRoles = designerRoles.subList(0, designerRoles.size());

        final Role currentRole = designerRoles.get(0);
        final Optional<Role> lowerRole = ctx.getGuild().getRoles().stream().filter(r -> r.getPosition() == currentRole.getPosition() - 1).findFirst();
        if (lowerRole.isEmpty() || !roles.contains(lowerRole.get().getId())) {
            ctx.getMessage().reply("Oh no, " + member.getAsMention() + " you lost your last designer role :/").queue();
            ctx.getGuild().removeRoleFromMember(member, currentRole).queue();
            return;
        }


        ctx.getGuild().removeRoleFromMember(member, currentRole).queue();
        ctx.getGuild().addRoleToMember(member, lowerRole.get()).queue();
        ctx.getChannel().sendMessage(member.getAsMention() + " got a downrank and is now **" + lowerRole.get().getName() + "**!").queue();
    }

}
