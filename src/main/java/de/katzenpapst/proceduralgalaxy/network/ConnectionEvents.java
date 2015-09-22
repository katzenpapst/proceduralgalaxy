package de.katzenpapst.proceduralgalaxy.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRace;
import micdoodle8.mods.galacticraft.core.dimension.SpaceRaceManager;
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderOrbit;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;

public class ConnectionEvents {
	private static boolean clientConnected = false;

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		//ChunkLoadingCallback.onPlayerLogout(event.player);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		// Side mSide = FMLCommonHandler.instance().getEffectiveSide();
		// this happens on the server side
		// ChunkLoadingCallback.onPlayerLogin(event.player);
		
	
		/*
		
		if (event.player instanceof EntityPlayerMP) {
			EntityPlayerMP thePlayer = (EntityPlayerMP) event.player;
			
			NBTTagCompound nbt = new NBTTagCompound ();
			ProceduralGalaxy.instance.getSolarSystemManager().writeToNBT(nbt);
			
			ProceduralGalaxy.instance.getChannelHandler().sendToPlayer(new SimplePacketPG(
					SimplePacketPG.EnumSimplePacketPG.C_LOAD_SYSTEMS, 
					new Object[] {nbt}
				), thePlayer);
			
		
		}*/
	}

	@SubscribeEvent
	public void onConnectionReceived(ServerConnectionFromClientEvent event) {
		Result wat = event.getResult();
		// So I think this is done by the server
		Side mSide = FMLCommonHandler.instance().getEffectiveSide();
		
		NBTTagCompound nbt = new NBTTagCompound ();
		ProceduralGalaxy.instance.getSolarSystemManager().writeToNBT(nbt);
		event.manager.scheduleOutboundPacket(
				ConnectionPacket.createUpdateSystemPacket(nbt)
		);
	}

	@SubscribeEvent
	public void onConnectionOpened(ClientConnectedToServerEvent event) {
		if (!event.isLocal) {
			ConnectionEvents.clientConnected = true;
		}
	}

	@SubscribeEvent
	public void onConnectionClosed(ClientDisconnectionFromServerEvent event) {
		if (ConnectionEvents.clientConnected) {
			ConnectionEvents.clientConnected = false;
			//WorldUtil.unregisterPlanets();
			//WorldUtil.unregisterSpaceStations();
			//ConfigManagerCore.restoreClientConfigOverrideable();
		}
	}
}