package de.katzenpapst.proceduralgalaxy;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock;
import micdoodle8.mods.galacticraft.core.tile.TileEntityAdvanced;

public class TestTileEnt extends TileEntityAdvanced {

	@Override
    public double getPacketRange()
    {
        return 12.0D;
    }

    @Override
    public int getPacketCooldown()
    {
        return 3;
    }

    @Override
    public boolean isNetworkedTile()
    {
        return true;
    }

	

}
