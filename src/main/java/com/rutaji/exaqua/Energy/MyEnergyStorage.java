package com.rutaji.exaqua.Energy;

import com.rutaji.exaqua.config.ServerModConfig;
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

/**
 * Stores energy in tile entities. These tile entities implement {@link com.rutaji.exaqua.tileentity.IMYEnergyStorageTile IMYEnergyStorageTile}.
 */
public class MyEnergyStorage extends EnergyStorage implements Capability.IStorage<IEnergyContainer>{
    private static final String NBTCONSTANT = "energystorage";
    //region Constructor
    public MyEnergyStorage(int capacity, MyDelegate onChange, int maxRecieve, int maxExtract){
        super(capacity,maxRecieve,maxExtract);
        this.OnChange = onChange;
    }
    public MyEnergyStorage(int capacity, MyDelegate onChange){
        this(capacity,onChange,9999999,999999);
    }
    //endregion
    /** Delegate run every time stored energy changes.
     * Used to send changes from server to client.
     */
    public MyDelegate OnChange;

    /**
     * Drains energy, but only if there is enough. Returns true if the energy was drain. If there wasn't enough energy nothing is drained and returns false.
     * Always return True if {@link ServerModConfig#RequireElectricity server config RequireElectricity} is False.
     * @param energy amount of energy to be drained in FE
     * @return true if energy was drained. False if no energy was drain.
     */
    public boolean TryDrainEnergy(int energy){
        if(!ServerModConfig.RequireElectricity.get()) {return true;}
        if (HasEnoughEnergy(energy))
        {
            extractEnergy(energy,false);
            return true;
        }
        return false;
    }

    /**
     * @return true if stored energy is higher or same as given parametr.
     */
    public boolean HasEnoughEnergy(int energyAmounth)
    {
        return getEnergyStored() >= energyAmounth;
    }

    /**
     * @return true, if storage can receive energy.
     */
    @Override
    public boolean canReceive()
    {
        return !IsFull() && this.maxReceive > 0;
    }

    /**
     * @return true if storage is full.
     */
    public boolean IsFull()
    {
        return getEnergyStored() == getMaxEnergyStored();
    }


    /**
     * Sets stored energy to given number.
     */
    public void setEnergy(int energy) {
        this.energy = energy;
        OnContentChanged();
    }

    /**
     * Called every time stored energy changes by {@link MyEnergyStorage#setEnergy setEnergy}.
     * Executes {@link MyEnergyStorage#OnChange OnChange} to send change to a client.
     */
    public void OnContentChanged()
    {
        OnChange.Execute();
    }

    /** Returns {@link EnergyStorageAdapter EnergyStorageAdapter} that translates this class into simple IEnergyStorage from Forge.
     * Used for comunication with other mods.
     * @return {@link EnergyStorageAdapter EnergyStorageAdapter} wrapped around this energy storage.
     */
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

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive
     *            Maximum amount of energy to be inserted.
     * @param simulate
     *            If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
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

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     * @param maxExtract
     *            Maximum amount of energy to be extracted.
     * @param simulate
     *            If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
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

    /**
     * Stores data in NBT.
     */
    public CompoundNBT serializeNBT(@NotNull CompoundNBT nbt)
    {
        nbt.putInt(NBTCONSTANT, getEnergyStored());
        return nbt;
    }

    /**
     * Reads data from NBT.
     */
    public void deserializeNBT(@NotNull CompoundNBT nbt) {
        setEnergy((nbt.getInt(NBTCONSTANT)));
    }


    /**
     * Stores data in NBT.
     */
    @Nullable
    @Override
    public INBT writeNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        if (instance instanceof MyEnergyStorage) {
                nbt.putInt(NBTCONSTANT, getEnergyStored());
        }
        return nbt;
    }
    /**
     * Reads data from NBT.
     */
    @Override
    public void readNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side, INBT nbt) {
        if(instance instanceof MyEnergyStorage)
        {
            setEnergy( ((CompoundNBT) nbt).getInt(NBTCONSTANT));
        }

    }
    //endregion
}
