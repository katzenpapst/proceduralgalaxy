package de.katzenpapst.proceduralgalaxy.data;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;


public class CelestialBodyData extends GenericData {
	
	public float relativeSize = 1.0F;
	// I'll try to use the value for both parts of the class
	public float distanceFromCenter = 1.0F;
    public float relativeOrbitTime = 1.0F;
    public float phaseShift = 0.0F;
    //public String discovererName = null;
    public UUID discovererUUID = null;
    


    // this will be relative to my mod
    public String celestialBodyIcon;

    // I should probably set these colors somewhere else. 
    /*public float ringColorR = 1.0F;
    public float ringColorG = 1.0F;
    public float ringColorB = 1.0F;*/
    
    @Override
	public boolean loadFromNbt(NBTTagCompound nbt) {
    	if(!super.loadFromNbt(nbt)) return false;
    	
    	relativeSize = nbt.getFloat("relativeSize");
    	distanceFromCenter = nbt.getFloat("distanceFromCenter");
    	relativeOrbitTime = nbt.getFloat("relativeOrbitTime");
    	phaseShift = nbt.getFloat("phaseShift");
    	
    	celestialBodyIcon = nbt.getString("celestialBodyIcon");
    	
    	if(nbt.hasKey("discovererUUID")) {
    		discovererUUID = UUID.fromString(nbt.getString("discovererUUID"));
    	} else {
    		discovererUUID = null;
    	}

		return true;
	
	}
	
	@Override
	public void saveToNbt(NBTTagCompound nbt) {
		super.saveToNbt(nbt);
		nbt.setFloat("relativeSize", relativeSize);
    	nbt.setFloat("distanceFromCenter", distanceFromCenter);
    	nbt.setFloat("relativeOrbitTime",relativeOrbitTime);
    	nbt.setFloat("phaseShift",phaseShift);
    	
    	nbt.setString("celestialBodyIcon", celestialBodyIcon);
    	if(discovererUUID != null) {
    		nbt.setString("discovererUUID",discovererUUID.toString());
    	}
		
		
	}
}
