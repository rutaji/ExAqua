package com.rutaji.exaqua;

import com.rutaji.exaqua.Fluids.ModFluids;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.container.ModContainers;
import com.rutaji.exaqua.container.SieveContainer;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.item.ModItems;
import com.rutaji.exaqua.networking.PacketHandler;
import com.rutaji.exaqua.screen.AutoSqueezerScreen;
import com.rutaji.exaqua.screen.CauldronScreen;
import com.rutaji.exaqua.screen.SieveScreen;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import com.rutaji.exaqua.screen.SqueezerScreen;
import com.rutaji.exaqua.util.ColorsToFloat;
import com.rutaji.exaqua.util.ModItemModelProperties;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExAqua.MOD_ID)
public class ExAqua
{
    public static final String MOD_ID = "exaqua";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public ExAqua() {



        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModTileEntities.register(eventBus);
        ModContainers.register(eventBus);
        ModRecipeTypes.register(eventBus);
        ModFluids.register(eventBus);

        eventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        eventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        eventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.init();
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ModItemModelProperties.makeHandSieve(ModItems.HANDSIEVE.get());

        ScreenManager.registerFactory(ModContainers.SQUEEZERCONTAINER.get(), SqueezerScreen::new);
        ScreenManager.registerFactory(ModContainers.SIEVECONTAINER.get(), SieveScreen::new);
        ScreenManager.registerFactory(ModContainers.CAULDRON_CONTAINER.get(), CauldronScreen::new);
        ScreenManager.registerFactory(ModContainers.AUTO_SQUEEZER_CONTAINER.get(), AutoSqueezerScreen::new);

        //RenderTypeLookup.setRenderLayer(ModBlocks.DIAMONDSIEVE.get(),RenderType.getCutout());

        RenderTypeLookup.setRenderLayer(ModFluids.MUD_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.MUD_FLUID.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.MUD_FLOWING.get(), RenderType.getTranslucent());

    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
