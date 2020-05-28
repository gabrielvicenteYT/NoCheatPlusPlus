package xyz.msws.anticheat.modules.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.modules.AbstractModule;

public class PlayerManager extends AbstractModule {
	private NOPE plugin;

	private Map<UUID, CPlayer> players = new HashMap<>();

	public PlayerManager(NOPE plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	public CPlayer getPlayer(OfflinePlayer player) {
		return getPlayer(player.getUniqueId());
	}

	public CPlayer getPlayer(UUID player) {
		if (!players.containsKey(player))
			players.put(player, new CPlayer(player, plugin));
		return players.get(player);
	}

	public List<UUID> getLoadedPlayers() {
		return new ArrayList<UUID>(players.keySet());
	}

	public void removePlayer(UUID player) {
		if (players.containsKey(player))
			players.get(player).saveData();
		players.remove(player);
	}

	public void clearPlayers() {
		for (UUID player : players.keySet())
			removePlayer(player);
	}

	@Override
	public void enable() {
	}

	@Override
	public void disable() {
		clearPlayers();
	}
}
