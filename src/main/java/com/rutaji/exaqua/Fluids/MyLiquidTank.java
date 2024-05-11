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

/**
 * Stores fluid in tile entities. These tile entities implement {@link com.rutaji.exaqua.tileentity.IMyLiquidTankTile IMyLiquidTankTile}.
 */
public class MyLiquidTank extends FluidTank /*implements Capability.IStorage<IFluidHandler>/*,IFluidHandler*/  {

    // region Constructor
    public MyLiquidTank(MyDelegate m) {
        this(m,3000, e -> true);
    }
    public MyLiquidTank(MyDelegate m, int capacity, Predicate<FluidStack> validator) {
        super(capacity,validator);
        OnChange = m;
    }
    //endregion

    /** Returns {@link WaterFluidTankCapabilityAdapter WaterFluidTankCapabilityAdapter} that translates this class into IFluidTank and IFluidHandler from Forge.
     * Used for comunication with other mods.
     * @return {@link WaterFluidTankCapabilityAdapter WaterFluidTankCapabilityAdapter} wrapped around this energy storage.
     */
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
    /** Delegate run every time stored fluid changes.
     * Used to send changes from server to client.
     */
    public MyDelegate OnChange;

    /**
     * @return stored fluid.
     */

    public @NotNull FluidStack GetFluidstack(){return fluid;}

    /**
     * @return true, if storage is full.
     */
    public boolean IsFull()
    {
        return getFluidAmount() >= getCapacity();
    }

    /**
     * @return number from 0 to 1 representing how full the storage is. 0 is empty. 1 is full.
     */
    public float GetFullness(){return getFluidAmount()/(float)getCapacity();}


    /**
     * Called every time stored fluid changes by {@link MyLiquidTank#setFluid  setEnergy}.
     * Executes {@link MyLiquidTank#OnChange OnChange} to send change to a client.
     */
    @Override
    public void onContentsChanged() {
        OnChange.Execute();
    }

    /**
     * Sets stored fluid to given fluid.
     */
    @Override
    public void setFluid(FluidStack stack)
    {
        this.fluid = stack;
        onContentsChanged();
    }

    /**
     * Adds 1 bucket of given fluid into the storage. Returns how much (in mB) fluid was added.
     * @return how much (in mB) fluid was added.
     */

    public int AddBucket(Fluid fluid)
    {
        return fill(new FluidStack(fluid ,1000),FluidAction.EXECUTE);
    }

    /**
     * @return true, if tank is empty.
     */
    public boolean IsEmpty() {
        return getFluid().isEmpty();
    }

    /**
     * Changes fluid stack stored to given fluid, but keeps the amount.
     */
    public void ChangeFluidKeepAmount(Fluid fluid)
    {
        FluidStack fluidStack = new FluidStack(fluid,getFluidAmount());
        setFluid(fluidStack);
    }
}
