package de.katzenpapst.proceduralgalaxy;

import net.minecraft.world.biome.BiomeGenBase;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldChunkManagerSpace;

//import WorldChunkManagerSpace;

public class WorldChunkManagerTutorial extends WorldChunkManagerSpace {
	 @Override
	    public BiomeGenBase getBiome() {
	        return BiomeGenBase.desert;
	    }
}
