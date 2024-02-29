package com.rutaji.exaqua.integration.mekanism;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EnergyStorageAdapter implements IMekanismStrictEnergyHandler {
    @Override
    public @NotNull List<IEnergyContainer> getEnergyContainers(@Nullable Direction side) {
        return Arrays.asList(new MyEnergyStorage[]{energyStorage});
    }

    @Override
    public void onContentsChanged() {

    }
    private final MyEnergyStorage energyStorage;

    public EnergyStorageAdapter(MyEnergyStorage storage)
    {
        energyStorage = storage;
    }

    public ICapabilityProvider initCapabilities() {
        return new EnergyStorageAdapter.Provider();
    }

    private class Provider implements net.minecraftforge.common.capabilities.ICapabilityProvider {
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
            if (capability.getName().equals("mekanism.api.energy.IStrictEnergyHandler"))
            {
                return LazyOptional.of(() -> EnergyStorageAdapter.this).cast();
            }
            return null;
        }
    }

}
