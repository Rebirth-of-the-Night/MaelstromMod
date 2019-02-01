package com.barribob.MaelstromMod.world.gen.mineshaft;

import java.util.List;
import java.util.Random;

import com.barribob.MaelstromMod.entity.entities.EntityAzureVillager;
import com.barribob.MaelstromMod.util.handlers.LootTableHandler;
import com.barribob.MaelstromMod.world.gen.ModStructureTemplate;

import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

/**
 * 
 * The specific template used for generating the maelstrom fortress
 *
 */
public class AzureMineshaftTemplate extends ModStructureTemplate
{
    private int distance;
    private static int numTemplates = 0;
    private int templateId;

    public AzureMineshaftTemplate()
    {
    }

    public AzureMineshaftTemplate(TemplateManager manager, String type, BlockPos pos, Rotation rotation, int distance, boolean overwriteIn)
    {
	super(manager, type, pos, rotation, overwriteIn);
	templateId = numTemplates++;
	this.distance = distance;
    }

    public int getDistance()
    {
	return this.distance;
    }

    /**
     * Loads structure block data markers and handles them by their name
     */
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb)
    {
	if (function.startsWith("chest"))
	{
	    worldIn.setBlockToAir(pos);
	    BlockPos blockpos = pos.down();

	    if (sbb.isVecInside(blockpos))
	    {
		TileEntity tileentity = worldIn.getTileEntity(blockpos);

		if (tileentity instanceof TileEntityChest)
		{
		    ((TileEntityChest) tileentity).setLootTable(LootTableHandler.AZURE_FORTRESS, rand.nextLong());
		}
	    }
	}
	else if (function.startsWith("villager_below"))
	{
	    worldIn.setBlockToAir(pos);
	    EntityAzureVillager entity = new EntityAzureVillager(worldIn);
	    entity.setPosition((double) pos.getX() + 0.5D, (double) pos.getY() - 0.5D, (double) pos.getZ() + 0.5D);
	    worldIn.spawnEntity(entity);
	}
	else if (function.startsWith("minecart"))
	{
	    worldIn.setBlockToAir(pos);
	    EntityMinecartEmpty minecart = new EntityMinecartEmpty(worldIn);
	    minecart.setPosition((double) pos.getX() + 0.5D, (double) pos.getY() - 0.5D, (double) pos.getZ() + 0.5D);
	    worldIn.spawnEntity(minecart);
	}
    }

    public int getId()
    {
	return this.templateId;
    }

    /**
     * Determines if the new template is overlapping with another template,
     * excluding the parent
     */
    public boolean isCollidingExcParent(TemplateManager manager, AzureMineshaftTemplate parent, List<StructureComponent> structures)
    {
	List<StructureComponent> collisions = findAllIntersecting(structures);

	boolean foundCollision = false;

	for (StructureComponent collision : collisions)
	{
	    if (((AzureMineshaftTemplate) collision).getId() != parent.getId())
	    {
		foundCollision = true;
	    }
	}

	return foundCollision;
    }

    public static void resetTemplateCount()
    {
	numTemplates = 0;
    }

    @Override
    public String templateLocation()
    {
	return "mineshaft";
    }
}
