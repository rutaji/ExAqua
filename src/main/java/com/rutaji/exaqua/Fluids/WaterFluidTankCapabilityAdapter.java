package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * This classed is used as middle men betweenn {@link MyLiquidTank MyLiquidTank} and other mods.
 * Every method calls it's equivalent in {@link MyLiquidTank MyLiquidTank}.
 */
public class WaterFluidTankCapabilityAdapter implements IFluidTank, IFluidHandler{

    public MyLiquidTank tankie;
    public WaterFluidTankCapabilityAdapter(MyLiquidTank t)
    {
        tankie = t;
    }
    public ICapabilityProvider initCapabilities() {
         return new Provider();
    }

    @NotNull
    @Override
    public FluidStack getFluid() {
        return tankie.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return tankie.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return tankie.getCapacity();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return tankie.isFluidValid(stack);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tankie.getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return tankie.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tankie.isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return tankie.fill(resource,action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return tankie.drain(maxDrain, action);
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return tankie.drain(resource,action);
    }

    private class Provider implements ICapabilityProvider {
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            {
                return LazyOptional.of(() -> WaterFluidTankCapabilityAdapter.this).cast();
            }
            return null;
        }
    }

}