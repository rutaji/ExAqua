package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter;
import com.rutaji.exaqua.others.MyDelegate;
import mekanism.api.NBTConstants;
import mekanism.api.fluid.IExtendedFluidTank;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MyLiquidTank implements IExtendedFluidTank , Capability.IStorage<IFluidHandler>,IFluidHandler  {

    // region Constructor
    public MyLiquidTank(MyDelegate m) {
        Onchange = m;
    }
    public MyLiquidTank(MyDelegate m,int capacity) {
        Onchange = m;
        Capacity = capacity;
    }
    //endregion


    public ICapabilityProvider getCapabilityProvider() {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                if (cap.getName() == "net.minecraftforge.fluids.capability.IFluidHandler") {
                    return LazyOptional.of(() -> new WaterFluidTankCapabilityAdapter(MyLiquidTank.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
    private final String NBTCONSTANT = "liquidstored";
    public MyDelegate Onchange;
    public  FluidStack FluidStored =  FluidStack.EMPTY;
    private int Capacity = 3000; //default capacity
    @Nonnull
    @Override
    public FluidStack getFluid() {
        return FluidStored;
    }

    @Override
    public int getFluidAmount() {
        return FluidStored.getAmount();
    }

    @Override
    public int getCapacity() {
        return Capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    public int getTanks() {
        return 1;
    }

    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return FluidStored;
    }

    public void AddBucket(Fluid f)
    {
           this.fill(new FluidStack(f,1000),FluidAction.EXECUTE);
    }
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.isFluidValid(FluidStored);
    }

    @Override
    public void setStack(FluidStack stack) {

        FluidStored = stack;
        SendChangeToClient();
    }
    public boolean CanTakeFluid(Fluid f){
        return !IsFull() && (f == FluidStored.getFluid() || FluidStored.isEmpty());
    }
    public boolean IsFull()
    {
        return FluidStored.getAmount() == getCapacity();
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        System.out.println("fil");
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (FluidStored.isEmpty())
            {
                return Math.min(getCapacity(), resource.getAmount());
            }
            if (!FluidStored.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(getCapacity() - FluidStored.getAmount(), resource.getAmount());
        }
        if (FluidStored.isEmpty())
        {
            FluidStored = new FluidStack(resource, Math.min(getCapacity(), resource.getAmount()));
            SendChangeToClient();
            return FluidStored.getAmount();
        }
        if (!FluidStored.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = getCapacity() - FluidStored.getAmount();

        if (resource.getAmount() < filled)
        {
            FluidStored.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            FluidStored.setAmount(getCapacity());
        }
        SendChangeToClient();
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        System.out.println("drain");
        int drained = maxDrain;
        if (FluidStored.getAmount() < drained)
        {
            drained = FluidStored.getAmount();
        }
        FluidStack stack = new FluidStack(FluidStored, drained);
        if (action.execute() && drained > 0)
        {
            FluidStored.shrink(drained);
            SendChangeToClient();

        }
        return stack;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        System.out.println("drain fluidstack");
        if (resource.isEmpty() || !resource.isFluidEqual(FluidStored))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }
    public void SendChangeToClient()
    {
        Onchange.Execute();
    }

    @Override
    public void onContentsChanged() {
        //I dont use it. It probadly never get´s called, but it needs to be there
    }
    //region nbt
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        INBT fluidTag = nbt.get(NBTCONSTANT);
        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) fluidTag);
        if (fluidStack != null) {
            setStack(fluidStack);
        }
    }

    public CompoundNBT serializeNBT(CompoundNBT nbt) {
        if (!isEmpty()) {
            nbt.put(NBTCONSTANT, getFluid().writeToNBT(new CompoundNBT()));
        }
        return nbt;
    }


    @Nullable
    @Override
    public INBT writeNBT(Capability<IFluidHandler> capability, IFluidHandler instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        if (instance instanceof MyLiquidTank) {
            MyLiquidTank fluidTank = (MyLiquidTank) instance;
            if (!fluidTank.isEmpty()) {
                nbt.put(NBTCONSTANT, fluidTank.getFluid().writeToNBT(new CompoundNBT()));
            }
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<IFluidHandler> capability, IFluidHandler instance, Direction side, INBT nbt) {
        if (instance instanceof MyLiquidTank) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) nbt);
            if (fluidStack != null) {
                setStack(fluidStack);
            }
        }
    }
    //endregion
}
