package de.katzenpapst.proceduralgalaxy.worldgen.celestial;

import micdoodle8.mods.galacticraft.api.galaxies.Star;

public class DynamicStar extends Star implements IDynamicCelestialBody {

	String displayName;
	
	public DynamicStar(String name) {
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
