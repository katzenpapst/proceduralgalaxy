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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.FMLClientHandler;
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
import de.katzenpapst.proceduralgalaxy.network.SimplePacketPG;
import de.katzenpapst.proceduralgalaxy.tick.TickHandlerServer;
import de.katzenpapst.proceduralgalaxy.worldgen.SolarSystemGenerator;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicMoon;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicPlanet;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicSolarSystem;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicStar;

@Mod(modid=ProceduralGalaxy.MODID, name=ProceduralGalaxy.MODNAME, version=ProceduralGalaxy.VERSION,  dependencies = "required-after:GalacticraftCore") //Tell forge "Oh hey, there's a new mod here to load."

public class ProceduralGalaxy {
	public static final String MODNAME = "Pra's GC Mod";
	public static final String MODID = "proceduralgalaxy";
	public static final String VERSION = "0.0.0";
    public static final String ASSET_PREFIX = MODID;
    
    protected ConfigManager configMgr;
    
    public static Moon moonTutorial;
    public Star starRa;
    public Planet starAmun;
    public SolarSystem systemRa;
    
    protected boolean debugDoOnce = false;
    
    private PGChannelHandler channelHandler;
    

	@Instance(ProceduralGalaxy.MODID)
    public static ProceduralGalaxy instance;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        TestBlock b = new TestBlock();
        GameRegistry.registerBlock(b, "blockObservatory");
    	createCelestialObjects();
        
        channelHandler = PGChannelHandler.init();
    }
    
    private void createCelestialObjects() {
    	systemRa = new SolarSystem("systemRa", "milkyWay").setMapPosition(new Vector3(2.5F, -1.15F, 0.0F));
    	
    	starRa = new Star("sunRa").setParentSolarSystem(systemRa);
    	starRa.setBodyIcon(new ResourceLocation(ASSET_PREFIX, "textures/gui/celestialbodies/sun-red2.png"));
    	starRa.setRelativeSize(1);
    	starRa.setTierRequired(-1);
    	
    	systemRa.setMainStar(starRa);
    	
    	
    	
    	
    	
    	
    	// planets
    	
    	
    	Planet planetOsiris = makePlanet("osiris", "planet-desert.png", 0.5F, 0.25F, 3, 0.5F);
    	
    	planetOsiris.setParentSolarSystem(systemRa);
 
    	//planetOsiris.setRelativeSize(0.1F);
    	
    	starAmun = makePlanet("sunAmun", "sun-white.png", (float) Math.PI*3/4, 0.4F, 3, 9F);
    	starAmun.setParentSolarSystem(systemRa);
    	
    	Planet planetBaal = makePlanet("baal", "planet-gas02.png", (float)Math.PI*1/9, 0.7F, 3, 9F);
    	planetBaal.setParentSolarSystem(systemRa);
    	
    	Planet planetAnubis = makePlanet("anubis", "planet-desert.png", (float)Math.PI*4/5, 2F, 3, 9F);
    	planetAnubis.setParentSolarSystem(systemRa);
    	
    	GalaxyRegistry.registerSolarSystem(systemRa);
    	GalaxyRegistry.registerPlanet(planetAnubis);
    	GalaxyRegistry.registerPlanet(starAmun);
    	GalaxyRegistry.registerPlanet(planetBaal);
    	GalaxyRegistry.registerPlanet(planetOsiris);
    }
    
    private Planet makePlanet(String name, String texture, float phaseShift, float distance, int tier, float orbitTime) {
    	String textureName = "textures/gui/celestialbodies/";
    	textureName = textureName.concat(texture);
    	Planet result = new Planet(name);
    	result.setBodyIcon(new ResourceLocation(ASSET_PREFIX, textureName));
    	result.setPhaseShift(phaseShift);
    	result.setRelativeDistanceFromCenter(new ScalableDistance(distance, distance));
    	result.setTierRequired(tier);
    	result.setRelativeOrbitTime(orbitTime);
    	return result;
    	
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

    /**
     * Okay, I admit I'm not sure how exactly synchronized works, or in what thread these
     * minecraft server <---> client messages are processed. But I *think* this here
     * *might* prevent race conditions. This is the method which should be called from 
     * @return
     */
    synchronized public SolarSystem createNewSolarSystem(EntityPlayerMP forUser) {
	    try{
	    	DynamicSolarSystem sys = TickHandlerServer.ssData.generateNew(forUser.getUniqueID());
	    	ProceduralGalaxy.instance.getChannelHandler().sendToPlayer(new SimplePacketPG(
	    				SimplePacketPG.EnumSimplePacketPG.C_SOLAR_SYSTEM_GENERATED,
	    				new Object[] {
    						sys
	    				}
	    			), forUser);
	    } catch (CannotGenerateException e) {
	    	// C_SOLAR_SYSTEM_GENERATION_FAILED
	    	ProceduralGalaxy.instance.getChannelHandler().sendToPlayer(new SimplePacketPG(
    				SimplePacketPG.EnumSimplePacketPG.C_SOLAR_SYSTEM_GENERATION_FAILED,
    				new Object[] {
						e
    				}
    			), forUser);
	    	// think of some useful error handling, or just die if this happens?
			return null;
		}
    	return null;
    }
}
