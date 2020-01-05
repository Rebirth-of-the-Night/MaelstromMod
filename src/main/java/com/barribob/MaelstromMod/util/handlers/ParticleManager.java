package com.barribob.MaelstromMod.util.handlers;

import java.util.Random;
import java.util.function.Consumer;

import com.barribob.MaelstromMod.particle.EffectParticle;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModRandom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleExplosionLarge;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.client.particle.ParticleSuspendedTown;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * 
 * A place to handle all of the regularly spawned particles rather than copy and
 * pasting multiple times
 *
 */
public class ParticleManager
{
    /**
     * Calls a particle spawner with the vector offset
     * 
     * @param radius
     *            The radius of the circle
     * @param points
     *            The number of points around the circle
     * @param particleSpawner
     */
    public static void spawnParticlesInCircle(float radius, int points, Consumer<Vec3d> particleSpawner)
    {
	float degrees = 360f / points;
	for (int i = 0; i < points; i++)
	{
	    double radians = Math.toRadians(i * degrees);
	    Vec3d offset = new Vec3d(Math.sin(radians), Math.cos(radians), 0).scale(radius);
	    particleSpawner.accept(offset);
	}
    }

    /**
     * Spawns the little square purple particles
     * 
     * @param worldIn
     * @param rand
     * @param pos
     */
    public static void spawnMaelstromParticle(World worldIn, Random rand, Vec3d pos)
    {
	Particle particle = new ParticleSuspendedTown.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
	setMaelstromColor(particle);
	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    /**
     * Spawns purple spiral particles
     * 
     * @param worldIn
     * @param rand
     * @param pos
     */
    public static void spawnMaelstromPotionParticle(World worldIn, Random rand, Vec3d pos, boolean isLight)
    {
	Particle particle = new ParticleSpell.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, 0.0D, 0.1D, 0.0D);

	if (isLight)
	{
	    setMaelstromLightColor(particle);
	}
	else
	{
	    setMaelstromColor(particle);
	}

	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    /**
     * Spawns purple explosion particles
     * 
     * @param worldIn
     * @param rand
     * @param pos
     */
    public static void spawnMaelstromExplosion(World worldIn, Random rand, Vec3d pos)
    {
	Particle particle = new ParticleExplosionLarge.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, ModRandom.getFloat(0.05f), 0.0f, ModRandom.getFloat(0.05f));
	setMaelstromColor(particle);
	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static void spawnColoredExplosion(World worldIn, Vec3d pos, Vec3d baseColor)
    {
	Particle particle = new ParticleExplosionLarge.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, ModRandom.getFloat(0.05f), 0.0f, ModRandom.getFloat(0.05f));
	baseColor = ModColors.variateColor(baseColor, 0.2f);
	particle.setRBGColorF((float) baseColor.x, (float) baseColor.y, (float) baseColor.z);
	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    /**
     * Spawns large smoke particles
     * 
     * @param worldIn
     * @param rand
     * @param pos
     */
    public static void spawnMaelstromLargeSmoke(World worldIn, Random rand, Vec3d pos)
    {
	Particle particle = new ParticleExplosion.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, ModRandom.getFloat(0.05f), 0.0f, ModRandom.getFloat(0.05f));
	setMaelstromColor(particle);
	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    /**
     * Spawns purple smoke
     * 
     * @param worldIn
     * @param rand
     * @param pos
     */
    public static void spawnMaelstromSmoke(World worldIn, Random rand, Vec3d pos, boolean isLight)
    {
	Particle particle = new ParticleSmokeNormal.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, 0.0D, 0.01D, 0.0D);

	if (isLight)
	{
	    setMaelstromLightColor(particle);
	}
	else
	{
	    setMaelstromColor(particle);
	}

	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static void spawnColoredSmoke(World worldIn, Random rand, Vec3d pos, Vec3d baseColor)
    {
	Particle particle = new ParticleSmokeNormal.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, 0.0D, 0.01D, 0.0D);

	baseColor = ModColors.variateColor(baseColor, 0.2f);
	particle.setRBGColorF((float) baseColor.x, (float) baseColor.y, (float) baseColor.z);

	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    /**
     * Spawns blackish-purplish flames
     */
    public static void spawnDarkFlames(World worldIn, Random rand, Vec3d pos)
    {
	spawnDarkFlames(worldIn, rand, pos, Vec3d.ZERO);
    }

    public static void spawnDarkFlames(World worldIn, Random rand, Vec3d pos, Vec3d vel)
    {
	Particle particle = new ParticleFlame.Factory().createParticle(0, worldIn, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);

	float f = ModRandom.getFloat(0.1f);
	particle.setRBGColorF(0.1f + f, 0, 0.1f + f);

	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static void spawnEffect(World world, Vec3d pos, Vec3d baseColor)
    {
	Particle particle = new EffectParticle.Factory().createParticle(0, world, pos.x, pos.y, pos.z, 0, 0, 0);
	baseColor = ModColors.variateColor(baseColor, 0.2f);
	particle.setRBGColorF((float) baseColor.x, (float) baseColor.y, (float) baseColor.z);
	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static void spawnFirework(World world, Vec3d pos, Vec3d color)
    {
	spawnFirework(world, pos, color, Vec3d.ZERO);
    }

    public static void spawnFirework(World world, Vec3d pos, Vec3d color, Vec3d vel)
    {
	Particle particle = new ParticleFirework.Factory().createParticle(0, world, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
	particle.setRBGColorF((float) color.x, (float) color.y, (float) color.z);
	Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static void spawnParticleSphere(World world, Vec3d pos, float radius)
    {
	for (int i = 0; i < 1000; i++)
	{
	    Vec3d unit = new Vec3d(0, 1, 0);
	    unit = unit.rotatePitch((float) (Math.PI * ModRandom.getFloat(1)));
	    unit = unit.rotateYaw((float) (Math.PI * ModRandom.getFloat(1)));
	    unit = unit.normalize().scale(radius);
	    ParticleManager.spawnMaelstromParticle(world, world.rand, pos.add(unit));
	}
	ParticleManager.spawnEffect(world, pos, ModColors.RED);
    }

    /**
     * Helper function to vary and unify the colors
     * 
     * @param particle
     */
    private static void setMaelstromColor(Particle particle)
    {
	float f = ModRandom.getFloat(0.2f);
	particle.setRBGColorF(0.3f + f, 0.2f + f, 0.4f + f);
    }

    /**
     * Helper function to vary and unify the colors
     * 
     * @param particle
     */
    private static void setMaelstromLightColor(Particle particle)
    {
	float f = ModRandom.getFloat(0.2f);
	particle.setRBGColorF(0.8f + f, 0.5f + f, 0.8f + f);
    }
}
