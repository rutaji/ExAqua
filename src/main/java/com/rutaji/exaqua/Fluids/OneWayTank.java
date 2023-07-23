package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter;
import mekanism.api.NBTConstants;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class OneWayTank implements IExtendedFluidTank , Capability.IStorage<IFluidHandler>,IFluidHandler  {

    @CapabilityInject(IMekanismFluidHandler.class)
    private static Capability<IMekanismFluidHandler> MEKANISM_CAPABILITY =  null;

    // Returns the capability provider for this fluid tank
    public ICapabilityProvider getCapabilityProvider() {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                if (cap != MEKANISM_CAPABILITY) {
                    return LazyOptional.of(() -> new WaterFluidTankCapabilityAdapter(OneWayTank.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
    public  FluidStack FluidStored =  FluidStack.EMPTY;
    private int Capacity = 3000;
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
        return stack.getFluid() == Fluids.WATER;
    }

    public int getTanks() {
        return 1;
    }

    @NotNull
    public FluidStack getFluidInTank(int tank) {
        return FluidStored;
    }

    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;//todo change later
    }

    @Override
    public void setStack(FluidStack stack) {
        FluidStored = stack;
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
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
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        int drained = maxDrain;
        if (FluidStored.getAmount() < drained)
        {
            drained = FluidStored.getAmount();
        }
        FluidStack stack = new FluidStack(FluidStored, drained);
        if (action.execute() && drained > 0)
        {
            FluidStored.shrink(drained);

        }
        return stack;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(FluidStored))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public void onContentsChanged() {

    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        INBT fluidTag = nbt.get(NBTConstants.STORED);
        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) fluidTag);
        if (fluidStack != null) {
            setStack(fluidStack);
        }
    }


    @Nullable
    @Override
    public INBT writeNBT(Capability<IFluidHandler> capability, IFluidHandler instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        if (instance instanceof OneWayTank) {
            OneWayTank fluidTank = (OneWayTank) instance;
            if (!fluidTank.isEmpty()) {
                nbt.put(NBTConstants.STORED, fluidTank.getFluid().writeToNBT(new CompoundNBT()));
            }
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<IFluidHandler> capability, IFluidHandler instance, Direction side, INBT nbt) {
        if (instance instanceof OneWayTank ) {
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT((CompoundNBT) nbt);
            if (fluidStack != null) {
                setStack(fluidStack);
            }
        }
    }
}
