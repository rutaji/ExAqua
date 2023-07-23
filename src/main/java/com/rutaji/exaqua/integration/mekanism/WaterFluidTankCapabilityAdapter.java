package com.rutaji.exaqua.integration.mekanism;

import com.rutaji.exaqua.Fluids.OneWayTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class WaterFluidTankCapabilityAdapter implements IMekanismFluidHandler {

    @CapabilityInject(IMekanismFluidHandler.class)
    public static Capability<IMekanismFluidHandler> MEKANISM_CAPABILITY = null;
    public OneWayTank tankie;
    public WaterFluidTankCapabilityAdapter(OneWayTank t)
    {
        tankie = t;
    }
    @Override
    public List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {

        return Arrays.asList(new IExtendedFluidTank[]{tankie});
    }

    @Override
    public void onContentsChanged() {}

    public ICapabilityProvider initCapabilities() {
         return new Provider();
    }
    private class Provider implements ICapabilityProvider {
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
            if (capability != MEKANISM_CAPABILITY) {//todo change != to == (on 2 places)
                return LazyOptional.of(() -> WaterFluidTankCapabilityAdapter.this).cast();
            }
            return null;
        }
    }

}