package de.katzenpapst.proceduralgalaxy.worldgen;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Vector2d;

import org.lwjgl.util.vector.Vector2f;

import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;
import de.katzenpapst.proceduralgalaxy.config.ConfigManager;
import de.katzenpapst.proceduralgalaxy.data.CelestialBodyData;
import de.katzenpapst.proceduralgalaxy.data.LandeableData;
import de.katzenpapst.proceduralgalaxy.data.MoonData;
import de.katzenpapst.proceduralgalaxy.data.PlanetData;
import de.katzenpapst.proceduralgalaxy.data.SolarSystemData;
import de.katzenpapst.proceduralgalaxy.data.StarData;
import de.katzenpapst.proceduralgalaxy.data.LandeableData.LandeableType;
import de.katzenpapst.proceduralgalaxy.exception.CannotGenerateException;
import de.katzenpapst.proceduralgalaxy.util.RandomGenerator;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicPlanet;
import de.katzenpapst.proceduralgalaxy.worldgen.celestial.DynamicStar;
import de.katzenpapst.proceduralgalaxy.worldgen.gas.GasDataLookup;
import de.katzenpapst.proceduralgalaxy.worldgen.helper.NumeralHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector2;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;

public class SolarSystemGenerator {
	
	long worldSeed;
	// aka highest possible coordinate
	double max_coord = 5.0D;
	// lowest possible coordinate
	double min_coord = 0.0D;
	
	double min_distance = 1;
	
	public static final int SALT = 133754689;
	public static final int NUM_ATTEMPTS = 10;
	public static final int MIN_PLANET_SIZE = 1;
	public static final int MAX_PLANET_SIZE = 10;
	// if a size is > this, it will be considered a gas giant
	public static final int ROCKY_PLANET_MAX = 6;
	
	public static final double MICRO_PLANET_MAX = 1;
	
	// for relative stuff, how big should
	public static final double OVERWORLD_SIZE = 4;
	public static final double MIN_PLANET_DISTANCE = 0.3;
	public static final double MAX_PLANET_DISTANCE = 3;
	// public static final double GAS_PLANET_MIN = 3;
	
	public static final double MIN_ROCKY_DENSITY = 0.3; 
	public static final double MAX_ROCKY_DENSITY = 1.0;
	
	public static final double MIN_ROCKY_PRESSURE = 0.0; 
	public static final double MAX_ROCKY_PRESSURE = 5.0;
	
	public static final double ROCKY_PRESSURE_UNCERTAINTY = 1.5;
	public static final double GAS_PRESSURE_UNCERTAINTY = 5;
	
	public static final double MIN_GAS_PRESSURE = 20.0; 
	public static final double MAX_GAS_PRESSURE = 150.0;
	
	public static final int MIN_DAY_LENGTH = 2;
	public static final int MAX_DAY_LENGTH = 64;
	
	public static final double MIN_MOON_DISTANCE = 4;
	public static final double MAX_MOON_DISTANCE = 30;
	
	private double _pressure_rocky_m;
	private double _pressure_rocky_t;
	
	private double _pressure_gas_m;
	private double _pressure_gas_t;
	
	
	enum PlanetGenType {
		PGT_ICE,
		PGT_DESERT,
		PGT_LIFE_O2,
		PGT_LIFE_CH4
	}
	
	
	private String discoverer = null;

	private float hZoneWidth = ProceduralGalaxy.instance.getConfigManager().getHabitableZoneWidth();
	
	
	
	protected ConfigManager configManager;
	
	protected String[] sunTextures = {
		"sun-blue",
		"sun-red1",
		"sun-red2",
		"sun-white",
		"sun-yellow",
	};
	
	
	Map <String, SolarSystem> registeredSystems;

	
	public SolarSystemGenerator() {
		
		worldSeed = Minecraft.getMinecraft().theWorld.getSeed();
		configManager = ProceduralGalaxy.instance.getConfigManager();
		
		_pressure_rocky_m = (MAX_ROCKY_PRESSURE-MIN_ROCKY_PRESSURE)/(ROCKY_PLANET_MAX-MICRO_PLANET_MAX);
		_pressure_rocky_t = MAX_ROCKY_PRESSURE - _pressure_rocky_m * ROCKY_PLANET_MAX;
		
		_pressure_gas_m = (MIN_GAS_PRESSURE-MAX_ROCKY_PRESSURE)/(MAX_PLANET_SIZE-ROCKY_PLANET_MAX);
		_pressure_gas_t = MAX_ROCKY_PRESSURE - _pressure_rocky_m * MAX_PLANET_SIZE;
		
		updateSystemList();
	}
	
	public void setDiscoverer(String name) {
		discoverer = name;
	}
	
