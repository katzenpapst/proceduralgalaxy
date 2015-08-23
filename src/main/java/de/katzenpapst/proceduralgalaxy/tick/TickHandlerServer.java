package de.katzenpapst.proceduralgalaxy.tick;

import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
// import de.katzenpapst.proceduralgalaxy.SolarSystemManager;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;


/**
 * I admit I have no idea what exactly a tickhandler is for, but it seems like I need it for data storage and stuff
 * 
 * @author Alex
 *
 */
public class TickHandlerServer {
	
	// public static SolarSystemManager ssData = null;
	
	public static void restart()
    {
		// ssData = null;
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
    	
        if (event.phase == TickEvent.Phase.START)
        {
            /*if (TickHandlerServer.ssData == null)
            {
                World world = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
                
                ssData = SolarSystemManager.initWorldData(world);
            }*/
            
            /*
             * World world0 = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        CommandGCInv.savefile = (GCInvSaveData) world0.loadItemData(GCInvSaveData.class, GCInvSaveData.SAVE_ID);
        if (CommandGCInv.savefile == null)
        {
            CommandGCInv.savefile = new GCInvSaveData();
            world0.setItemData(GCInvSaveData.SAVE_ID, CommandGCInv.savefile);
        }*/
        }
    }
}
