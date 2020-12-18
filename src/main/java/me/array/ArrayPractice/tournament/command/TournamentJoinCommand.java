package me.array.ArrayPractice.tournament.command;

import me.array.ArrayPractice.tournament.Tournament;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.Profile;
import com.qrakn.honcho.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "tournament join")
public class TournamentJoinCommand {

	public void execute(Player player) {
		if(Tournament.CURRENT_TOURNAMENT == null || Tournament.CURRENT_TOURNAMENT.hasStarted()){
			player.sendMessage(ChatColor.RED + "There isn't a joinable Tournament");
			return;
		}
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if(Tournament.CURRENT_TOURNAMENT.getTeamCount() == 1){
			Party party = Profile.getByUuid(player.getUniqueId()).getParty();
			if(party != null && party.getPlayers().size() != 1){
				player.sendMessage(ChatColor.YELLOW + "This is a solo Tournament");
				return;
			}
		}else{
			Party party = Profile.getByUuid(player.getUniqueId()).getParty();
			if(party == null || party.getPlayers().size() != Tournament.CURRENT_TOURNAMENT.getTeamCount() ){
				player.sendMessage(ChatColor.RED + "The Tournament needs " + Tournament.CURRENT_TOURNAMENT.getTeamCount() + " players to start.");
				return;
			}
			if(!party.isLeader(player.getUniqueId())){
				player.sendMessage(ChatColor.RED + "Only Leaders can do this");
				return;
			}
		}
		if(profile.isBusy(player)){
			player.sendMessage(ChatColor.RED + "You cannot join the Tournament in your current state");
			return;
		}
		Party party = Profile.getByUuid(player.getUniqueId()).getParty();
		if(party == null){
			player.chat("/party create");
			party = Profile.getByUuid(player.getUniqueId()).getParty();
		}
		Tournament.CURRENT_TOURNAMENT.participate(party);
	}
}