	/**
	 * Hack, but meh.
	 * You should call this if you recycle this instance before generating
	 */
	public void updateSystemList() {
		registeredSystems = GalaxyRegistry.getRegisteredSolarSystems();
	}
	
	
	/**
	 * Now, my idea is: everything in the system is determined by it's number (beginning at 0)
	 * and the worldseed, so that you can get a lot of infos about a system without actually loading it
	 * 
	 * @TODO figure out how to make this run server-side only
	 * 
	 * @param nr
	 * @return
	 */
	public SolarSystemData generate(int nr) throws CannotGenerateException {
		
		long seed = worldSeed ^ nr ^ SALT;
		RandomGenerator generator = new RandomGenerator(seed);
		// first the solarsystem
		
		SolarSystemData sys = new SolarSystemData();
		
		String systemName = getSystemName(generator);
		sys.displayName = systemName;
		sys.index = nr;
		sys.mapPosition  = getSystemPosition(generator);
		sys.unlocalizedName = systemName.toLowerCase()+"_system";

		// now the star
		StarData star = new StarData();
		sys.mainStar = star;
		star.size = 0.1F+generator.nextFloat();
		star.brightness = (float) generator.nextDouble(0.1, 2.5);
		
		String textureName = "";
		int randIndex = generator.nextInt(sunTextures.length); // it's exclusive
		
		// todo add stars with different sizes and temperatures
		star.celestialBodyIcon = sunTextures[randIndex];
		star.displayName = systemName;
		star.relativeSize = 1F+star.size;
		star.unlocalizedName = systemName.toLowerCase()+"_star";
		
		if(discoverer != null) {
			star.discovererName = discoverer;
		}
		
		
		// now generate planets
		sys.planets = generatePlanets(generator, star);
		
		return sys;
	}
	
	protected ArrayList<PlanetData> generatePlanets(RandomGenerator gen, StarData star) {
		ArrayList<PlanetData> result = new ArrayList<PlanetData>();
		// stuff
		int numPlanets = gen.nextInt(configManager.getMinNumPlanets(), configManager.getMaxNumPlanets()+1);
		// distance is a float, 1 = "like OW"
		// let's say min. distance is 0.3, maximum is 3
		// get the orbit distance
		
		double stepSize = (MAX_PLANET_DISTANCE-MIN_PLANET_DISTANCE)/numPlanets;
		double stepSizeHalf = stepSize/2;
		double brightness = star.getWeightedBrightness();
		
		
				
		for(int i=0;i<numPlanets;i++) {
			PlanetData pd = new PlanetData();
			// orbit distance with a little random deviation
			// I don't think it's necessary to go full gaussian on this one
			// double orbitDistance = MIN_PLANET_DISTANCE + stepSizeHalf + i*stepSize + stepSizeHalf*gen.nextRooftop();
			// the formula above, simplified. 
			// the point is, the planets are supposed to be halfway between n and n+1, +- half the width
			
			double orbitDistance = MIN_PLANET_DISTANCE + stepSize * ( (gen.nextRooftop() + 1)/2 + i );
			pd.distanceFromCenter = (float)orbitDistance;
			
			// orbital speed has been found to be a function of the distance, namely
			// speed = C * 1/sqrt(distance)
			// for OW this is 1 for C=1, so I guess that's ok so
			// but, it seems that for GC, 2 means "twice as slow"
			// so, removing the 1/
			pd.relativeOrbitTime = (float) Math.sqrt(orbitDistance);
			
			// phase shift in pi
			pd.phaseShift = (float) gen.nextDouble(0, 2*Math.PI);
			
			// the formula is L / (4*Pi*r^2), where L is the Star's luminosity and r the distance from it
			// since 4*Pi is a constant, normalize to brightness / distance^2, should yield a multiplicator

			
			// this is, in fact, the temperature related to the OW, or at least the formula say so
			// temperature = b*C / r^2
			// with b being the luminosity of the sun and C some other constant
			// and r being the relative distance from the sun
			double curBrightness = brightness / (orbitDistance*orbitDistance / MAX_PLANET_DISTANCE);
			
			generateLandeable(pd, gen, curBrightness,star,i, MAX_PLANET_SIZE);
			
			
			pd.moons = generateMoons(gen, pd, curBrightness);
			
			result.add(pd);
		}
		return result;
	}
	
