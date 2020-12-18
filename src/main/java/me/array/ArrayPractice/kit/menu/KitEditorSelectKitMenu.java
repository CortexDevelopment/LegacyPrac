package me.array.ArrayPractice.kit.menu;

import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import me.array.ArrayPractice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitEditorSelectKitMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&7Select a kit";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		Kit.getKits().forEach(kit -> {
			if (kit.isEnabled()) {
				if (kit.getGameRules().isEditable()) {
					buttons.put(buttons.size(), new KitDisplayButton(kit));
				}
			}
		});

		return buttons;
	}

	@AllArgsConstructor
	private class KitDisplayButton extends Button {

		private Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(kit.getDisplayIcon())
					.name("&b" + kit.getName())
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			player.closeInventory();

			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.getKitEditor().setSelectedKit(kit);
			profile.getKitEditor().setPreviousState(profile.getState());

			new KitManagementMenu(kit).openMenu(player);
		}

	}
}