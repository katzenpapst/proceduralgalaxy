package de.katzenpapst.proceduralgalaxy.data;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PlanetData extends LandeableData {
	
	public ArrayList<MoonData> moons = new ArrayList<MoonData>(); 
	
	
	@Override
	public boolean loadFromNbt(NBTTagCompound nbt) {
    	if(!super.loadFromNbt(nbt)) return false;
    	
		NBTTagList nbtList = nbt.getTagList("moons", 10);
		moons.clear();
		
		for(int i=0;i<nbtList.tagCount();i++) {
			NBTTagCompound curNbt = nbtList.getCompoundTagAt(i);
			MoonData data = new MoonData();
			data.loadFromNbt(curNbt);
			moons.add(data);
		}
 
		return true;
	
	}
	
	@Override
	public void saveToNbt(NBTTagCompound nbt) {
		super.saveToNbt(nbt);
	
		NBTTagList nbtList = new NBTTagList();
		
		for(MoonData d : moons) {
			NBTTagCompound curNbt = new NBTTagCompound();
			d.saveToNbt(curNbt);
			nbtList.appendTag(curNbt);
		}
		nbt.setTag("moons", nbtList);
	}
}
