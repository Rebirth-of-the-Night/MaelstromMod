package com.barribob.MaelstromMod;

import com.barribob.MaelstromMod.commands.CommandDimensionTeleport;
import com.barribob.MaelstromMod.init.BiomeInit;
import com.barribob.MaelstromMod.init.ModDimensions;
import com.barribob.MaelstromMod.init.ModEntities;
import com.barribob.MaelstromMod.init.ModProfessions;
import com.barribob.MaelstromMod.init.ModRecipes;
import com.barribob.MaelstromMod.init.ModStructures;
import com.barribob.MaelstromMod.proxy.CommonProxy;
import com.barribob.MaelstromMod.util.Reference;
import com.barribob.MaelstromMod.util.handlers.RenderHandler;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import com.barribob.MaelstromMod.world.gen.WorldGenCustomStructures;
import com.barribob.MaelstromMod.world.gen.WorldGenOre;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * 
 * Main mod class Many of the base boilerplate here is thanks to loremaster's
 * tutorials https://www.youtube.com/channel/UC3n-lKS-MYlunVtErgzSFZg Entities,
 * world generation, and dimension frameworks are thanks to Harry Talks
 * https://www.youtube.com/channel/UCUAawSqNFBEj-bxguJyJL9g Also thanks to
 * Julian Abelar for a bunch of modding tutorials and articles
 * https://jabelarminecraft.blogspot.com/
 *
 */
@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main
{
    @Instance
    public static Main instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    /**
     * 
     * Basically initializes the entire mod by calling all of the init methods in
     * the static classes
     */
    @EventHandler
    public static void PreInit(FMLPreInitializationEvent event)
    {
	GameRegistry.registerWorldGenerator(new WorldGenOre(), 3);
	GameRegistry.registerWorldGenerator(new WorldGenCustomStructures(), 0);

	ModEntities.registerEntities();
	proxy.init();

	BiomeInit.registerBiomes();
	ModDimensions.registerDimensions();
    }

    @EventHandler
    public static void Init(FMLInitializationEvent event)
    {
	ModRecipes.init();
	SoundsHandler.registerSounds();
	ModStructures.registerStructures();
	ModProfessions.associateCareersAndTrades();
	
    }

    @EventHandler
    public static void PostInit(FMLPostInitializationEvent event)
    {

    }

    @EventHandler
    public static void serverLoad(FMLServerStartingEvent event)
    {
	event.registerServerCommand(new CommandDimensionTeleport());
    }
}