	protected ArrayList<MoonData> generateMoons(RandomGenerator gen, PlanetData parent, double parentBrightness) {
		ArrayList<MoonData> result = new ArrayList<MoonData>();
		
		int parentRadiusAbsolute = (int) (((PlanetData) parent).bodyRadius*OVERWORLD_SIZE);
		
		int maxSize =(int) (parentRadiusAbsolute / 2);
		ConfigManager cfgmgr = ProceduralGalaxy.instance.getConfigManager();
		// I just do it linear, aka, a 2-planet can have 1 moon at most, 5-planet 4, etc
		int maxNumMoons = Math.min(parentRadiusAbsolute-1, cfgmgr.getMaxNumMoons());
		int minNumMoons = cfgmgr.getMinNumMoons();
		
		// sanity checks. maxSize <= 0 could be redundant here
		if(maxSize <= 0 || parentRadiusAbsolute == 1 || maxNumMoons < minNumMoons) {
			// 1 should not have moons. ever. even if minMoons is set to > 0
			return result; 
		}
		
		

		// no gas giants as moons
		if(maxSize > ROCKY_PLANET_MAX)
			maxSize = ROCKY_PLANET_MAX;
		
		int numMoons = gen.nextInt(minNumMoons, maxNumMoons);
		
		double stepSize = (MAX_MOON_DISTANCE-MIN_MOON_DISTANCE)/numMoons;
		double stepSizeHalf = stepSize/2;
		
		
		for(int i=0;i<numMoons;i++) {
			MoonData md = new MoonData();
			// the regular moon is at 13F. confusing.
			double orbitDistance = MIN_MOON_DISTANCE + stepSize * ( (gen.nextRooftop() + 1)/2 + i );
			md.distanceFromCenter = (float)orbitDistance;
			
			// maybe differently for moons? they do the division for earth's moon
			md.relativeOrbitTime = (float) Math.sqrt(orbitDistance)  / 0.01F;
			
			// phase shift in pi
			md.phaseShift = (float) gen.nextDouble(0, 2*Math.PI);
			
			/*
			 * int maxSize = (int)MAX_PLANET_SIZE;
		
		if(parent instanceof PlanetData) {
			maxSize = (int) (((PlanetData) parent).bodyRadius*MAX_PLANET_SIZE / 2);
		}*/
			
			generateLandeable(md,gen,parentBrightness,parent,i,maxSize);
			
			result.add(md);
		}
		return result;
	}
	
	protected void generateLandeable(LandeableData data, RandomGenerator gen, double brightness, CelestialBodyData parent, int bodyNumber, int maxSize) {
		
		
		
		// size. 
		// while it might seem that in our system the planets are big at medium distance
		// and get smaller closer and further away from the sun, if you actually
		// plot the sizes relative to the distance, it looks rather random
		int size = 1;
		if(maxSize >= MIN_PLANET_SIZE) {
			size = gen.nextInt((int)MIN_PLANET_SIZE, maxSize+1);
		}
		
		
		
		data.bodyRadius = (double)size/OVERWORLD_SIZE;
		
		// density. hm. the data I gathered is inconclusive. 
		// it seems to me that mercury, venus and earth are actually the densest
		// I think I'll keep the max density low, because otherwise the gravity skyrockets
		// with size=2 and density=2 gravity would be f*cking 38g
		
		data.density = gen.nextDouble(MIN_ROCKY_DENSITY, MAX_ROCKY_DENSITY);
		
		// gravity goes with C * m/r²
		// mass should be proportional to volume, which goes with C * r³
		// so gravity goes with r?
		// pd.gravityFactor
		
		double gravity = data.getGravityFactor();
		// since I don't create the data, reset it a little
		data.atmosphere.clear();
		// atmosphere and type
		if(size <= MICRO_PLANET_MAX) {
			data.landeableType = LandeableType.LT_MICRO;
			// no atmosphere for these
			data.atmosphericPressure = 0;
		} else if(size <= ROCKY_PLANET_MAX) {
			data.landeableType = LandeableType.LT_ROCKY;
			// pressure on earth = 101 kPa   -> 1
			// pressure on venus = 9.2 MPa   -> 91
			// pressure on titan = 146.7 kPa -> 1,44
			// pressure on mars  = 0.636 kPa -> 0,006
			
			// okay. bigger -> more likely to have high density
			// but the uncertainty should be rather big
			// maybe with an extra peak at the higher end?
			data.atmosphericPressure = (_pressure_rocky_m * size + _pressure_rocky_t) + gen.nextDouble(-1, 1)*ROCKY_PRESSURE_UNCERTAINTY;
			if(data.atmosphericPressure < MIN_ROCKY_PRESSURE) {
				data.atmosphericPressure = MIN_ROCKY_PRESSURE;
			}
		} else {
			data.landeableType = LandeableType.LT_GAS;
			data.atmosphericPressure = (_pressure_gas_m * size + _pressure_gas_t) + gen.nextDouble(-1, 1) * GAS_PRESSURE_UNCERTAINTY;
			if(data.atmosphericPressure < MIN_GAS_PRESSURE) {
				data.atmosphericPressure = MIN_GAS_PRESSURE;
			}
		}
		// temperature. I guess pressure should influence it
		// let's try it like this. It should barely influence the temperature on low pressure,
		// but quite a lot when it's high
		data.temperature = brightness + Math.pow(gen.nextDouble(0, data.atmosphericPressure)/2,2);
		float absoluteTemperature = (float)data.temperature*288;
		// atmosphere
		if(data.atmosphericPressure > 0) {
			int numGasses = gen.nextInt(2, 6);
			data.atmosphere = GasDataLookup.getValidGasses(absoluteTemperature, numGasses, gen);
		}
		
		PlanetGenType type = getType(data);
		
		switch(data.landeableType) {
		case LT_GAS:
			data.celestialBodyIcon = "planet-gas0"+gen.nextInt(1, 4);
			break;
		case LT_MICRO:
			data.celestialBodyIcon = "micromoon";
			break;
		default:
			// icon
			switch(type) {
			
			case PGT_ICE:
				data.celestialBodyIcon = "planet-ice";
				break;
			case PGT_LIFE_CH4:
				data.celestialBodyIcon = "planet-life-ch4";
				break;
			case PGT_LIFE_O2:
				data.celestialBodyIcon = "planet-life-o2";
				break;
			case PGT_DESERT:
			default:
				//?
				data.celestialBodyIcon = "planet-desert";
				break;
			}
		}
		
		data.dayLength = (long)1000*gen.nextInt(MIN_DAY_LENGTH, MAX_DAY_LENGTH+1);
		
		if(discoverer != null) {
			data.discovererName = discoverer;
		}
		
		if(parent instanceof StarData) {
			// Like, "Omicron Persei 8"
			data.displayName = parent.displayName+" "+Integer.toString(bodyNumber+1);
		} else {
			// Like, "Omicron Persei 8c"
			data.displayName = parent.displayName+NumeralHelper.letter(bodyNumber+1);
		}
		
		data.unlocalizedName = data.displayName.toLowerCase()+"_planet";
	}
	
