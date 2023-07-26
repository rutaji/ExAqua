package com.rutaji.exaqua.Fluids;

import com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter;
import com.rutaji.exaqua.javadoesnthavedelegatesitfuckingsucks.MyDelegate;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import mekanism.api.NBTConstants;
import mekanism.api.fluid.IExtendedFluidTank;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MyLiquidTank implements IExtendedFluidTank , Capability.IStorage<IFluidHandler>,IFluidHandler  {

    public MyLiquidTank(MyDelegate m) {
        Onchange = m;
    }
    public MyLiquidTank(MyDelegate m,int capacity) {
        Onchange = m;
        Capacity = capacity;
    }

    // Returns the capability provider for this fluid tank
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
    public MyDelegate Onchange;
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
        boolean test = stack.getFluid() == Fluids.WATER;
        System.out.println("valid" + test);
        return test;
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
        return this.isFluidValid(FluidStored);
    }

    @Override
    public void setStack(FluidStack stack) {

        FluidStored = stack;
        SendChangeToClient();
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
            SendChangeToClient();
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
        if (instance instanceof MyLiquidTank) {
            MyLiquidTank fluidTank = (MyLiquidTank) instance;
            if (!fluidTank.isEmpty()) {
                nbt.put(NBTConstants.STORED, fluidTank.getFluid().writeToNBT(new CompoundNBT()));
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
}
