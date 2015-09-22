package de.katzenpapst.proceduralgalaxy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class SolarSystemSavedData extends WorldSavedData {
	

	public static final String saveDataID = "PGSolarSystemData";

	public SolarSystemSavedData(String p_i2141_1_) {
		super(p_i2141_1_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void readFromNBT(NBTTagCompound p_76184_1_) {
		ProceduralGalaxy.instance.getSolarSystemManager().readFromNBT(p_76184_1_);
	}

	@Override
	public void writeToNBT(NBTTagCompound p_76187_1_) {
		ProceduralGalaxy.instance.getSolarSystemManager().writeToNBT(p_76187_1_);
	}

}
