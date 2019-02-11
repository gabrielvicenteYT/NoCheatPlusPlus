package org.mswsplex.anticheat.checks.render;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.utils.MSG;

/**
 * Checks if a player hasn't sent a swing packet before interaction event
 * 
 * @author imodm
 *
 */
public class NoSwing1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.RENDER;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (event.getAction() == Action.RIGHT_CLICK_AIR)
			return;

		if (cp.timeSince("lastSwing") < 1000)
			return;

		if (plugin.devMode())
			MSG.tell(player, "&blastSwing: " + cp.timeSince("lastSwing"));
		cp.flagHack(this, 50);
	}

	@Override
	public String getCategory() {
		return "NoSwing";
	}

	@Override
	public String getDebugName() {
		return "NoSwing#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
