package de.katzenpapst.proceduralgalaxy.data;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

public class SolarSystemData extends GenericData {
	public Vector3 mapPosition = new Vector3(0,0,0);
	
	// something like an ID for my SolarSystems
	public int solarSystemID = 0;
	
	public StarData mainStar = null;
	
	public ArrayList<PlanetData> planets = new ArrayList<PlanetData>(); 
	
	@Override
	public boolean loadFromNbt(NBTTagCompound nbt) {
		if(!super.loadFromNbt(nbt)) return false;
		
		
		
		NBTTagCompound vec = nbt.getCompoundTag("mapPosition");
		
		mapPosition = loadVector3(vec);
		
		solarSystemID = nbt.getInteger("solarSystemID");
		
		mainStar = new StarData();
		mainStar.loadFromNbt(nbt.getCompoundTag("mainStar"));
		
		NBTTagList nbtList = nbt.getTagList("planets", 10);
		planets.clear();
		
		for(int i=0;i<nbtList.tagCount();i++) {
			NBTTagCompound curNbt = nbtList.getCompoundTagAt(i);
			PlanetData data = new PlanetData();
			data.loadFromNbt(curNbt);
			planets.add(data);
		}
		
		return true;
	
	}
	
	/**
	 * NBT Saving
	 */
	public void saveToNbt(NBTTagCompound nbt) {
		super.saveToNbt(nbt);
		
		nbt.setTag("mapPosition", saveVector3(mapPosition));
		nbt.setInteger("solarSystemID", solarSystemID);
		
		NBTTagCompound starData = new NBTTagCompound ();
		mainStar.saveToNbt(starData);
		nbt.setTag("mainStar", starData);
		
		NBTTagList nbtList = new NBTTagList();
		
		for(PlanetData d : planets) {
			NBTTagCompound curNbt = new NBTTagCompound();
			d.saveToNbt(curNbt);
			nbtList.appendTag(curNbt);
		}
		nbt.setTag("planets", nbtList);
	}
}
