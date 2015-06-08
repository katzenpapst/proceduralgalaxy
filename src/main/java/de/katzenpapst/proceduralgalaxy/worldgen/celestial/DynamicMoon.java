package de.katzenpapst.proceduralgalaxy.worldgen.celestial;

import micdoodle8.mods.galacticraft.api.galaxies.Moon;

public class DynamicMoon extends Moon implements IDynamicCelestialBody {

	String displayName;
	
	public DynamicMoon(String name) {
		super(name);
		displayName = name;
	}
	

	
	@Override
	public String getLocalizedName() {
		return this.displayName;
	}
	
	public void setDisplayName(String name) {
		displayName = name;
	}

}
