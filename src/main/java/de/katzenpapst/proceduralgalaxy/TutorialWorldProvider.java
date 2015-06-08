package de.katzenpapst.proceduralgalaxy;

import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;

public class TutorialWorldProvider extends WorldProviderSpace implements IGalacticraftWorldProvider, ISolarLevel {

	@Override
	public void setDimension(int var1)
    {
        this.dimensionId = var1; // but why? this gets done anyway
        // todo add special logic for dynamic planets
        super.setDimension(var1);
    }
	
	@Override
	public float getGravity() {
		return 0.072F;
	}

	@Override
	public double getMeteorFrequency() {
		return 7.0D;
	}

	@Override
	public double getFuelUsageMultiplier() {
		return 0.7D;
	}

	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier > 0;
	}

	@Override
	public float getFallDamageModifier() {
		return 0.18F;
	}

	@Override
	public float getSoundVolReductionAmount()
	{
		return 20.0F;
	}

	@Override
	public float getThermalLevelModifier() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getWindLevel() {
		return 0;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return ProceduralGalaxy.moonTutorial;
	}

	@Override
	public Vector3 getFogColor() {
		return new Vector3(0, 0, 0);
	}

	@Override
	public Vector3 getSkyColor() {
		return new Vector3(0, 0, 0);
	}

	@Override
	public boolean canRainOrSnow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSunset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getDayLength() {
		// 24000
		return 256000L;
	}

	@Override
	public boolean shouldForceRespawn() {
		return !ConfigManagerCore.forceOverworldRespawn;
	}

	@Override
	public Class<? extends IChunkProvider> getChunkProviderClass() {
		return TutorialChunkProvider.class;
	}

	@Override
	public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
		return WorldChunkManagerTutorial.class;
	}

	@Override
	public double getSolarEnergyMultiplier() {
		return 5;
	}
	
	@Override
	public double getHorizon()
	{
		return 44.0D;
	}
	@Override
	public int getAverageGroundLevel()
	{
		return 44;
	}
	
	@Override
	public boolean canCoordinateBeSpawn(int var1, int var2)
	{
		return true;
	}

}
