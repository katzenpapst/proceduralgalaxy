package de.katzenpapst.proceduralgalaxy.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.FMLInjectionData;
import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Configuration.UnicodeInputStreamReader;

import java.io.InputStream;

public class ConfigManager {
	
	Configuration config;
	
	public static final String CONFIG_DIR = "ProceduralGalaxy";
	public static final String MAIN_CONFIG_FILE = "main.conf";
	public static final String STARNAME_FILE = "starnames.txt";
	
	protected ArrayList<String> starNames = null;
	protected int defaultPlanetTier = 3;
	
	protected float habitableZoneWidth = 1;
	
	protected int minNumPlanets = 2;
	protected int maxNumPlanets = 10;
	protected int minNumMoons = 0;
	protected int maxNumMoons = 10;
	
	protected File configDirectory;
	
	public ConfigManager(File configDir) {
		starNames = new ArrayList<String>();
		configDirectory = configDir;
		loadMainConfig();
		loadStarnameList();
		
	}
	
	protected void loadMainConfig() {
		// do
		File configFile = new File(configDirectory, CONFIG_DIR+"/"+MAIN_CONFIG_FILE);
		config = new Configuration(configFile);
		config.load();
        

		defaultPlanetTier = config.getInt("defaultPlanetTier", Configuration.CATEGORY_GENERAL, defaultPlanetTier, 1, 100, "Default tier for planets. You might want to change this if you have a mod which adds higher-tier rockets");
	
		
		minNumPlanets = config.getInt("minNumPlanets", Configuration.CATEGORY_GENERAL, minNumPlanets, 0, 10, "Minimum number of planets in a solar system");
		maxNumPlanets = config.getInt("maxNumPlanets", Configuration.CATEGORY_GENERAL, maxNumPlanets, minNumPlanets, 20, "Maximum number of planets in a solar system");
		
		minNumMoons = config.getInt("minNumMoons", Configuration.CATEGORY_GENERAL, minNumMoons, 0, 10, "Minimum number of moons a planet can have. Setting this to > 0 will make moons appear EVERYWHERE");
		maxNumMoons = config.getInt("maxNumMoons", Configuration.CATEGORY_GENERAL, maxNumMoons, minNumMoons, 20, "Maximum number of moons a planet can have");
     
		habitableZoneWidth = config.getFloat("habitableZoneWidth", Configuration.CATEGORY_GENERAL, habitableZoneWidth, 0.05F, 10F, "Width of the habitable Zone. The wider it is, the more planets could have oxygen");
        //String[] names = config.getStringList("starNameList", Configuration.CATEGORY_GENERAL, defaultValues, "this is a test");

        

        config.save();
	}
	
	protected void loadStarnameList() {
		File file = new File(configDirectory, CONFIG_DIR+"/"+STARNAME_FILE);
		
		BufferedReader buffer = null;
        UnicodeInputStreamReader input = null;
        String defaultEncoding = Configuration.DEFAULT_ENCODING;
        
        String basePath = ((File)(FMLInjectionData.data()[6])).getAbsolutePath().replace(File.separatorChar, '/').replace("/.", "");
        String path = file.getAbsolutePath().replace(File.separatorChar, '/').replace("/./", "/").replace(basePath, "");
        try {
        	
	        if (file.getParentFile() != null)
	        {
	            file.getParentFile().mkdirs();
	        }
	
	        if (!file.exists())
	        {
	            
                // fill stuff with default data
	            ResourceLocation res = new ResourceLocation(ProceduralGalaxy.ASSET_PREFIX, "etc/starnames.txt");
	            InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream();
	            readStarnameStream(stream);
	            
	            // doesn't matter much if I can't create it
	            if (!file.createNewFile())
	                return;
	            // now save it
	            if (file.canWrite())
	            {
	            	writeStarnameStream(new FileOutputStream(file));
	            }
	            
	            
	        } 
	        else if (file.canRead())
            {
	        	readStarnameStream(new FileInputStream(file));
            }
        } catch (Exception e) {
        	
        }
		
	}
	
	protected void writeStarnameStream(FileOutputStream fos) throws IOException {
        BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(fos, Configuration.DEFAULT_ENCODING));

        for(String line: starNames) {
        	buffer.write(line + Configuration.NEW_LINE);
        }
        buffer.close();
        fos.close();
        
	}
	
	protected void readStarnameStream(InputStream stream) throws IOException {
		UnicodeInputStreamReader input = new UnicodeInputStreamReader(stream, Configuration.DEFAULT_ENCODING);
        // defaultEncoding = input.getEncoding();
		BufferedReader buffer = new BufferedReader(input);

        String line;

        while (true)
        {
            line = buffer.readLine();

            if (line == null)
            {
                break;
            }
            if(!line.isEmpty())
            	starNames.add(line);
        }
        buffer.close();
        input.close();
        stream.close();
	}
	
	public int getPlanetTier() {
		return defaultPlanetTier;
	}
	
	public ArrayList<String> getStarNames() {
		return starNames;
	}

	public int getMinNumPlanets() {
		return minNumPlanets;
	}

	public int getMaxNumPlanets() {
		return maxNumPlanets;
	}
	
	public int getMinNumMoons() {
		return minNumMoons;
	}

	public int getMaxNumMoons() {
		return maxNumMoons;
	}
	
	public float getHabitableZoneWidth() {
		return habitableZoneWidth;
	}

}
