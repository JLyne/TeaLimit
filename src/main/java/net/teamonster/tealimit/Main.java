package net.teamonster.tealimit;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class Main extends JavaPlugin implements Listener {
	private int breedLimit;
	private int naturalLimit;
	private int range;
	private int spawnEggLimit;
	private int spawnerLimit;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();

		this.breedLimit = getConfig().getInt("breed-limit");
		this.naturalLimit = getConfig().getInt("natural-limit");
		this.range = getConfig().getInt("range");
		this.spawnEggLimit = getConfig().getInt("spawnegg-limit");
		this.spawnerLimit = getConfig().getInt("spawner-limit");
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		switch (event.getSpawnReason()) {
			case BREEDING, EGG, DISPENSE_EGG -> {
				if (entityLimit(event.getEntity(), this.breedLimit)) {
					event.setCancelled(true);

					event.getEntity().getWorld().spawnParticle(
							Particle.VILLAGER_ANGRY, event.getEntity().getEyeLocation(),
							5, 0.35, 0.25, 0.35);
				}
			}
			case NATURAL, NETHER_PORTAL -> {
				if (entityLimit(event.getEntity(), this.naturalLimit)) {
					event.setCancelled(true);
				}
			}
			case SPAWNER -> {
				if (entityLimit(event.getEntity(), this.spawnerLimit)) {
					event.setCancelled(true);
				}
			}
			case SPAWNER_EGG -> {
				if (entityLimit(event.getEntity(), this.spawnEggLimit)) {
					event.setCancelled(true);

					event.getEntity().getWorld().spawnParticle(
							Particle.VILLAGER_ANGRY, event.getEntity().getEyeLocation(),
							5, 0.35, 0.25, 0.35);
				}
			}
		}
	}

	@EventHandler
	public void onCreatureWantsToFuck(EntityEnterLoveModeEvent event) {
		if (entityLimit(event.getEntity(), this.breedLimit)) {
			event.setCancelled(true);
			event.getEntity().getWorld().spawnParticle(
					Particle.VILLAGER_ANGRY, event.getEntity().getEyeLocation(),
					5, 0.35, 0.25, 0.35);
		}
	}

	private boolean entityLimit(Entity entity, int limit) {
		Collection<Entity> entityList = entity.getLocation().getNearbyEntities(this.range, 255.0d, this.range);
		EntityType entityType = entity.getType();
		int count = 0;

		for (Entity value : entityList) {
			if (value.getType() == entityType)
				count++;
		}

		return count >= limit;
	}
}
