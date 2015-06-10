package de.katzenpapst.proceduralgalaxy.data;

import micdoodle8.mods.galacticraft.api.vector.Vector3;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class GenericData {
	// my classes will return this instead of the localized name
	public String displayName = "";
	// for GC-internal stuff
	public String unlocalizedName = "";
	
	/**
	 * Blank constructor
	 */
	public GenericData() {
		
	}
	
	/**
	 * Should read an NBT compound and fill 
	 * @param nbt
	 */
	public boolean loadFromNbt(NBTTagCompound nbt) {
		
		displayName = nbt.getString("displayName");
		unlocalizedName = nbt.getString("unlocalizedName");
		return true;
		
	}
	
	/**
	 * NBT Saving
	 * @param nbt TODO
	 */
	public void saveToNbt(NBTTagCompound nbt) {
		nbt.setString("unlocalizedName", unlocalizedName);
		nbt.setString("displayName", displayName);
	}
	
	/**
	 * Helper for vector loading
	 * @param nbt
	 * @return
	 */
	protected static Vector3 loadVector3(NBTTagCompound nbt) {
		Vector3 vec = new Vector3 ();
		vec.x = nbt.getDouble("x");
		vec.y = nbt.getDouble("y");
		vec.z = nbt.getDouble("z");
		return vec;
	}
	
	protected static NBTTagCompound saveVector3(Vector3 vec) {
		NBTTagCompound result = new NBTTagCompound();
		
		result.setDouble("x", vec.x);
		result.setDouble("y", vec.y);
		result.setDouble("z", vec.z);
		return result;
	}
}
