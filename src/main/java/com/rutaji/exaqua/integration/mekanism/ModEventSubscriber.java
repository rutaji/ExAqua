package com.rutaji.exaqua.integration.mekanism;

import com.mojang.datafixers.TypeRewriteRule;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.Fluids.OneWayTank;
import com.rutaji.exaqua.tileentity.SqueezerTile;
import mekanism.api.fluid.IMekanismFluidHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ExAqua.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {

        //CapabilityManager.INSTANCE.register(IFluidHandler.class,new OneWayTank(),OneWayTank::new);


    }
}