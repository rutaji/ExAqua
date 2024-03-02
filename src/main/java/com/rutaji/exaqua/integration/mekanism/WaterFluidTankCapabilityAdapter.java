package com.rutaji.exaqua.integration.mekanism;

import com.rutaji.exaqua.Fluids.MyLiquidTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class WaterFluidTankCapabilityAdapter implements IMekanismFluidHandler {

    public MyLiquidTank tankie;
    public WaterFluidTankCapabilityAdapter(MyLiquidTank t)
    {
        tankie = t;
    }
    @Override
    public @NotNull List<IExtendedFluidTank> getFluidTanks(@Nullable Direction side) {

        return Arrays.asList(new IExtendedFluidTank[]{new IExtendedFluidTank() {
            MyLiquidTank tank = tankie;
            @Override
            public void setStack(FluidStack stack) {
                tank.setFluid(stack);
                tank.onContentsChanged();
            }

            @Override
            public void onContentsChanged() {
                System.out.println("onContent call from adapter");
                tank.onContentsChanged();
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                tank.readFromNBT(nbt);
            }

            @NotNull
            @Override
            public FluidStack getFluid() {
                return tank.GetFluidstack();
            }

            @Override
            public int getFluidAmount() {
                return tank.getFluidAmount();
            }

            @Override
            public int getCapacity() {
                return tank.getCapacity();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return tank.isFluidValid(stack);
            }
        }});
    }

    @Override
    public void onContentsChanged()
    {
        System.out.println("oncontent change called from adapter CLASS !! directly from class");
        tankie.onContentsChanged();
    }

    public ICapabilityProvider initCapabilities() {
         return new Provider();
    }
    private class Provider implements ICapabilityProvider {
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
            if (capability.getName().equals("net.minecraftforge.fluids.capability.IFluidHandler"))
            {
                return LazyOptional.of(() -> WaterFluidTankCapabilityAdapter.this).cast();
            }
            return null;
        }
    }

}