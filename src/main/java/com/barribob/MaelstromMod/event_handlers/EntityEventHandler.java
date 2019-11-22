package com.barribob.MaelstromMod.event_handlers;

import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.entity.entities.EntityMaelstromBeast;
import com.barribob.MaelstromMod.entity.util.LeapingEntity;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber()
public class EntityEventHandler
{
    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event)
    {
	if (event.getEntityLiving() instanceof LeapingEntity && ((LeapingEntity) event.getEntityLiving()).isLeaping())
	{
	    ((LeapingEntity) event.getEntityLiving()).onStopLeaping();
	    ((LeapingEntity) event.getEntityLiving()).setLeaping(false);
	}
    }

    @SubscribeEvent
    public static void onEntitySpawnEvent(LivingSpawnEvent event)
    {
	if (event.getEntityLiving() instanceof EntitySheep)
	{
	    if (event.getEntityLiving().dimension == ModConfig.world.fracture_dimension_id)
	    {
		((EntitySheep) event.getEntityLiving()).setFleeceColor(EnumDyeColor.CYAN);
	    }
	    if (event.getEntityLiving().dimension == ModConfig.world.cliff_dimension_id)
	    {
		((EntitySheep) event.getEntityLiving()).setFleeceColor(EnumDyeColor.GRAY);
	    }
	}
    }
}
