package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "sumo cancel", permission = "array.staff")
public class SumoCancelCommand {

	public void execute(CommandSender sender) {
		if (Array.getInstance().getSumoManager().getActiveSumo() == null) {
			sender.sendMessage(CC.RED + "There isn't an active Sumo Event.");
			return;
		}

		Array.getInstance().getSumoManager().getActiveSumo().end();
	}

}