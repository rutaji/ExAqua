package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter;
import com.rutaji.exaqua.others.MyDelegate;
import mekanism.api.fluid.IExtendedFluidTank;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class MyLiquidTank extends FluidTank /*implements Capability.IStorage<IFluidHandler>/*,IFluidHandler*/  {

    // region Constructor
    public MyLiquidTank(MyDelegate m) {
        this(m,3000, e -> true);
    }
    public MyLiquidTank(MyDelegate m, int capacity, Predicate<FluidStack> validator) {
        super(capacity,validator);
        Onchange = m;
    }
    //endregion

    public ICapabilityProvider getCapabilityProvider() {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                if (cap.getName().equals("net.minecraftforge.fluids.capability.IFluidHandler")) {
                    return LazyOptional.of(() -> new WaterFluidTankCapabilityAdapter(MyLiquidTank.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
    private final String NBTCONSTANT = "liquidstored";
    public MyDelegate Onchange;

    public @NotNull FluidStack GetFluidstack(){return fluid;}

    public boolean IsFull()
    {
        return getFluidAmount() < getCapacity();
    }


    @Override
    public void onContentsChanged() {
        Onchange.Execute();
    }

    public void AddBucket(Fluid fluid)
    {
        fill(new FluidStack(fluid ,1000),FluidAction.EXECUTE);
    }
}
