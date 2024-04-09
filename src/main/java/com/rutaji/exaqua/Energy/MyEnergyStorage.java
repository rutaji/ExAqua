package com.rutaji.exaqua.Energy;

import com.rutaji.exaqua.others.MyDelegate;
import mekanism.api.energy.IEnergyContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MyEnergyStorage extends EnergyStorage implements Capability.IStorage<IEnergyContainer>{
    private static final String NBTCONSTANT = "energystorage";
    //region Constructor
    public MyEnergyStorage(int capacity, MyDelegate m, int maxRecieve, int maxExtract){
        super(capacity,maxRecieve,maxExtract);
        Onchange = m;
    }
    public MyEnergyStorage(int capacity, MyDelegate m){
        this(capacity,m,9999999,999999);
    }
    //endregion
    public MyDelegate Onchange;
    public boolean TryDrainEnergy(int energy){
        if (HasEnoughEnergy(energy))
        {
            extractEnergy(energy,false);
            return true;
        }
        return false;
    }
    public boolean HasEnoughEnergy(int energyAmounth)
    {
        return getEnergyStored() >= energyAmounth;
    }
    @Override
    public boolean canReceive()
    {
        return !IsFull() && this.maxReceive > 0;
    }
    public boolean IsFull()
    {
        return getEnergyStored() == getMaxEnergyStored();
    }


    public void setEnergy(int energy) {
        this.energy = energy;
        SendChangeToClient();
    }
    public void SendChangeToClient()
    {
        Onchange.Execute();
    }

    public ICapabilityProvider getCapabilityProvider() {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                if (cap == CapabilityEnergy.ENERGY) {
                    return LazyOptional.of(() -> new EnergyStorageAdapter(MyEnergyStorage.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (!canReceive())
            return 0;

        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            setEnergy(energy + energyReceived);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            setEnergy(energy - energyExtracted);
        return energyExtracted;
    }


    //region NBT
    public CompoundNBT serializeNBT(@NotNull CompoundNBT nbt)
    {
        nbt.putInt(NBTCONSTANT, getEnergyStored());
        return nbt;
    }


    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        setEnergy((nbt.getInt(NBTCONSTANT)));
    }
    

    @Nullable
    @Override
    public INBT writeNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        if (instance instanceof MyEnergyStorage) {
            MyEnergyStorage EnergyTank = (MyEnergyStorage) instance;
                nbt.putInt(NBTCONSTANT, getEnergyStored());
        }
        return nbt;
    }
    @Override
    public void readNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side, INBT nbt) {
        if(instance instanceof MyEnergyStorage)
        {
            setEnergy( ((CompoundNBT) nbt).getInt(NBTCONSTANT));
        }

    }
    //endregion
}
