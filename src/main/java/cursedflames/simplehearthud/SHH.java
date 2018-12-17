package cursedflames.simplehearthud;

import org.apache.logging.log4j.Logger;

import cursedflames.simplehearthud.client.ArmorRenderHandler;
import cursedflames.simplehearthud.client.ExtraHeartRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = SHH.MODID, clientSideOnly = true, useMetadata = true)
@Mod.EventBusSubscriber
public class SHH {
	@Mod.Instance
	public static SHH instance;

	public static final String MODID = "simplehearthud";
	// TODO config gui
	public static Config config;

	public static Logger logger;

//	@SidedProxy(clientSide = "cursedflames.simplehearthud.proxy.ClientProxy", serverSide = "cursedflames.simplehearthud.proxy.ServerProxy")
//	public static ISideProxy proxy;

	public static ExtraHeartRenderHandler heartRender;
	public static ArmorRenderHandler armorRender;

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Config(MODID, "1", logger);
		config.preInit(event);
		ModConfig.initConfig();
		if (ModConfig.extraHearts.getBoolean(true)) {
			heartRender = new ExtraHeartRenderHandler();
			MinecraftForge.EVENT_BUS.register(heartRender);
		}

		if (ModConfig.extraArmor.getBoolean(true)) {
			armorRender = new ArmorRenderHandler();
			MinecraftForge.EVENT_BUS.register(armorRender);
		}
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		config.postInit(event);
	}
}