	protected PlanetGenType getType(LandeableData data) {
		float absoluteTemperature = (float)data.temperature*288;
		if(data.atmosphere.size() >= 1) {
			if(data.atmosphere.indexOf(IAtmosphericGas.OXYGEN) != -1) {
				if(absoluteTemperature > 273 && absoluteTemperature < 373) {
					return PlanetGenType.PGT_LIFE_O2;
				}
			} 
			if (data.atmosphere.indexOf(IAtmosphericGas.METHANE) != -1) {
				// hm, not sure what fluid they might use...
				return PlanetGenType.PGT_LIFE_CH4;
			}
			if (data.atmosphere.indexOf(IAtmosphericGas.WATER) != -1 && absoluteTemperature < 273) {
				return PlanetGenType.PGT_ICE;
			}
		}
		
		return PlanetGenType.PGT_DESERT;
	}

	
	protected String getSystemName(RandomGenerator generator) throws CannotGenerateException {
		ArrayList<String> list = ProceduralGalaxy.instance.getConfigManager().getStarNames();
		
		
		int index = generator.nextInt(list.size());
		String name = list.get(index);
		for(int i=0;i<NUM_ATTEMPTS;i++) {
			if(!registeredSystems.containsKey(name.toLowerCase(Locale.ENGLISH))) {
				// good
				return name;
			}
		}
		
		throw new CannotGenerateException();
		
	}
	
	protected Vector3 getSystemPosition(RandomGenerator generator) throws CannotGenerateException {
		
		// now I have a grid of (max_coord*2 + 1) x (max_coord*2 + 1)
		// for 10, 21x21, with the original system being in the center
		// this would mean, 440 possible locations
		
		int maxSystems = (int) Math.pow(max_coord*2+1, 2);
		if(registeredSystems.size() >= maxSystems) {
			throw new CannotGenerateException();
		}
		
		
		Vector2 coords = findCoordinates(generator);
		
		return new Vector3(coords.x, coords.y, 0);
	}
	
	protected Vector2 findCoordinates(RandomGenerator generator) throws CannotGenerateException {
		Vector2 res = new Vector2();
		
		
		for(int i=0;i<NUM_ATTEMPTS;i++) {
			
			res.x = generator.nextDouble(max_coord*-1, max_coord);
			res.y = generator.nextDouble(max_coord*-1, max_coord);

			for (Map.Entry<String, SolarSystem> entry : registeredSystems.entrySet())
			{
				SolarSystem curSys = entry.getValue();
				Vector3 sysPos = curSys.getMapPosition();
				sysPos.scale(1/500D);
				// check if this position is within coords
				if(Math.abs(sysPos.x-res.x) <= min_distance && Math.abs(sysPos.y-res.y) <= min_distance) {
					// bad
					break;
				}
				// good
				return res;
			}
		}
		throw new CannotGenerateException();
	}
	
	protected Star generateMainStar() {
		return null;
		
		
	}
}
