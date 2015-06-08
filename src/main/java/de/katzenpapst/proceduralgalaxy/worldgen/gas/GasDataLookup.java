package de.katzenpapst.proceduralgalaxy.worldgen.gas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import de.katzenpapst.proceduralgalaxy.util.RandomGenerator;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;

public class GasDataLookup {
	// todo also add pressure later
	
	private static HashMap<IAtmosphericGas, GasInfo> gasData = null;
	
	private static void _initData() {
		gasData = new HashMap<IAtmosphericGas, GasInfo>();
		
		gasData.put(IAtmosphericGas.NITROGEN, new GasInfo(63.15F, 77.355F));
		gasData.put(IAtmosphericGas.OXYGEN, new GasInfo(54.36F, 90.188F));
		gasData.put(IAtmosphericGas.CO2, new GasInfo(216.6F, 194.7F));
		gasData.put(IAtmosphericGas.WATER, new GasInfo(273F, 373F));
		gasData.put(IAtmosphericGas.METHANE, new GasInfo(90.7F, 111.66F));
		gasData.put(IAtmosphericGas.HYDROGEN, new GasInfo(13.99F, 20.271F));
		gasData.put(IAtmosphericGas.HELIUM, new GasInfo(0F, 4.222F));
		gasData.put(IAtmosphericGas.ARGON, new GasInfo(83.81F, 87.302F));
		
	}
	
	public static ArrayList<IAtmosphericGas> getValidGasses(float temperature, int numGasses, RandomGenerator gen) {
		ArrayList<IAtmosphericGas> result = new ArrayList<IAtmosphericGas>();
		
		if(gasData == null) {
			_initData();
		}
		
		// hm, fill the result with all valid gasses first


		for (HashMap.Entry<IAtmosphericGas, GasInfo> entry : gasData.entrySet())
		{
			if(entry.getValue().boilingPoint < temperature) {
				result.add(entry.getKey());
			}
		}
		
		// shuffle them?
		Collections.shuffle(result, gen);

		// if we have too many, remove them
		while(result.size() > numGasses) {
			result.remove(0);
		}
		
		return result;
	}
}
