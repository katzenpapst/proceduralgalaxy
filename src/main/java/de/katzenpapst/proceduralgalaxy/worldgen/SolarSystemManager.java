package de.katzenpapst.proceduralgalaxy.worldgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import de.katzenpapst.proceduralgalaxy.DynamicPlanetWorldProvider;
import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;
import de.katzenpapst.proceduralgalaxy.SolarSystemSavedData;
import de.katzenpapst.proceduralgalaxy.config.ConfigManager;
import de.katzenpapst.proceduralgalaxy.data.LandeableData;
import de.katzenpapst.proceduralgalaxy.data.MoonData;
import de.katzenpapst.proceduralgalaxy.data.PlanetData;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;
import de.katzenpapst.proceduralgalaxy.exception.CannotGenerateException;
import de.katzenpapst.proceduralgalaxy.tick.TickHandlerServer;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicMoon;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicPlanet;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicSolarSystem;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicStar;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/**
 * This is supposed to be the central class for everything related to dynamical solar systems 
 * 
 * @author katzenpapst
 *
 */
public class SolarSystemManager {
	
	
	// maybe I need to store the actual solar systems just like that. time will show
	protected HashMap<Integer, SolarSystemData> solarSystemDataStorage;
	
	protected HashMap<Integer, LandeableData> dimIdDataMapping;
	protected HashMap<Integer, CelestialBody> dimIdBodyMapping;
	// the highest solar system ID should be saved here
	protected int highestId = 0;
	
	protected SolarSystemGenerator generator = null;
	
	protected ConfigManager configMgr = null; 

	public SolarSystemManager() {

		configMgr = ProceduralGalaxy.instance.getConfigManager();
		solarSystemDataStorage = new HashMap<Integer, SolarSystemData>();
		
		dimIdDataMapping = new HashMap<Integer, LandeableData>();
		dimIdBodyMapping = new HashMap<Integer, CelestialBody>();
	}
	
	/**
	 * Generate a new system for a certain user
	 * 
	 * @param username
	 * @return
	 * @throws CannotGenerateException
	 */
	public DynamicSolarSystem generateNew(UUID userId) throws CannotGenerateException {
		
		SolarSystemGenerator generator = new SolarSystemGenerator (userId);
		SolarSystemData data = generator.generate(highestId+1);
		DynamicSolarSystem generatedSystem = addSolarSystem(data);

		doneGenerating(false);
    	return generatedSystem;
	}
	
	/**
	 * Takes SolarSystemData, and actually creates the solar system. Not for direct use
	 * @param data
	 */
	private DynamicSolarSystem createSolarSystem(SolarSystemData data) {
    	DynamicSolarSystem sys = new DynamicSolarSystem(data.displayName, "milkyWay");
    	// clone, because otherwise some really weird stuff happens...
    	sys.setMapPosition(data.mapPosition.clone());
    	
    	
    	DynamicStar star = new DynamicStar(data.mainStar.displayName);
    	
    	star.setBodyIcon(new ResourceLocation(ProceduralGalaxy.ASSET_PREFIX, "textures/gui/celestialbodies/"+data.mainStar.celestialBodyIcon+".png"));
    	star.setParentSolarSystem(sys);
    	sys.setMainStar(star);
    	
    	star.setRelativeSize(data.mainStar.relativeSize);
    	
    	GalaxyRegistry.registerSolarSystem(sys);

    	
    	for(int p=0;p<data.planets.size();p++) {
    		PlanetData pData = data.planets.get(p);
    		DynamicPlanet planet = new DynamicPlanet(pData.displayName);
    		planet.setBodyIcon(new ResourceLocation(ProceduralGalaxy.ASSET_PREFIX, "textures/gui/celestialbodies/"+pData.celestialBodyIcon+".png"));
    		planet.setParentSolarSystem(sys);
    		planet.setPhaseShift(pData.phaseShift);
    		planet.setRelativeDistanceFromCenter(new ScalableDistance(pData.distanceFromCenter,pData.distanceFromCenter));
    		planet.setRelativeOrbitTime(pData.relativeOrbitTime);
    		planet.setRelativeSize(pData.relativeSize);
    		planet.setTierRequired(configMgr.getPlanetTier());
    		
    		initDimensionData(planet, pData);
    		GalaxyRegistry.registerPlanet(planet);
    		
    		for(int m=0;m<pData.moons.size();m++) {
    			MoonData mData = pData.moons.get(m);
    			DynamicMoon moon = new DynamicMoon(mData.displayName);
    			moon.setBodyIcon(new ResourceLocation(ProceduralGalaxy.ASSET_PREFIX, "textures/gui/celestialbodies/"+mData.celestialBodyIcon+".png"));
    			moon.setParentPlanet(planet);    			
    			moon.setPhaseShift(mData.phaseShift);
    			moon.setRelativeDistanceFromCenter(new ScalableDistance(mData.distanceFromCenter,mData.distanceFromCenter));
    			moon.setRelativeOrbitTime(mData.relativeOrbitTime);
    			moon.setRelativeSize(mData.relativeSize);
    			moon.setTierRequired(configMgr.getPlanetTier());
    			
    			initDimensionData(moon, mData);
    			GalaxyRegistry.registerMoon(moon);
    		}
    	}
    	return sys;
    	
	}
	
