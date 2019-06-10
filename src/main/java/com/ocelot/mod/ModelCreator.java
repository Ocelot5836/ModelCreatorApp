package com.ocelot.mod;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.mrcrayfish.device.api.ApplicationManager;
import com.ocelot.api.utils.NumberHelper;
import com.ocelot.mod.application.ApplicationModelCreator;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * The main mod class.
 * 
 * @author Ocelot5836
 */
@Mod(modid = ModelCreator.MOD_ID, version = ModelCreator.VERSION, updateJSON = "https://raw.githubusercontent.com/Ocelot5836/storage/master/mods/device-mod/modelcreator/update.json", useMetadata = true)
public class ModelCreator
{

	/** The mod id */
	public static final String MOD_ID = "omca";
	/** The current version of the mod */
	public static final String VERSION = "2.1.0";
	/** The id for the model creator app */
	public static final ResourceLocation MODEL_CREATOR_ID = new ResourceLocation(MOD_ID, "mc");
	/** The formatted id for the model creator app */
	public static final String FORMATTED_MODEL_CREATOR_ID = MODEL_CREATOR_ID.getResourceDomain() + "." + MODEL_CREATOR_ID.getResourcePath();

	/** The mod's instance. Probably not too useful but might as well have it */
	@Instance(MOD_ID)
	public static ModelCreator instance;

	private static ExecutorService pool;

	/** The mod's logger */
	private static Logger logger;

	@EventHandler
	public void pre(FMLPreInitializationEvent event)
	{
		NumberHelper.init();
		Usernames.init();
		logger = event.getModLog();
		pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> this.pool.shutdown()));
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ApplicationManager.registerApplication(MODEL_CREATOR_ID, ApplicationModelCreator.class);
	}

	@EventHandler
	public void post(FMLPostInitializationEvent event)
	{
	}

	/**
	 * @return A logger that uses the mod id as the name
	 */
	public static Logger logger()
	{
		return logger;
	}

	/**
	 * @return Whether or not the mod is in a deobfuscated environment
	 */
	public static boolean isDebug()
	{
		return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}
}