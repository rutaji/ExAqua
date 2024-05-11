package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Registry for fluids  from this mod.
 */
public class ModFluids {

    //region register
    public static final DeferredRegister<Fluid> FLUIDS
            = DeferredRegister.create(ForgeRegistries.FLUIDS, ExAqua.MOD_ID);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
    //endregion
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation("block/water_overlay");

    public static final RegistryObject<FlowingFluid> MUD_FLUID
            = FLUIDS.register("mud_fluid", () -> new ForgeFlowingFluid.Source(ModFluids.MUD_PROPERTIES));

    public static final RegistryObject<FlowingFluid> MUD_FLOWING
            = FLUIDS.register("mud_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.MUD_PROPERTIES));


    public static final ForgeFlowingFluid.Properties MUD_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> MUD_FLUID.get(), () -> MUD_FLOWING.get(), FluidAttributes.builder(WATER_STILL_RL, WATER_FLOWING_RL)
            .density(1200).luminosity(0).viscosity(800000000).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY).overlay(WATER_OVERLAY_RL)
            .color(0xff45261d)).slopeFindDistance(1).levelDecreasePerBlock(4)
            .block(() -> ModFluids.MUD_BLOCK.get()).bucket(() -> ModItems.MUD_BUCKET.get());

    public static final RegistryObject<FlowingFluidBlock> MUD_BLOCK = ModBlocks.BLOCKS.register("mud_liquid",
            () -> new FlowingFluidBlock(() -> ModFluids.MUD_FLUID.get(), AbstractBlock.Properties.create(Material.WATER)
                    .doesNotBlockMovement().hardnessAndResistance(100f).noDrops()));

}
