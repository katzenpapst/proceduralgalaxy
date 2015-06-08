package de.katzenpapst.proceduralgalaxy;

import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvanced;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;




public class TestBlock extends BlockAdvanced {

	private IIcon iconMachineSide;
	private IIcon iconInput;
	private IIcon iconOutput;
	
	protected TestBlock() {
		super(Material.wood);

		setHardness(0.5F);
		setStepSound(Block.soundTypeGravel);
        setBlockName("dafuqBlockTest");
        setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.iconMachineSide = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_collector_fan");
        this.iconInput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_oxygen_output");
        this.iconOutput = par1IconRegister.registerIcon(GalacticraftCore.TEXTURE_PREFIX + "machine_input");
    }
	
	@Override
    public IIcon getIcon(int side, int metadata)
    {
        if (side == metadata + 2)
        {
            return this.iconOutput;
        }
        else if (side == ForgeDirection.getOrientation(metadata + 2).getOpposite().ordinal())
        {
            return this.iconInput;
        }

        return this.iconMachineSide;
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TestTileEnt();
	}

	 @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
		 entityPlayer.openGui(ProceduralGalaxy.instance, 1, world, x, y, z);
		 return true;
    }
}
