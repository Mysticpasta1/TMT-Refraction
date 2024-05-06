package com.teamwizardry.refraction;

import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.Utils;
import com.teamwizardry.refraction.api.soundmanager.SoundManager;
import com.teamwizardry.refraction.common.core.CatChaseHandler;
import com.teamwizardry.refraction.common.core.DispenserScrewDriverBehavior;
import com.teamwizardry.refraction.common.core.EventHandler;
import com.teamwizardry.refraction.common.mt.MTRefractionPlugin;
import com.teamwizardry.refraction.common.network.*;
import com.teamwizardry.refraction.common.proxy.RefractionInternalHandler;
import com.teamwizardry.refraction.init.*;
import com.teamwizardry.refraction.init.recipies.CraftingRecipes;
import com.teamwizardry.refraction.init.recipies.ModAssemblyRecipes;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber
public class Refraction {
	public static Refraction instance;

	public Refraction() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		Utils.HANDLER = new RefractionInternalHandler();

		CatChaseHandler.INSTANCE.getClass(); // load the class
		ModSounds.init();
		ModTab.init();
		ModBlocks.init();
		ModItems.init();
		ModEntities.init();
		ModEffects.init();

		EventHandler.INSTANCE.getClass();
		SoundManager.INSTANCE.getClass();

		//NetworkRegistry.INSTANCE.registerGuiHandler(Refraction.instance, new GuiHandler()); //unused anyway
		PacketHandler.register(PacketLaserFX.class, Side.CLIENT);
		PacketHandler.register(PacketAXYZMarks.class, Side.CLIENT);
		PacketHandler.register(PacketAssemblyProgressParticles.class, Side.CLIENT);
		PacketHandler.register(PacketAssemblyDoneParticles.class, Side.CLIENT);
		PacketHandler.register(PacketBeamParticle.class, Side.CLIENT);
		PacketHandler.register(PacketAmmoColorChange.class, Side.SERVER);
		PacketHandler.register(PacketLaserDisplayTick.class, Side.CLIENT);
		PacketHandler.register(PacketWormholeParticles.class, Side.CLIENT);
		//PacketHandler.register(PacketBuilderGridSaver.class, Side.SERVER); // Unused

		if (ModList.get().isLoaded("crafttweaker"))
			MTRefractionPlugin.init();
		CraftingRecipes.init();
		ModAssemblyRecipes.init();
		DispenserBlock.registerDispenseBehavior((IItemProvider) () -> ModItems.SCREW_DRIVER, new DispenserScrewDriverBehavior());
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LASER, 40, ModSounds.electrical_hums, 0.015f, 1f, false);
		SoundManager.INSTANCE.addSpeaker(ModBlocks.LIGHT_BRIDGE, 67, ModSounds.light_bridges, 0.05f, 1f, false);
	}

	public static void serverStarting(FMLServerStartingEvent event) {
		String clname = Utils.HANDLER.getClass().getName();
		String expect = RefractionInternalHandler.class.getName();
		if (!clname.equals(expect)) {
			new IllegalAccessError("The Refraction API internal method handler has been overriden. "
					+ "This will cause the intended behavior of Refraction to be different than expected. "
					+ "It's marked \"Do not Override\", anyway. Whoever the hell overrode it needs to go "
					+ " back to primary school and learn to read. (Expected classname: " + expect + ", Actual classname: " + clname + ")").printStackTrace();
		}
	}
}
