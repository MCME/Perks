package com.mcmiddleearth.perks.commands;

import com.mcmiddleearth.perks.PerksPlugin;
import com.mcmiddleearth.perks.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * @author Jubo
 */
public class VideoTeamCommand implements CommandExecutor {

    private static Team team;
    private static Scoreboard board;
    private static final String teamName = "videoteam";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command can only be used by players.");
            return true;
        }
        if(!player.hasPermission(Permissions.USER_VIDEOTEAM.getPermissionNode())){
            PerksPlugin.getMessageUtil().sendNoPermissionError(player);
            return true;
        }
        if(team == null) {
            sendErrorMessage(player);
            return true;
        }
        if(team.hasPlayer(player)) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            team.removePlayer(player);
            sendShowNametagMessage(player);
        }
        else {
            player.setScoreboard(board);
            team.addPlayer(player);
            sendActivateMessage(player);
        }
        return true;
    }

    public static void enableVideoTeam(){
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        team = board.getTeam(teamName);
        if(team == null){
            team = board.registerNewTeam(teamName);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY,Team.OptionStatus.FOR_OTHER_TEAMS);
        }
    }

    public static void disableVideoTeam(){
        for(Player player : Bukkit.getOnlinePlayers()) team.removePlayer(player);
        if(team !=null) team.unregister();
        team = null;
    }

    private void sendErrorMessage(Player player){
        PerksPlugin.getMessageUtil().sendErrorMessage(player,"Error, something went wrong.");
    }

    private void sendActivateMessage(Player player){
        PerksPlugin.getMessageUtil().sendInfoMessage(player,"You can now hide all nametags with F1.");
    }

    private void sendShowNametagMessage(Player player){
        PerksPlugin.getMessageUtil().sendInfoMessage(player,"You switched the videoteam functionality off.");
    }
}
