package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.entity.ai.ModGroundNavigator;
import com.barribob.MaelstromMod.entity.animation.Animation;
import com.barribob.MaelstromMod.entity.animation.AnimationNone;
import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.IAnimatedMob;
import com.barribob.MaelstromMod.util.IElement;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.LevelHandler;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * A base class for mob to scale nicely with the leveling system. Also
 * streamlines some of the attribute setting, namely attack and max health
 *
 */
public abstract class EntityLeveledMob extends EntityCreature implements IAnimatedMob, IElement
{
    private float level;
    private float regenStartTimer;
    private static float regenStartTime = 60;
    private Element element = Element.NONE;

    @SideOnly(Side.CLIENT)
    protected Animation currentAnimation;

    protected static final DataParameter<Boolean> IMMOVABLE = EntityDataManager.<Boolean>createKey(EntityLeveledMob.class, DataSerializers.BOOLEAN);
    private Vec3d initialPosition = null;
    private boolean animationsInit = false;
    protected double healthScaledAttackFactor = 0.0; // Factor that determines how much attack is affected by health

    public EntityLeveledMob(World worldIn)
    {
	super(worldIn);
	this.setLevel(1);
	this.experienceValue = 5;
    }

    public EntityLeveledMob(World worldIn, Element element)
    {
	this(worldIn);
	this.element = element;
    }

    // Because for some reason the default entity ai for 1.12 sends entities
    // off cliffs and holes instead of going around them
    @Override
    protected PathNavigate createNavigator(World worldIn)
    {
	return new ModGroundNavigator(this, worldIn);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
	if (id == animationByte && currentAnimation == null)
	{
	    initAnimation();
	}
	else
	{
	    super.handleStatusUpdate(id);
	}
    }

    @SideOnly(Side.CLIENT)
    protected void initAnimation()
    {
    }

    @Override
    public void onLivingUpdate()
    {
	super.onLivingUpdate();

	if (world.isRemote && currentAnimation != null)
	{
	    currentAnimation.update();
	}

	if (!world.isRemote && this.getAttackTarget() == null)
	{
	    if (this.regenStartTimer > this.regenStartTime)
	    {
		if (this.ticksExisted % 20 == 0)
		{
		    this.heal(this.getMaxHealth() * 0.015f);
		}
	    }
	    else
	    {
		this.regenStartTimer++;
	    }
	}

	/**
	 * Periodically check if the animations need to be reinitialized
	 */
	if (this.ticksExisted % 20 == 0)
	{
	    world.setEntityState(this, animationByte);
	}

	if (this.isImmovable() && this.initialPosition != null)
	{
	    this.setPosition(initialPosition.x, initialPosition.y, initialPosition.z);
	}
    }

    protected boolean isImmovable()
    {
	return this.dataManager == null ? false : this.dataManager.get(IMMOVABLE);
    }

    protected void setImmovable(boolean immovable)
    {
	this.dataManager.set(IMMOVABLE, immovable);
    }

    // Hold the entity in the same position
    @Override
    public void setPosition(double x, double y, double z)
    {
	super.setPosition(x, y, z);
	if (this.isImmovable())
	{
	    if (this.initialPosition == null)
	    {
		this.initialPosition = ModUtils.entityPos(this);
	    }
	    else
	    {
		super.setPosition(initialPosition.x, initialPosition.y, initialPosition.z);
	    }
	}
    }

    @Override
    public Animation getCurrentAnimation()
    {
	return this.currentAnimation == null ? new AnimationNone() : this.currentAnimation;
    }

    public float getLevel()
    {
	return this.level;
    }

    @Override
    protected void applyEntityAttributes()
    {
	super.applyEntityAttributes();
	this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
	this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0);
    }

    /**
     * Sets the level, updates attributes, and set health to the updated max health
     */
    public void setLevel(float level)
    {
	this.level = level;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
	compound.setFloat("level", level);
	compound.setBoolean("isImmovable", this.isImmovable());
	if (initialPosition != null)
	{
	    compound.setDouble("initialX", initialPosition.x);
	    compound.setDouble("initialY", initialPosition.y);
	    compound.setDouble("initialZ", initialPosition.z);
	}
	super.writeEntityToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
	if (compound.hasKey("level"))
	{
	    this.setLevel(compound.getFloat("level"));
	}
	if (compound.hasKey("isImmovable"))
	{
	    this.dataManager.set(IMMOVABLE, compound.getBoolean("isImmovable"));
	}
	if (compound.hasKey("initialX"))
	{
	    this.initialPosition = new Vec3d(compound.getDouble("initialX"), compound.getDouble("initialY"), compound.getDouble("initialZ"));
	}
	world.setEntityState(this, animationByte);

	super.readFromNBT(compound);
    }

    /**
     * Return the shared monster attribute attack
     */
    public float getAttack()
    {
	return ModUtils.getMobDamage(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue(), healthScaledAttackFactor, this.getMaxHealth(),
		this.getHealth(), this.getLevel());
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
	if (!source.isUnblockable())
	{
	    amount = amount * LevelHandler.getArmorFromLevel(level - 1);
	}

	if (source instanceof IElement)
	{
	    amount = ModUtils.calculateElementalDamage(this.element, ((IElement) source).getElement(), amount);
	}

	return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void entityInit()
    {
	super.entityInit();
	this.dataManager.register(IMMOVABLE, Boolean.valueOf(false));
    }

    @Override
    public Element getElement()
    {
	return element;
    }
}
