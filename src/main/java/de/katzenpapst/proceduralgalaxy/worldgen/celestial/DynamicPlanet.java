package de.katzenpapst.proceduralgalaxy.worldgen.celestial;

import micdoodle8.mods.galacticraft.api.galaxies.Planet;

public class DynamicPlanet extends Planet implements IDynamicCelestialBody {

	String displayName;
		
	public DynamicPlanet(String name) {
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
