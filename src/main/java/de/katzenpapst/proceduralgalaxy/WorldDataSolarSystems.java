package de.katzenpapst.proceduralgalaxy;

import java.util.ArrayList;

import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class WorldDataSolarSystems extends WorldSavedData {
	
	public static final String saveDataID = "PGSolarSystemData";
	
	protected ArrayList<SolarSystemData> solarSystems;

	public WorldDataSolarSystems(String p_i2141_1_) {
		super(p_i2141_1_);
		
		solarSystems = new ArrayList<SolarSystemData>(); 
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		
		// "10" seems to mean "list with elements of type Compound"
		// see NBTBase::getId
		NBTTagList nbtList = nbt.getTagList("solarSystems", 10);
		
		for(int i=0;i<nbtList.tagCount();i++) {
			NBTTagCompound curNbt = nbtList.getCompoundTagAt(i);
			SolarSystemData data = new SolarSystemData();
			data.loadFromNbt(curNbt);
			solarSystems.add(data);
		}
		// at this point I should actually create the systems
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	
		
		NBTTagList nbtList = new NBTTagList();
		
		for(SolarSystemData d : solarSystems) {
			NBTTagCompound curNbt = new NBTTagCompound();
			d.saveToNbt(curNbt);
			nbtList.appendTag(curNbt);
		}
		nbt.setTag("solarSystems", nbtList);
		
	}
	
	public void addSolarSystemData(SolarSystemData d) {
		solarSystems.add(d);
		this.markDirty();
	}
	
	public static WorldDataSolarSystems initWorldData(World world)
    {
		WorldDataSolarSystems worldData = (WorldDataSolarSystems) world.perWorldStorage.loadData(WorldDataSolarSystems.class, saveDataID);
		// WorldDataSolarSystems worldData = (WorldDataSolarSystems) world.loadItemData(WorldDataSolarSystems.class, WorldDataSolarSystems.saveDataID);

        if (worldData == null)
        {
            worldData = new WorldDataSolarSystems(WorldDataSolarSystems.saveDataID);
            world.perWorldStorage.setData(saveDataID, worldData);
            // world.setItemData(WorldDataSolarSystems.saveDataID, worldData);
            // worldData.dataCompound = new NBTTagCompound();
            worldData.markDirty();
        }

        return worldData;
    }

}
