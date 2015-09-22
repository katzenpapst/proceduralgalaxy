package de.katzenpapst.proceduralgalaxy.tick;

import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;
import de.katzenpapst.proceduralgalaxy.SolarSystemSavedData;
import de.katzenpapst.proceduralgalaxy.data.PlanetData;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;
import de.katzenpapst.proceduralgalaxy.data.StarData;
import de.katzenpapst.proceduralgalaxy.worldgen.SolarSystemManager;


/**
 * I admit I have no idea what exactly a tickhandler is for, but it seems like I need it for data storage and stuff
 * 
 * @author Alex
 *
 */
public class TickHandlerServer {
	
	public static SolarSystemSavedData ssData = null;
	
	public static void restart()
    {
		ssData = null;
    }
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		/*if(event.phase == TickEvent.Phase.START) {
			//noop
			if(ssData == null) {
				
			}
		}*/
	}

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
    	/*MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		//Prevent issues when clients switch to LAN servers
		if (server == null) return;
		if (event.phase == TickEvent.Phase.START)
		{
		if (AsteroidsTickHandlerServer.spaceRaceData == null)
		{
		World world = server.worldServerForDimension(0);
		AsteroidsTickHandlerServer.spaceRaceData = (ShortRangeTelepadHandler) world.mapStorage.loadData(ShortRangeTelepadHandler.class, ShortRangeTelepadHandler.saveDataID);
		if (AsteroidsTickHandlerServer.spaceRaceData == null)
		{
		AsteroidsTickHandlerServer.spaceRaceData = new ShortRangeTelepadHandler(ShortRangeTelepadHandler.saveDataID);
		world.mapStorage.setData(ShortRangeTelepadHandler.saveDataID, AsteroidsTickHandlerServer.spaceRaceData);
		}
		}
		}*/
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server == null) return;
		if (event.phase == TickEvent.Phase.START)
		{
			if (TickHandlerServer.ssData == null)
            {
				World world = server.worldServerForDimension(0);
				TickHandlerServer.ssData = (SolarSystemSavedData) world.mapStorage.loadData(SolarSystemSavedData.class, SolarSystemSavedData.saveDataID);
				if(TickHandlerServer.ssData == null) {
					// TickHandlerServer.ssData = ProceduralGalaxy.instance.getSolarSystemManager();//new SolarSystemManager();
					TickHandlerServer.ssData = new SolarSystemSavedData(SolarSystemSavedData.saveDataID);
					world.mapStorage.setData(SolarSystemSavedData.saveDataID, TickHandlerServer.ssData );
				}
            }
		}
    	
    	/*
        if (event.phase == TickEvent.Phase.START)
        {
            if (TickHandlerServer.ssData == null)
            {
                World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
                
                ssData = SolarSystemManager.initWorldData(world);
                // createCelestialObjects();
            }
        }*/
    }
    
    private void createCelestialObjects() {
    	
    	// now try it with the data
    	SolarSystemData ssData = new SolarSystemData();
    	ssData.displayName = "Amun Ra";
    	ssData.mapPosition = new Vector3(2.5F, -1.15F, 0.0F);
    	ssData.solarSystemID = 1; 
    	ssData.unlocalizedName = "systemRa";
    	ssData.mainStar = new StarData();
    	
    	ssData.mainStar.brightness = 2;
    	ssData.mainStar.celestialBodyIcon = "sun-red2";
    	ssData.mainStar.discovererUUID = null;
    	ssData.mainStar.displayName = "Ra";
    	ssData.mainStar.relativeSize = 1;
    	ssData.mainStar.size = 1;
    	ssData.mainStar.unlocalizedName = "sunRa";

    	
    	PlanetData osiris = new PlanetData();
    	osiris.atmosphere.add(IAtmosphericGas.NITROGEN);
    	osiris.atmosphere.add(IAtmosphericGas.CO2);
    	osiris.density = 1.2;
    	osiris.atmosphericPressure = 0.3;
    	osiris.bodyRadius = 0.4;
    	osiris.dayLength = 4000;
    	osiris.displayName = "Osiris";
    	osiris.distanceFromCenter = 0.32F;
    	osiris.celestialBodyIcon = "planet-desert";
    	osiris.phaseShift = (float) (23.F/Math.PI);
    	osiris.temperature = 3F;
    	osiris.unlocalizedName = "osiris";
    	osiris.dimensionID = 23;
    	ssData.planets.add(osiris);
    	
    	ProceduralGalaxy.instance.getSolarSystemManager().addSolarSystem(ssData);
    	GalaxyRegistry.refreshGalaxies();
    }
}
