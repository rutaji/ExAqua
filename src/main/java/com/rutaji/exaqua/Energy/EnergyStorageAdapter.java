package com.rutaji.exaqua.Energy;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * This classed is used as middle men betweenn {@link MyEnergyStorage MyEnergyStorage} and other mods.
 * Every method calls it's equivalent in {@link MyEnergyStorage MyEnergyStorage}.
 */
public class EnergyStorageAdapter implements IEnergyStorage {

    private final MyEnergyStorage energyStorage;

    public EnergyStorageAdapter(MyEnergyStorage storage)
    {
        energyStorage = storage;
    }

    public ICapabilityProvider initCapabilities() {
        return new EnergyStorageAdapter.Provider();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive,simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract,simulate);
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canReceive();
    }

    private class Provider implements net.minecraftforge.common.capabilities.ICapabilityProvider {
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
            if (capability == CapabilityEnergy.ENERGY)
            {
                return LazyOptional.of(() -> EnergyStorageAdapter.this).cast();
            }
            return null;
        }
    }

}
