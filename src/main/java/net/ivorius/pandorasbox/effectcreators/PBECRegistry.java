/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package net.ivorius.pandorasbox.effectcreators;

import net.ivorius.pandorasbox.effects.PBEffect;
import net.ivorius.pandorasbox.effects.PBEffectMulti;
import net.ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRegistry
{
    private static ArrayList<String> goodCreators = new ArrayList<String>();
    private static ArrayList<String> badCreators = new ArrayList<String>();

    private static Hashtable<String, PBEffectCreator> effectCreators = new Hashtable<String, PBEffectCreator>();

    public static void register(PBEffectCreator creator, String id, boolean good)
    {
        (good ? goodCreators : badCreators).add(id);
        effectCreators.put(id, creator);
    }

    public static String getID(PBEffectCreator creator)
    {
        for (String id : effectCreators.keySet())
        {
            if (effectCreators.get(id).equals(creator))
            {
                return id;
            }
        }

        return null;
    }

    public static PBEffectCreator effectCreatorWithName(String name)
    {
        return effectCreators.get(name);
    }

    public static boolean hasEffect(String name)
    {
        return effectCreators.containsKey(name);
    }

    public static boolean isEffectGood(String name)
    {
        return goodCreators.contains(name);
    }

    public static PBEffectCreator randomEffectCreatorOfType(Random random, boolean good)
    {
        ArrayList<String> list = good ? goodCreators : badCreators;
        return effectCreators.get(list.get(random.nextInt(list.size())));
    }

    public static PBEffect createEffect(World world, Random random, double x, double y, double z, String name)
    {
        PBEffectCreator creator = effectCreatorWithName(name);

        if (creator != null)
        {
            return constructEffectSafe(creator, world, x, y, z, random);
        }

        return null;
    }

    public static PBEffect createRandomEffectOfType(World world, Random random, double x, double y, double z, boolean good)
    {
        PBEffectCreator creator = randomEffectCreatorOfType(random, good);

        if (creator != null)
        {
            return constructEffectSafe(creator, world, x, y, z, random);
        }

        return null;
    }

    public static PBEffect createRandomEffect(World world, Random random, double x, double y, double z, boolean multi)
    {
        float currentMinChance = 1.0f;
        ArrayList<PBEffect> effects = new ArrayList<PBEffect>();

        do
        {
            PBEffectCreator creator = randomEffectCreatorOfType(random, random.nextFloat() < 0.49f);
            PBEffect effect = constructEffectSafe(creator, world, x, y, z, random);

            if (effect != null)
            {
                effects.add(effect);
            }

            currentMinChance = Math.min(currentMinChance, creator.chanceForMoreEffects(world, x, y, z, random));
        }
        while (random.nextFloat() < currentMinChance && effects.size() < 3 && multi);

        if (effects.size() == 1)
        {
            return effects.get(0);
        }
        else
        {
            PBEffect[] effectArray = effects.toArray(new PBEffect[effects.size()]);
            int[] delays = new int[effectArray.length];

            for (int i = 0; i < delays.length; i++)
            {
                delays[i] = i == 0 ? 0 : random.nextInt(60);
            }

            PBEffectMulti multiEffect = new PBEffectMulti(effectArray, delays);
            return multiEffect;
        }
    }

    public static PBEffect constructEffectSafe(PBEffectCreator creator, World world, double x, double y, double z, Random random)
    {
        try
        {
            return creator.constructEffect(world, x, y, z, random);
        }
        catch (Exception ex)
        {
            System.out.println("PBEC failed! '" + getID(creator) + "'");
            ex.printStackTrace();
        }

        return null;
    }

    public static EntityPandorasBox spawnPandorasBox(World world, Random random, boolean multi, Entity entity)
    {
        PBEffect effect = createRandomEffect(world, random, entity.posX, entity.posY + 1.2, entity.posZ, multi);
        return spawnPandorasBox(world, effect, entity);
    }

    public static EntityPandorasBox spawnPandorasBox(World world, Random random, String id, Entity entity)
    {
        PBEffect effect = createEffect(world, random, entity.posX, entity.posY + 1.2, entity.posZ, id);
        return spawnPandorasBox(world, effect, entity);
    }

    public static EntityPandorasBox spawnPandorasBox(World world, PBEffect effect, Entity entity)
    {
        if (effect != null)
        {
            EntityPandorasBox entityPandorasBox = new EntityPandorasBox(world, effect);

            Vec3 look = entity.getLookVec();

            entityPandorasBox.setPosition(entity.posX + look.xCoord * 0.5, entity.posY + 1.2 + look.yCoord * 0.5, entity.posZ + look.zCoord * 0.5);
            entityPandorasBox.rotationYaw = entity.rotationYaw + 180.0f;

            entityPandorasBox.beginFloatingAway();

            world.spawnEntityInWorld(entityPandorasBox);

            return entityPandorasBox;
        }

        return null;
    }

    public static Set<String> getAllIDs()
    {
        return effectCreators.keySet();
    }

    public static String[] getIDArray()
    {
        Set<String> allIDs = getAllIDs();
        return allIDs.toArray(new String[allIDs.size()]);
    }
}