package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.others.MyDelegate;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

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
                if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                    return LazyOptional.of(() -> new WaterFluidTankCapabilityAdapter(MyLiquidTank.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
    public MyDelegate Onchange;

    public @NotNull FluidStack GetFluidstack(){return fluid;}

    public boolean IsFull()
    {
        return getFluidAmount() >= getCapacity();
    }

    public float GetFullness(){return getFluidAmount()/(float)getCapacity();}


    @Override
    public void onContentsChanged() {
        Onchange.Execute();
    }
    @Override
    public void setFluid(FluidStack stack)
    {
        this.fluid = stack;
        onContentsChanged();
    }

    public void AddBucket(Fluid fluid)
    {
        fill(new FluidStack(fluid ,1000),FluidAction.EXECUTE);
    }

    public boolean IsEmpty() {
        return getFluid().isEmpty();
    }
    public void ChangeFluidKeepAmount(Fluid fluid)
    {
        FluidStack fluidStack = new FluidStack(fluid,getFluidAmount());
        setFluid(fluidStack);
    }
}
