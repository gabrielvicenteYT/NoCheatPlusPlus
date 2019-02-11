package org.mswsplex.anticheat.checks.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;
import org.mswsplex.anticheat.protocols.TinyProtocol;

import io.netty.channel.Channel;

/**
 * Kicks the player for too many custompayload packets
 * 
 * @author imodm
 *
 */
public class ServerCrasher1 implements Check {
	private AntiCheat plugin;

	private final int MAX_PACKETS = 40, TIMESPAN = 20;

	@Override
	public CheckType getType() {
		return CheckType.MISC;
	}

	@Override
	public void register(AntiCheat plugin) {

		this.plugin = plugin;

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player target : Bukkit.getOnlinePlayers()) {
					CPlayer cp = plugin.getCPlayer(target);
					cp.setTempData("customPayloadPackets", 0);
				}
			}
		}.runTaskTimer(plugin, 0, TIMESPAN);

		new TinyProtocol(plugin) {
			@Override
			public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
				if (sender == null)
					return super.onPacketInAsync(sender, channel, packet);
				CPlayer cp = ServerCrasher1.this.plugin.getCPlayer(sender);
				String name = packet.toString().split("\\.")[packet.toString().split("\\.").length - 1];
				if (name.contains("@"))
					name = name.substring(0, name.indexOf("@"));

				if (!name.equals("PacketPlayInCustomPayload"))
					return super.onPacketInAsync(sender, channel, packet);

				int customPackets = cp.getTempInteger("customPayloadPackets");

				cp.setTempData("customPayloadPackets", customPackets + 1);

				if (customPackets > MAX_PACKETS) {
					double lastTooMany = cp.timeSince("extremeCustomPayload");

					cp.setTempData("extremeCustomPayload", (double) System.currentTimeMillis());

					if (lastTooMany < 100)
						return null;

					Bukkit.getScheduler().runTask(this.plugin, () -> {
						if (sender.isOnline())
							sender.kickPlayer("Too many packets!");
						cp.flagHack(ServerCrasher1.this, 300);
					});
					return null;
				}
				return super.onPacketInAsync(sender, channel, packet);
			}
		};

	}

	@Override
	public String getCategory() {
		return "ServerCrasher";
	}

	@Override
	public String getDebugName() {
		return "ServerCrasher#1";
	}

	@Override
	public boolean lagBack() {
		return false;
	}
}
