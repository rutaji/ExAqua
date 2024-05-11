package com.rutaji.exaqua.container;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for all containers from this mod.
 */
public class ModContainers {
    /**
     * register containing all containers
     */
    public static DeferredRegister<ContainerType<?>> CONTAINERS
            = DeferredRegister.create(ForgeRegistries.CONTAINERS, ExAqua.MOD_ID);

    public static final RegistryObject<ContainerType<SqueezerContainer>> SQUEEZERCONTAINER
            = CONTAINERS.register("squeezercontainer",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();
                return new SqueezerContainer(windowId, world, pos, inv, inv.player);
            })));
    public static final RegistryObject<ContainerType<SieveContainer>> SIEVECONTAINER
            = CONTAINERS.register("sievecontainer",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();
                return new SieveContainer(windowId, world, pos, inv, inv.player);
            })));
    public static final RegistryObject<ContainerType<CauldronContainer>> CAULDRON_CONTAINER
            = CONTAINERS.register("cauldroncontainer",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();
                return new CauldronContainer(windowId, world, pos, inv, inv.player);
            })));
    public static final RegistryObject<ContainerType<AutoSqueezerContainer>> AUTO_SQUEEZER_CONTAINER
            = CONTAINERS.register("auto_squeezer_container",
            () -> IForgeContainerType.create(((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();
                return new AutoSqueezerContainer(windowId, world, pos, inv, inv.player);
            })));

    /**
     * Registers all modded containers. Called from {@link ExAqua ExAqua} at a start of the game.
     */
    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
