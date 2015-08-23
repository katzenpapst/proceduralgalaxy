package de.katzenpapst.proceduralgalaxy.network;
import java.util.EnumMap;

import net.minecraft.entity.player.EntityPlayerMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.network.GalacticraftChannelHandler;
import micdoodle8.mods.galacticraft.core.network.GalacticraftPacketHandler;
import micdoodle8.mods.galacticraft.core.network.IPacket;
import micdoodle8.mods.galacticraft.core.network.PacketControllableEntity;
import micdoodle8.mods.galacticraft.core.network.PacketDynamic;
import micdoodle8.mods.galacticraft.core.network.PacketDynamicInventory;
import micdoodle8.mods.galacticraft.core.network.PacketEntityUpdate;
import micdoodle8.mods.galacticraft.core.network.PacketRotateRocket;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;

/**
 * Mostly stolen from GalacticraftCore...
 * @author katzenpapst
 *
 */
public class PGChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket>{
	
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	
	private PGChannelHandler()
    {
        this.addDiscriminator(0, SimplePacketPG.class);
    }

    public static PGChannelHandler init()
    {
    	PGChannelHandler channelHandler = new PGChannelHandler();
    	// NetworkRegistry.INSTANCE.ne
    	
    	channelHandler.channels = NetworkRegistry.INSTANCE.newChannel(
    			ProceduralGalaxy.MODID, 
    			channelHandler, 
    			new PGPacketHandler());
        return channelHandler;
    }

	@Override
    public void encodeInto(ChannelHandlerContext ctx, IPacket msg, ByteBuf target) throws Exception
    {
        msg.encodeInto(ctx, target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, IPacket msg)
    {
        msg.decodeInto(ctx, source);
    }
    
    public void sendToServer(IPacket message)
    {
    	if (FMLCommonHandler.instance().getSide() != Side.CLIENT) return;
    	this.channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        this.channels.get(Side.CLIENT).writeOutbound(message);
    }
    
    public void sendToPlayer(IPacket message, EntityPlayerMP player) {
    	 this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
         this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
         this.channels.get(Side.SERVER).writeOutbound(message);
    }

}
