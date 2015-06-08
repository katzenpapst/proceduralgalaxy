package de.katzenpapst.proceduralgalaxy.data;

import java.util.ArrayList;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class LandeableData extends CelestialBodyData {
	
	public enum LandeableType {
		// rocky planet/moon
		LT_ROCKY,
		// gas giant
		LT_GAS,
		// micromoon. I hope I manage to do it... 
		LT_MICRO
	}
	
	public LandeableType landeableType = LandeableType.LT_ROCKY;
	
	// it should be derived from stuff like size if there are any atmospheres at all
	// but the actual list still needs to be saved here
	public ArrayList<IAtmosphericGas> atmosphere = new ArrayList<IAtmosphericGas>();
	
	// seems like GC has no concept of density
	// I hereby define it as 1 = exactly like overworld, 0 = no atmosphere, etc
	// this should definitely affect meteorFrequency and sky color
	public double atmosphericPressure = 1.0F;
	// public float soundVolumeReduction = 1; // depending on density, higher -> more quiet, 0 < x < 1 louder
	// public bool hasBreathableAtmosphere = false; // idea: density within a certain tolerance, oxygen on 2. or 3. position, no poisonous gases
	// public float windLevel = 1; // apparently used for flag only, well
	
	// this is supposed to be the planet/moon's size, relative to OW
	public double bodyRadius = 1.0F; 
	
	
    // todo figure out how to auto-find free IDs
	// i guess I could consider 0 to be "not set", even if that's the overworld
    public int dimensionID = 0;
    
    // other important stuff
    // these will be derived from the atmosphere:
    // public Vector3 fogColor; 
    // public Vector3 skyColor;
    // public boolean canRainOrSnow;
    // public boolean hasSunset
    // public double meteorFrequency = 0;
    
    public long dayLength = 24000;
    
    // should be derived from planet size. also, figure out how much Overworld has
    // public double horizon; // return this == FLAT ? 0.0D : 63.0D;
    
    public double temperature = 1.0F;
        
    // again, relative
    public double density = 1.0F;
    // public float fuelUsageMultiplier = 1; // derived from gravity and air density
    // public float fallDamageMultiplier = 1; // dito
    
    // no idea what this means... moon has 600, mars 800, asteroids too? well...
    // public int height = 800; 
    
    // public float solarEnergyMultiplier = 1; // should be derived from distance and air density, as soon as I figured that one out
    // public float thermalLevelModifier = 0; // 0 = nothing, negative = cold, positive = hot. figure something out

    // if true, stuff like coal ore, limestone etc is allowed to generate,
    // meaning the planet had had life for a long time
    // public boolean hasOrganicStone = false; but maybe this should be determined otherwise
    
    // todo add ore lists, mob lists, villager lists  ets
    
    
    /**
     * Calculates the value which should actually be set for a planet/moon
     * @return
     */
    public double getGravitySubtrahend() {
    	/*
    	 * c=0.08D 
    	 * gravity = c - gravitySubtrahend
    	 * gravity = c * gravityFactor
    	 * c - gravitySubtrahend = c * gravityFactor
    	 * gravitySubtrahend = c*(1-gravityFactor)
    	 */
    	return 0.08D * (1-getGravityFactor());
    }
    
    /**
     * Calculates the relative gravity from mass and radius
     * requires radius and density to be set
     * @return
     */
    public double getGravityFactor() {
    	// in relative units, this is quite simple now
    	return getMass() / Math.pow(bodyRadius, 2);
    }
    
    /**
     * Calculates the body's relative mass from volume and density
     * requires radius and density to be set
     * @return
     */
    public double getMass() {
    	return density*getVolume();
    }
    
    /**
     * Calculates the body's relative volume from the radius
     * Lol, if this is minecraft, maybe assume planets to be cubes?
     * @return
     */
    public double getVolume() {
    	return 4/3*Math.PI*Math.pow(bodyRadius, 3);
    }

    
	
	@Override
	public boolean loadFromNbt(NBTTagCompound nbt) {
		if(!super.loadFromNbt(nbt)) return false;
		
	
		dimensionID = nbt.getInteger("dimensionID");
		
		atmosphericPressure = nbt.getDouble("atmosphericDensity");
		bodyRadius = nbt.getDouble("bodySize");
		dayLength = nbt.getLong("dayLength");
		
		landeableType = LandeableType.valueOf(nbt.getString("landeableType"));

		
		// do the atmospheres
		NBTTagList atmoList = nbt.getTagList("atmosphere", 8);
		for(int i=0;i<atmoList.tagCount();i++) {
			String curName = atmoList.getStringTagAt(i);
			IAtmosphericGas gas;
			try {
				gas = IAtmosphericGas.valueOf(curName);
			} catch(IllegalArgumentException ex) {
				System.out.print("Illegal gas for Planet "+this.displayName+": "+curName);
				continue;
			}
			atmosphere.add(gas);
		}
		
		
		
	
		return true;
	
	}
	
	@Override
	public void saveToNbt(NBTTagCompound nbt) {
		super.saveToNbt(nbt);
		
		nbt.setInteger("displayName", dimensionID);
		nbt.setDouble("atmosphericDensity",atmosphericPressure);
		nbt.setDouble("bodySize",bodyRadius);
		nbt.setLong("dayLength",dayLength);
		
		
		nbt.setString("landeableType", landeableType.name());

		
		// atmosphere
		NBTTagList atmoList = new NBTTagList ();		
		for(IAtmosphericGas gas: atmosphere) {
			NBTTagString strTag = new NBTTagString(gas.name());
			atmoList.appendTag(strTag);
		}
		nbt.setTag("atmosphere", atmoList);
		
		
	}
}
