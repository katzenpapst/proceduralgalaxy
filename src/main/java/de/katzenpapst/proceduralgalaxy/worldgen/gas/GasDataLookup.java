package de.katzenpapst.proceduralgalaxy.worldgen.gas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import de.katzenpapst.proceduralgalaxy.util.RandomGenerator;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
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
	
	/**
	 * Returns a NON-NORMALIZED vector3
	 * @param gas
	 * @return
	 */
	public static Vector3 getGasColor(IAtmosphericGas gas) {
		Vector3 color = null;//new Vector3(0,0,0);
		//  GalacticraftCore.planetOverworld.atmosphereComponent(IAtmosphericGas.NITROGEN).atmosphereComponent(IAtmosphericGas.OXYGEN).atmosphereComponent(IAtmosphericGas.ARGON).atmosphereComponent(IAtmosphericGas.WATER);
		switch(gas) {
		// I guess I'll just have to tweak these until it works
		case NITROGEN:
			color = new Vector3(255,169,238);
			break;
		case ARGON:
			color = new Vector3(255, 164, 232);
			break;
		case CO2:
			color = new Vector3(253, 211, 241);
			break;
		case HELIUM:
			color = new Vector3(253, 211, 241);
			break;
		case HYDROGEN:
			color = new Vector3(255, 193, 147);
			break;
		case METHANE:
			color = new Vector3(245, 202, 139);
			break;
		case OXYGEN:
			color = new Vector3(0, 204, 255);
			break;
		case WATER:
			color = new Vector3(208, 228, 255);			
			break;
		default:
			color = new Vector3(255, 255, 255);
			break;
		}
		// color.normalize();
		return color;
	}
	
	/**
	 * Divides all components by 255
	 * @param input
	 * @return
	 */
	public static Vector3 normalizeColor(Vector3 input) {
		input.scale(1.0 / 255.0);
		return input;
	}
	
	public static Vector3 getAtmosphereColor(ArrayList<IAtmosphericGas> gasses) {
		Vector3 color = new Vector3(0,0,0);
		double factor;
		
		for(int i=0;i<gasses.size();i++) {
			IAtmosphericGas curGas = gasses.get(i);
			// should make 0.8 for the first one, 0.2 for the second one, etc. might work...
			factor = -0.6*(i+1)+1.4;
			Vector3 curGasColor = getGasColor(curGas);
			curGasColor.scale(factor);
			color.translate(curGasColor);
		}
		
		color = normalizeColor(color);
		
		return color;
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
