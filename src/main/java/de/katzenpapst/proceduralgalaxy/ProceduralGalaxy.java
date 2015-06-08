package de.katzenpapst.proceduralgalaxy;

import java.util.ArrayList;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeMoon;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.proceduralgalaxy.config.ConfigManager;
import de.katzenpapst.proceduralgalaxy.data.MoonData;
import de.katzenpapst.proceduralgalaxy.data.PlanetData;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;
import de.katzenpapst.proceduralgalaxy.data.StarData;
import de.katzenpapst.proceduralgalaxy.exception.CannotGenerateException;
import de.katzenpapst.proceduralgalaxy.gui.GuiHandler;
import de.katzenpapst.proceduralgalaxy.network.PGChannelHandler;
import de.katzenpapst.proceduralgalaxy.tick.TickHandlerServer;
import de.katzenpapst.proceduralgalaxy.worldgen.SolarSystemGenerator;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicMoon;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicPlanet;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicSolarSystem;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicStar;

@Mod(modid=ProceduralGalaxy.MODID, name=ProceduralGalaxy.MODNAME, version=ProceduralGalaxy.VERSION,  dependencies = "required-after:GalacticraftCore") //Tell forge "Oh hey, there's a new mod here to load."

public class ProceduralGalaxy {
	public static final String MODNAME = "Procedural Galaxy";
	public static final String MODID = "proceduralgalaxy";
	public static final String VERSION = "0.0.0";
    public static final String ASSET_PREFIX = MODID;
    
    protected ConfigManager configMgr;
    
    public static Moon moonTutorial;
    protected boolean debugDoOnce = false;
    
    private PGChannelHandler channelHandler;
    

	@Instance(ProceduralGalaxy.MODID)
    public static ProceduralGalaxy instance;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TestBlock b = new TestBlock();
        GameRegistry.registerBlock(b, "dafuqBlockTest");
        
        channelHandler = PGChannelHandler.init();
    }
    
    public PGChannelHandler getChannelHandler() {
    	return channelHandler;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	configMgr = new ConfigManager(event.getModConfigurationDirectory());
    }
    
    public ConfigManager getConfigManager(){
    	return configMgr;
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	NetworkRegistry.INSTANCE.registerGuiHandler(ProceduralGalaxy.instance, new GuiHandler());
    	FMLCommonHandler.instance().bus().register(new TickHandlerServer());
    	//event.getSide().isServer()
    }
    
    protected void loadSolarSystem(SolarSystemData data) {
    	// damn. Now, how did that work again?
    	DynamicSolarSystem sys = new DynamicSolarSystem(data.displayName, "milkyWay");
    	sys.setMapPosition(data.mapPosition);
    	
    	DynamicStar star = new DynamicStar(data.mainStar.displayName);
    	
    	star.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/"+data.mainStar.celestialBodyIcon+".png"));
    	star.setParentSolarSystem(sys);
    	sys.setMainStar(star);
    	
    	star.setRelativeSize(data.mainStar.relativeSize);
    	
    	GalaxyRegistry.registerSolarSystem(sys);

    	
    	for(int p=0;p<data.planets.size();p++) {
    		PlanetData pData = data.planets.get(p);
    		DynamicPlanet planet = new DynamicPlanet(pData.displayName);
    		planet.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/"+pData.celestialBodyIcon+".png"));
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
    			moon.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/"+mData.celestialBodyIcon+".png"));
    			moon.setParentPlanet(planet);    			
    			moon.setPhaseShift(mData.phaseShift);
    			moon.setRelativeDistanceFromCenter(new ScalableDistance(mData.distanceFromCenter,mData.distanceFromCenter));
    			moon.setRelativeOrbitTime(mData.relativeOrbitTime);
    			moon.setRelativeSize(mData.relativeSize);
    			moon.setTierRequired(configMgr.getMoonTier());
    			
    			GalaxyRegistry.registerMoon(moon);
    		}
    	}
    	
    	GalaxyRegistry.refreshGalaxies();
    	
    }
    
    public SolarSystem createNewSolarSystem() {
    	if(debugDoOnce) return null;
    	debugDoOnce = true;
    	// I should figure out how to do this on serverside only
    	SolarSystemGenerator gen = new SolarSystemGenerator();
	    try{
	    	SolarSystemData data = gen.generate(0);
	    	loadSolarSystem(data);
	    } catch (CannotGenerateException e) {
			return null;
		}
    	return null;
    }
}
