package de.katzenpapst.proceduralgalaxy.gui;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return getClientGuiElement(ID, player, world, new Vector3(x, y, z));
	}
	
	 @SideOnly(Side.CLIENT)
    private Object getClientGuiElement(int ID, EntityPlayer player, World world, Vector3 position)
    {
		if (ID == 1)
        {
            return new GuiObservatory(null);
        }
		 return null;
	 
    }

}
