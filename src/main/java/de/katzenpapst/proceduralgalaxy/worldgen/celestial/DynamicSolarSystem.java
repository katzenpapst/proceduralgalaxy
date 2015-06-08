package de.katzenpapst.proceduralgalaxy.worldgen.celestial;

import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;

public class DynamicSolarSystem extends SolarSystem {

	protected String displayName = "";
	
	public DynamicSolarSystem(String solarSystem, String parentGalaxy) {
		super(solarSystem, parentGalaxy);
		displayName = solarSystem;
	}
	
	@Override
	public String getLocalizedName() {
		return this.displayName;
	}
	
	public void setDisplayName(String name) {
		displayName = name;
	}
	

}