	private void initDimensionData(CelestialBody body, LandeableData data) {
	
		
		LandeableData usedBy = dimIdDataMapping.get(data.dimensionID);
		if(usedBy != null) {
			GCLog.severe("Dimension ID "+data.dimensionID+" cannot be used for "+data.displayName+" since it is used by "+usedBy.displayName);
			return;
		}
		body.setDimensionInfo(data.dimensionID, DynamicPlanetWorldProvider.class);
		dimIdDataMapping.put(data.dimensionID, data);
		dimIdBodyMapping.put(data.dimensionID, body);
		// http://forum.micdoodle8.com/index.php?threads/how-to-write-a-basic-moon-addon-1-7.4452/
	
	}
	
	public LandeableData getDataByDimId(int dimID) {
		return dimIdDataMapping.get(dimID);
	}
	
	public CelestialBody getCelestialBodyByDimId(int dimID) {
		return dimIdBodyMapping.get(dimID);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		
		// "10" seems to mean "list with elements of type Compound"
		// see NBTBase::getId
		NBTTagList nbtList = nbt.getTagList("solarSystems", 10);
		
		for(int i=0;i<nbtList.tagCount();i++) {
			NBTTagCompound curNbt = nbtList.getCompoundTagAt(i);
			SolarSystemData data = new SolarSystemData();
			data.loadFromNbt(curNbt);
			addSolarSystem(data);
		}
		doneGenerating(true);
	}

	public void writeToNBT(NBTTagCompound nbt) {
	
		
		NBTTagList nbtList = new NBTTagList();
		// for saving, I don't need the keys
		for (SolarSystemData d : solarSystemDataStorage.values()) {
			if(d.solarSystemID != -1) {
				NBTTagCompound curNbt = new NBTTagCompound();
				d.saveToNbt(curNbt);
				nbtList.appendTag(curNbt);
			}
		}
		nbt.setTag("solarSystems", nbtList);
		
	}
	
	/**
	 * Add data, obtained by any means, to the system
	 * Will also create the system. A call to GalaxyRegistry.refreshGalaxies(); is necessary afterwards!
	 * @param d
	 */
	public DynamicSolarSystem addSolarSystem(SolarSystemData d) {
		if(d.solarSystemID != -1 && solarSystemDataStorage.containsKey(d.solarSystemID)) {
			// asplode. this shouldn't happen
			throw new IllegalArgumentException("Invalid Solar System data! ID "+d.solarSystemID+" exists twice!");
		}
		if(d.solarSystemID > highestId) {
			highestId = d.solarSystemID; 
		}
		solarSystemDataStorage.put(d.solarSystemID, d);
		return createSolarSystem(d);
	}
	
	/**
	 * This should be called after addSolarSystem has been called enough times.
	 * It should update/notify everything which needs being updated/notified when solar systems are added
	 * @param initialLoad	whenever this is the initial load from NBT, and not runtime system generation
	 */
	protected void doneGenerating(boolean initialLoad) {
		if(!initialLoad) {
			if(TickHandlerServer.ssData != null) {
				TickHandlerServer.ssData.markDirty();
			}
		}

    	GalaxyRegistry.refreshGalaxies();
	}
	/*
	public static SolarSystemManager initWorldData(World world)
    {
		
		SolarSystemManager worldData = (SolarSystemManager) world.perWorldStorage.loadData(SolarSystemManager.class, saveDataID);
		// WorldDataSolarSystems worldData = (WorldDataSolarSystems) world.loadItemData(WorldDataSolarSystems.class, WorldDataSolarSystems.saveDataID);

        if (worldData == null)
        {
            worldData = new SolarSystemManager(SolarSystemManager.saveDataID);
            world.perWorldStorage.setData(saveDataID, worldData);
            // world.setItemData(WorldDataSolarSystems.saveDataID, worldData);
            // worldData.dataCompound = new NBTTagCompound();
            worldData.markDirty();
        }

        return worldData;
    }*/

}
