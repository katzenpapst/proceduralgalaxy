package de.katzenpapst.proceduralgalaxy.network;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import micdoodle8.mods.galacticraft.core.network.NetworkUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import micdoodle8.mods.galacticraft.core.network.IPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;


public class SimplePacketPG implements IPacket {

	public static enum EnumSimplePacketPG
    {
		// SERVER
        S_GENERATE_SOLAR_SYSTEM(Side.SERVER, String.class),
        
        // CLIENT
        C_SOLAR_SYSTEM_GENERATED(Side.CLIENT, SolarSystemData.class);
    
		private Side targetSide;
        private Class<?>[] decodeAs;

        private EnumSimplePacketPG(Side targetSide, Class<?>... decodeAs)
        {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide()
        {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses()
        {
            return this.decodeAs;
        }
    }
	
	private EnumSimplePacketPG type;
    private List<Object> data;
    
    public SimplePacketPG() {
    	
    }
    
    public SimplePacketPG(EnumSimplePacketPG packetType, Object[] data)
    {
        this(packetType, Arrays.asList(data));
    }

    public SimplePacketPG(EnumSimplePacketPG packetType, List<Object> data)
    {
        if (packetType.getDecodeClasses().length != data.size())
        {
            // GCLog.info("Simple Packet Core found data length different than packet type");
            new RuntimeException().printStackTrace();
        }

        this.type = packetType;
        this.data = data;
    }
	
	@Override
    public void encodeInto(ChannelHandlerContext context, ByteBuf buffer)
    {
        buffer.writeInt(this.type.ordinal());

        try
        {
            NetworkUtil.encodeData(buffer, this.data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext context, ByteBuf buffer)
    {
        this.type = EnumSimplePacketPG.values()[buffer.readInt()];

        if (this.type.getDecodeClasses().length > 0)
        {
            this.data = NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
        }
    }
        

    @SideOnly(Side.CLIENT)
	@Override
	public void handleClientSide(EntityPlayer player) {
		switch(this.type) {
		case C_SOLAR_SYSTEM_GENERATED: 
			
			break;
		default:
			break;
		}

	}

    // @SideOnly(Side.SERVER)
	@Override
	public void handleServerSide(EntityPlayer player) {
		/*EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        GCPlayerStats stats = GCEntityPlayerMP.getPlayerStats(playerBase);*/
    	switch(this.type) {
    	case S_GENERATE_SOLAR_SYSTEM:
    		break;
		default:
			break;
    	}
		
	}

}
