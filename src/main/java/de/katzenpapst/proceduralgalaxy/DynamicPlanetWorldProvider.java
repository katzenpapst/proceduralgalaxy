package de.katzenpapst.proceduralgalaxy;

import de.katzenpapst.proceduralgalaxy.data.LandeableData;
import de.katzenpapst.proceduralgalaxy.worldgen.SolarSystemManager;
import de.katzenpapst.proceduralgalaxy.worldgen.gas.GasDataLookup;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;

public class DynamicPlanetWorldProvider extends WorldProviderSpace implements IGalacticraftWorldProvider, ISolarLevel {

	LandeableData curData = null;
	
	
	@Override
	public void setDimension(int var1) 
    {
        this.dimensionId = var1; // but why? this gets done anyway

        SolarSystemManager ssMgr = ProceduralGalaxy.instance.getSolarSystemManager();
        if(ssMgr == null) {
        	// this can't work, but how can I die here?
        	GCLog.severe("No SS Mgr...");
        }
        
        curData = ssMgr.getDataByDimId(dimensionId);
        
        super.setDimension(var1);
    }
	
	@Override
	public float getGravity() {
		return (float) curData.getGravitySubtrahend();
	}

	@Override
	public double getMeteorFrequency() {
		double tmp = 1-curData.atmosphericPressure;
		if(tmp < 0)
			tmp = 0;
		// maybe do some stuff to check if we have an asteroid belt nearby
		return tmp*7.0D;
	}

	@Override
	public double getFuelUsageMultiplier() {
		return curData.getGravityFactor();
	}

	@Override
	public boolean canSpaceshipTierPass(int tier) {
		return tier > ProceduralGalaxy.instance.getConfigManager().getPlanetTier();
	}

	@Override
	public float getFallDamageModifier() {
		// not sure about that...
		return (float) curData.getGravityFactor();
	}

	@Override
	public float getSoundVolReductionAmount()
	{
		double factor = 1-curData.atmosphericPressure;
		if(factor < 0) factor = 0;
		return (float) (20.0*factor);
	}

	@Override
	public float getThermalLevelModifier() {
		// TODO do trial&error with that
		return (float) (curData.temperature-1); // maybe also divide it by something?
	}

	@Override
	public float getWindLevel() {
		return (float) curData.atmosphericPressure;
	}

	@Override
	public CelestialBody getCelestialBody() {
		return ProceduralGalaxy.instance.getSolarSystemManager().getCelestialBodyByDimId(dimensionId);
	}

	@Override
	public Vector3 getFogColor() {
		// I need to calculate that from the atmosphere
		return GasDataLookup.getAtmosphereColor(curData.atmosphere);
	}

	@Override
	public Vector3 getSkyColor() {
		return GasDataLookup.getAtmosphereColor(curData.atmosphere);
	}
	

	@Override
	public boolean canRainOrSnow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasSunset() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public long getDayLength() {
		return curData.dayLength;
	}

	@Override
	public boolean shouldForceRespawn() {
		return !ConfigManagerCore.forceOverworldRespawn;
	}

	@Override
	public Class<? extends IChunkProvider> getChunkProviderClass() {
		return DynamicPlanetChunkProvider.class;
	}

	@Override
	public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
		return WorldChunkManagerTutorial.class;
	}

	@Override
	public double getSolarEnergyMultiplier() {
		// sunBrightness = 1, pressure = 1 => multiplier 1
		// with pressure = 0, 1.5
		// curData.sunBrightness
		return 2*curData.sunBrightness - curData.atmosphericPressure*0.5;
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
