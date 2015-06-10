package de.katzenpapst.proceduralgalaxy;

import java.util.ArrayList;
import java.util.HashMap;

import de.katzenpapst.proceduralgalaxy.config.ConfigManager;
import de.katzenpapst.proceduralgalaxy.data.MoonData;
import de.katzenpapst.proceduralgalaxy.data.PlanetData;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;
import de.katzenpapst.proceduralgalaxy.exception.CannotGenerateException;
import de.katzenpapst.proceduralgalaxy.worldgen.SolarSystemGenerator;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicMoon;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicPlanet;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicSolarSystem;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicStar;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/**
 * This is supposed to be the central class for everything related to dynamical solar systems 
 * 
 * @author katzenpapst
 *
 */
public class SolarSystemManager extends WorldSavedData {
	
	public static final String saveDataID = "PGSolarSystemData";
	
	// maybe I need to store the actual solar systems just like that. time will show
	protected HashMap<Integer, SolarSystemData> solarSystemDataStorage;
	// the highest solar system ID should be saved here
	protected int highestId = 0;
	
	protected SolarSystemGenerator generator = null;
	
	protected ConfigManager configMgr = null; 

	public SolarSystemManager(String p_i2141_1_) {
		super(p_i2141_1_);

		configMgr = ProceduralGalaxy.instance.getConfigManager();
		solarSystemDataStorage = new HashMap<Integer, SolarSystemData>(); 
		generator = new SolarSystemGenerator(); 
	}
	
	/**
	 * Generate a new system for a certain user
	 * 
	 * @param username
	 * @return
	 * @throws CannotGenerateException
	 */
	public DynamicSolarSystem generateNew(String username) throws CannotGenerateException {
		
		SolarSystemData data = generator.generate(highestId+1, username);
		DynamicSolarSystem generatedSystem = addSolarSystem(data);

		doneGenerating();
    	return generatedSystem;
	}
	
	/**
	 * Takes SolarSystemData, and actually creates the solar system. Not for direct use
	 * @param data
	 */
	private DynamicSolarSystem createSolarSystem(SolarSystemData data) {
    	DynamicSolarSystem sys = new DynamicSolarSystem(data.displayName, "milkyWay");
    	sys.setMapPosition(data.mapPosition);
    	
    	
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
    			moon.setTierRequired(configMgr.getMoonTier());
    			
    			GalaxyRegistry.registerMoon(moon);
    		}
    	}
    	return sys;
    	
	}

	@Override
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
		doneGenerating();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	
		
		NBTTagList nbtList = new NBTTagList();
		// for saving, I don't need the keys
		for (SolarSystemData d : solarSystemDataStorage.values()) {
		
			NBTTagCompound curNbt = new NBTTagCompound();
			d.saveToNbt(curNbt);
			nbtList.appendTag(curNbt);
		}
		nbt.setTag("solarSystems", nbtList);
		
	}
	
	/**
	 * Add data, obtained by any means, to the system
	 * Will also create the system. A call to GalaxyRegistry.refreshGalaxies(); is necessary afterwards!
	 * @param d
	 */
	protected DynamicSolarSystem addSolarSystem(SolarSystemData d) {
		if(solarSystemDataStorage.containsKey(d.solarSystemID)) {
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
	 */
	protected void doneGenerating() {
		this.markDirty();

    	GalaxyRegistry.refreshGalaxies();
	}
	
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
    }

}
