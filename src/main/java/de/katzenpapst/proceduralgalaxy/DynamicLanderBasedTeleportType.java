package de.katzenpapst.proceduralgalaxy;

import java.util.Random;

import de.katzenpapst.proceduralgalaxy.data.LandeableData;
import de.katzenpapst.proceduralgalaxy.data.LandeableData.LandeableType;
import de.katzenpapst.proceduralgalaxy.worldgen.SolarSystemManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.ITeleportType;
import micdoodle8.mods.galacticraft.core.entities.EntityLander;
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.EntityEntryPod;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons;

public class DynamicLanderBasedTeleportType implements ITeleportType {

	@Override
	public boolean useParachute() {
		// keep it as false for this case
		return false;
	}

	@Override
	public Vector3 getPlayerSpawnLocation(WorldServer world,
			EntityPlayerMP player) {
		if (player != null)
		{
			GCPlayerStats stats = GCPlayerStats.get(player);
			return new Vector3(stats.coordsTeleportedFromX, ConfigManagerCore.disableLander ? 250.0 : 900.0, stats.coordsTeleportedFromZ);
		}
		return null;
	}

	@Override
	public Vector3 getEntitySpawnLocation(WorldServer world, Entity entity) {
		return new Vector3(entity.posX, ConfigManagerCore.disableLander ? 250.0 : 900.0, entity.posZ);
	}

	@Override
	public Vector3 getParaChestSpawnLocation(WorldServer world,
			EntityPlayerMP player, Random rand) {
		if (ConfigManagerCore.disableLander)
		{
			final double x = (rand.nextDouble() * 2 - 1.0D) * 5.0D;
			final double z = (rand.nextDouble() * 2 - 1.0D) * 5.0D;
			return new Vector3(x, 220.0D, z);
		}
		return null;
	}

	@Override
	public void onSpaceDimensionChanged(World newWorld, EntityPlayerMP player,
			boolean ridingAutoRocket) {
		int targetDimID = newWorld.provider.dimensionId;
		
		
		
		SolarSystemManager ssMgr = ProceduralGalaxy.instance.getSolarSystemManager();
		// not sure if this thing exist client-side, and if not, whenever this is a problem...
		if(ssMgr == null) {
			GCLog.info("Could not get ssMgr...");
			return;
		}
		LandeableData lData = ssMgr.getDataByDimId(targetDimID);
		if(lData == null) {
			GCLog.info("Could not get lData for "+targetDimID);
			return;
		}
		
		
				
		
		GCPlayerStats stats = GCPlayerStats.get(player);
		if (!ridingAutoRocket && !ConfigManagerCore.disableLander && stats.teleportCooldown <= 0)
		{
			if (player.capabilities.isFlying)
			{
				player.capabilities.isFlying = false;
			}
			EntityLanderBase lander = null;
			
			if(lData.landeableType == LandeableType.LT_MICRO || lData.landeableType == LandeableType.LT_BELT || lData.getGravityFactor() <= 0.01) {
				// entry pod
				lander = new EntityEntryPod(player);
			} else if(lData.atmosphericPressure <= 0.01) {
				// moon lander
				lander = new EntityLander(player);
			} else {
				// mars lander
				lander = new EntityLandingBalloons(player);
			}
			
			// maybe not needed? this is only used for the moon lander in GC
			lander.setPosition(player.posX, player.posY, player.posZ);
			if (!newWorld.isRemote)
			{
				newWorld.spawnEntityInWorld(lander);
			}
			stats.teleportCooldown = 10;
		}
	}

}
