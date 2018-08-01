package com.ocelot.mod;

import org.apache.logging.log4j.Logger;

import com.mrcrayfish.device.api.ApplicationManager;
import com.ocelot.mod.application.ApplicationModelCreator;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
 * The main mod class.
 * 
 * @author Ocelot5836
 */
@net.minecraftforge.fml.common.Mod(modid = Mod.MOD_ID, version = Mod.VERSION, acceptedMinecraftVersions = "[1.12,1.12.2]", useMetadata = true)
public class Mod {

	/** The mod id */
	public static final String MOD_ID = "mca";
	/** The current version of the mod */
	public static final String VERSION = "1.0";
	/** The id for the model creator app */
	public static final ResourceLocation MODEL_CREATOR_ID = new ResourceLocation(MOD_ID, "mc");

	/** The mod's instance. Probably not too useful but might as well have it */
	@Instance(MOD_ID)
	public static Mod instance;

	/** The mod's logger */
	private static Logger logger;

	@EventHandler
	public void pre(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		// ModConfig.pre();
		// if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
		// ModConfig.clientPre();
		// }
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		ApplicationManager.registerApplication(MODEL_CREATOR_ID, ApplicationModelCreator.class);
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event) {
	}

	/**
	 * @return A logger that uses the mod id as the name
	 */
	public static Logger logger() {
		return logger;
	}

	/**
	 * @return Whether or not the mod is in a deobfuscated environment
	 */
	public static boolean isDebug() {
		return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}
}