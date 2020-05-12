package org.mswsplex.anticheat.msws;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Code copied from
 * https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates/
 * 
 * @author imodm
 *
 */
public class UpdateChecker {

	private Plugin plugin;
	private int resourceId;

	public UpdateChecker(Plugin plugin, int resourceId) {
		this.plugin = plugin;
		this.resourceId = resourceId;
	}

	public void getVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			try (InputStream inputStream = new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
					Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) {
					consumer.accept(scanner.next());
				}
			} catch (IOException exception) {
				this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
			}
		});
	}
}