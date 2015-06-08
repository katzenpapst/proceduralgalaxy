package de.katzenpapst.proceduralgalaxy.data;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class StarData extends CelestialBodyData {
	
	// star size, relative to the OW's sun
	public float size = 1.0F;
	
	// star's temperature/brightness relative to the OW's sun
	// I suppose, 0 = it's basically a moon
	// for other values, use this for calculate brightness depending on the distance
	public float brightness = 1.0F;
	// the formula is L / (4*Pi*r^2), where L is the Star's luminosity and r the distance from it
	// since 4*Pi is a constant, normalize to brightness / distance^2, should yield a multiplicator
	
	
	
	@Override
	public boolean loadFromNbt(NBTTagCompound nbt) {
    	if(!super.loadFromNbt(nbt)) return false;
    	
    	size = nbt.getFloat("size");
    	brightness = nbt.getFloat("brightness");
    	
		
		
 
		return true;
	
	}
	
	@Override
	public void saveToNbt(NBTTagCompound nbt) {
		super.saveToNbt(nbt);
		
		nbt.setFloat("size", size);
    	nbt.setFloat("brightness", brightness);
	
		
	}
	
	/**
	 * Returns the actual brightness value as it should be used for calculation    
	 * @return
	 */
	public float getWeightedBrightness() {
		// I'm not quite sure about this, I couldn't find any data on that
		return size*brightness;
	}
}
