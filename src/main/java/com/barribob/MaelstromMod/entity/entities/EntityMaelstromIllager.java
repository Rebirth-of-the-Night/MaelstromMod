package com.barribob.MaelstromMod.entity.entities;

import java.util.function.Supplier;

import com.barribob.MaelstromMod.entity.action.ActionSpawnEnemy;
import com.barribob.MaelstromMod.entity.ai.EntityAIRangedAttack;
import com.barribob.MaelstromMod.entity.ai.EntityAIRangedAttackNoReset;
import com.barribob.MaelstromMod.init.ModEntities;
import com.barribob.MaelstromMod.util.handlers.LootTableHandler;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

/**
 * 
 * The illager summoner boss
 *
 */
public class EntityMaelstromIllager extends EntityMaelstromMob
{
    private EntityAIRangedAttack rangedAttackAI;
    private int[] easy_minion_spawning = { 2 };
    private int[] hard_minion_spawning = { 1, 3, 1, 3 };
    private int counter;

    // For rendering purposes
    private boolean blockedBlow;

    // Responsible for the boss bar
    private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.NOTCHED_20));

    public EntityMaelstromIllager(World worldIn)
    {
	super(worldIn);
	this.setSize(0.7f, 2.2f);
	this.experienceValue = ModEntities.BOSS_EXPERIENCE;
	this.setLevel(1.5f);
    }

    @Override
    protected void updateAttributes()
    {
	this.setBaseMaxHealth(100);
    }

    protected void initEntityAI()
    {
	super.initEntityAI();
	rangedAttackAI = new EntityAIRangedAttackNoReset<EntityMaelstromMob>(this, 1.25f, 360, 60, 15.0f, 0.5f);
	this.tasks.addTask(4, rangedAttackAI);
    }

    public boolean blockedBlow()
    {
	return this.blockedBlow;
    }

    protected SoundEvent getAmbientSound()
    {
	return SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
	return SoundEvents.EVOCATION_ILLAGER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
	return SoundEvents.ENTITY_EVOCATION_ILLAGER_HURT;
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    protected boolean canDespawn()
    {
	return false;
    }

    @Override
    protected ResourceLocation getLootTable()
    {
	return LootTableHandler.MAELSTROM_ILLAGER;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
	if (!this.isSwingingArms())
	{
	    amount = 0;
	}

	this.blockedBlow = !this.isSwingingArms();

	float prevHealth = this.getHealth();
	boolean flag = super.attackEntityFrom(source, amount);

	String message = "";
	if (prevHealth > this.getMaxHealth() * 0.95 && this.getHealth() <= this.getMaxHealth() * 0.95)
	{
	    message = "What a fine being we have here! I must investigate!";
	}

	if (prevHealth > this.getMaxHealth() * 0.75 && this.getHealth() <= this.getMaxHealth() * 0.75)
	{
	    message = "Come join our empire as a maelstrom zombie!";
	}

	if (prevHealth > this.getMaxHealth() * 0.5 && this.getHealth() <= this.getMaxHealth() * 0.5)
	{
	    message = "Ouchie! My creations, assist me!";
	}

	if (message != "")
	{
	    for (EntityPlayer player : this.bossInfo.getPlayers())
	    {
		player.sendMessage(new TextComponentString(TextFormatting.DARK_PURPLE + "Maelstrom Illager: " + TextFormatting.WHITE + message));
	    }
	}

	return flag;
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    {
	int spawnAmount;
	if (this.getHealth() < this.getMaxHealth() * 0.5f)
	{
	    spawnAmount = hard_minion_spawning[counter % this.hard_minion_spawning.length];
	}
	else
	{
	    spawnAmount = easy_minion_spawning[counter % this.easy_minion_spawning.length];
	}

	counter++;

	for (int i = 0; i < spawnAmount; i++)
	{
	    Supplier<EntityLeveledMob> mobSupplier = () -> {
		int r = rand.nextInt(3);
		if (r == 0)
		{
		    return new EntityShade(this.world);
		}
		else if (r == 1)
		{
		    return new EntityMaelstromMage(this.world);
		}
		else
		{
		    return new EntityHorror(this.world);
		}
	    };
	    new ActionSpawnEnemy(mobSupplier).performAction(this, target);
	    ;
	}
    }

    /**
     * Spawn summoming particles
     */
    public void onUpdate()
    {
	super.onUpdate();

	if (this.world.isRemote && this.isSwingingArms())
	{
	    float f = this.renderYawOffset * 0.017453292F + MathHelper.cos((float) this.ticksExisted * 0.6662F) * 0.25F;
	    float f1 = MathHelper.cos(f);
	    float f2 = MathHelper.sin(f);
	    ParticleManager.spawnMaelstromPotionParticle(world, rand, new Vec3d(this.posX + (double) f1 * 0.6D, this.posY + 1.8D, this.posZ + (double) f2 * 0.6D), true);
	    ParticleManager.spawnMaelstromPotionParticle(world, rand, new Vec3d(this.posX - (double) f1 * 0.6D, this.posY + 1.8D, this.posZ - (double) f2 * 0.6D), true);
	}
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
	if (this.hasCustomName())
	{
	    this.bossInfo.setName(this.getDisplayName());
	}

	super.readEntityFromNBT(compound);
    }

    /**
     * Sets the custom name tag for this entity
     */
    public void setCustomNameTag(String name)
    {
	super.setCustomNameTag(name);
	this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void updateAITasks()
    {
	this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	super.updateAITasks();
    }

    /**
     * Add the given player to the list of players tracking this entity. For
     * instance, a player may track a boss in order to view its associated boss bar.
     */
    public void addTrackingPlayer(EntityPlayerMP player)
    {
	super.addTrackingPlayer(player);
	this.bossInfo.addPlayer(player);
    }

    /**
     * Removes the given player from the list of players tracking this entity. See
     * {@link Entity#addTrackingPlayer} for more information on tracking.
     */
    public void removeTrackingPlayer(EntityPlayerMP player)
    {
	super.removeTrackingPlayer(player);
	this.bossInfo.removePlayer(player);
    }
}