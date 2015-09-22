package de.katzenpapst.proceduralgalaxy;

import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.proceduralgalaxy.data.LandeableData;
import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.ChunkProviderSpace;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.IChunkProvider;

public class DynamicPlanetChunkProvider extends ChunkProviderSpace {
	
	LandeableData curData;

    public DynamicPlanetChunkProvider(World par1World, long seed, boolean mapFeaturesEnabled) {
        super(par1World, seed, mapFeaturesEnabled);
        
        curData = ProceduralGalaxy.instance.getSolarSystemManager().getDataByDimId(par1World.provider.dimensionId);
    }

    @Override
    protected BiomeDecoratorSpace getBiomeGenerator() {
        // TODO Auto-generated method stub
        return new BiomeDecoratorTutorial();
    }

     //This should be a custom biome for your mod, but I'm opting to go desert instead out of quickness
    //and the fact that biomes are outside the scope of this tutorial
    @Override
    protected BiomeGenBase[] getBiomesForGeneration() {
        // do biomes even matter now?
        return new BiomeGenBase[]{BiomeGenBase.desert};
    }

    @Override
    public int getCraterProbability() {
    	//mmh?
    	int prob = 10 - (int) (curData.atmosphericPressure*10);
    	if(prob < 0) prob = 0;
        return prob;
    }

    @Override
    protected SpawnListEntry[] getCreatures() {
        SpawnListEntry villager = new SpawnListEntry(EntityAlienVillager.class, 10, 2, 2);
        return new SpawnListEntry[]{villager};
    }

    @Override
    protected BlockMetaPair getDirtBlock() {
        // TODO Auto-generated method stub
        return new BlockMetaPair(GCBlocks.blockMoon, (byte) 3);
    }

    @Override
    protected BlockMetaPair getGrassBlock() {
        // TODO Auto-generated method stub
        return new BlockMetaPair(GCBlocks.blockMoon, (byte) 5);
    }

    @Override
    protected BlockMetaPair getStoneBlock() {
        // TODO Auto-generated method stub
        return new BlockMetaPair(GCBlocks.blockMoon, (byte) 4);
    }

    @Override
    public double getHeightModifier() {
    	// no idea what it does...
        return 10;
    }

    @Override
    protected SpawnListEntry[] getMonsters() {
        SpawnListEntry skele = new SpawnListEntry(EntityEvolvedSkeleton.class, 100, 4, 4);
        SpawnListEntry creeper = new SpawnListEntry(EntityEvolvedCreeper.class, 100, 4, 4);
        SpawnListEntry zombie = new SpawnListEntry(EntityEvolvedZombie.class, 100, 4, 4);
      
        return new SpawnListEntry[]{skele, creeper, zombie};
    }

    @Override
    public double getMountainHeightModifier() {
    	double factor = 48 * (4-curData.getGravityFactor());
    	if(factor < 0) factor = 0;
        return factor;
    }

    @Override
    protected int getSeaLevel() {
    	// hmmm
        return 56;
    }

    @Override
    public double getSmallFeatureHeightModifier() {
    	// what does it even do?
        return 0;
    }

    @Override
    public double getValleyHeightModifier() {
    	// or this?
        return 50;
    }

    @Override
    protected List<MapGenBaseMeta> getWorldGenerators() {
        // TODO fill in with caves and villages
    	/*
    	 * List<MapGenBaseMeta> generators = Lists.newArrayList();
generators.add(this.caveGenerator);
generators.add(this.cavernGenerator);
return generators;
    	 * */
        return new ArrayList<MapGenBaseMeta>();
    }

    @Override
    public void onChunkProvide(int arg0, int arg1, Block[] arg2, byte[] arg3) {
    	//  this.dungeonGenerator.generateUsingArrays(this.worldObj, this.worldObj.getSeed(), cX * 16, 30, cZ * 16, cX, cZ, blocks, metadata);
    }

    @Override
    public void onPopulate(IChunkProvider arg0, int arg1, int arg2){
    	// this.dungeonGenerator.handleTileEntities(this.rand);
    }
  /*
    @Override
    public boolean chunkExists(int x, int y){
        return false;
    }*/

}