/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnBlocks extends PBEffectSpawnEntities
{
    public Block[] blocks;

    public PBEffectSpawnBlocks()
    {
    }

    public PBEffectSpawnBlocks(int time, Block[] blocks)
    {
        super(time, blocks.length);

        setDoesSpawnFromBox(0.1, 0.4, 0.2, 1.0);
        this.blocks = blocks;
    }

    public PBEffectSpawnBlocks(int time, double range, double shiftY, Block[] blocks)
    {
        super(time, blocks.length);

        setDoesNotSpawnFromBox(range, shiftY);
        this.blocks = blocks;
    }

    @Override
    public Entity spawnEntity(World world, EntityPandorasBox entity, Random random, int number, double x, double y, double z)
    {
        Block block = blocks[number];
        EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, x, y, z, block.getDefaultState());
        entityFallingBlock.fallTime = 1;
        world.spawnEntity(entityFallingBlock);

        return entityFallingBlock;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
    }
}
