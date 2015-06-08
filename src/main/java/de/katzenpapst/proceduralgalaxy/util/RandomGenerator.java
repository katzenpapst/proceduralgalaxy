package de.katzenpapst.proceduralgalaxy.util;

import java.util.Random;

import micdoodle8.mods.galacticraft.api.vector.Vector3;

public class RandomGenerator extends Random {
	
	public RandomGenerator(long seed) {
		super(seed);
	}

	/**
	 * Returns an int with between min and max 
	 * 
	 * @param min inclusive
	 * @param max exclusive
	 * @return
	 */
	public int nextInt(int min, int max) {
		return this.nextInt(max-min)+min;
	}
	
	public double nextDouble(double min, double max) {
		return this.nextDouble()*(max-min) + min;
	}
	
	/**
	 * "Rooftop Distribution", aka, max at 0, then linear falloff to 1 and -1
	 * A very crude approximation for gaussian distribution, but should
	 * be sufficient for some cases, and most certainly way faster
	 * 
	 * @return
	 */
	public double nextRooftop(){
		return this.nextDouble()-this.nextDouble();
	}
}
