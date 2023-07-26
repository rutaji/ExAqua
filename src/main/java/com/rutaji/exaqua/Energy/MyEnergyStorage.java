package com.rutaji.exaqua.Energy;

import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.integration.mekanism.EnergyStorageAdapter;
import com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter;
import mekanism.api.NBTConstants;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;


public class MyEnergyStorage implements IEnergyContainer,Capability.IStorage<IEnergyContainer>{ //todo mabye more Interfaces will be required before it stops crashing
    public static double CountRF(double d)
    {
        return (d*5)/2;
    }
    public static double GetdoubleFroRF(double rf)
    {
        return (rf*2)/5;
    }
    FloatingLong Energy = FloatingLong.create(0);
    public MyEnergyStorage(double capacity){MaxCapacity =FloatingLong.create(capacity);}
    private static FloatingLong MaxCapacity ;
    @Override
    public FloatingLong getEnergy() {
        return Energy;
    }

    @Override
    public void setEnergy(FloatingLong energy) {
        Energy = energy;
    }

    @Override
    public FloatingLong getMaxEnergy() {
        return MaxCapacity;
    }

    @Override
    public void onContentsChanged() {

    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setEnergy(FloatingLong.parseFloatingLong(nbt.getString(NBTConstants.STORED)));
    }
    public ICapabilityProvider getCapabilityProvider() {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                if (cap.getName() == "mekanism.api.energy.IStrictEnergyHandler") {
                    return LazyOptional.of(() -> new EnergyStorageAdapter(MyEnergyStorage.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Nullable
    @Override
    public INBT writeNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        if (instance instanceof MyEnergyStorage) {
            MyEnergyStorage EnergyTank = (MyEnergyStorage) instance;
            if (!EnergyTank.isEmpty()) {
                nbt.putDouble(NBTConstants.STORED, Energy.getValue());
            }
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side, INBT nbt) {
        if(instance instanceof MyEnergyStorage)
        {
            setEnergy( FloatingLong.create(((CompoundNBT) nbt).getDouble(NBTConstants.STORED)));
        }

    }
}
