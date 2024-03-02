package com.rutaji.exaqua.Energy;

import com.rutaji.exaqua.integration.mekanism.EnergyStorageAdapter;
import com.rutaji.exaqua.others.MyDelegate;
import mekanism.api.Action;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MyEnergyStorage extends EnergyStorage implements Capability.IStorage<IEnergyContainer>{
    private static final String NBTCONSTANT = "energystorage";

    //region RF conversion
    public static int fromRF(int d) {return d;}
    public static int ToRF(int rf) {return rf;}
    //endregion

    //region Constructor
    public MyEnergyStorage(int capacity, MyDelegate m, int maxRecieve, int minRecieve){
        super(capacity,maxRecieve,minRecieve);
        Onchange = m;
    }
    public MyEnergyStorage(int capacity, MyDelegate m){
        this(capacity,m,9999999,0);
    }
    //endregion
    public MyDelegate Onchange;
    public int GetAsRF(){

         return getEnergyStored();
    }
    public boolean DrainRF(int rf){
        if (HasEnoughEnergy(rf))
        {
            extractEnergy(rf,false);
            return true;
        }
        return false;
    }
    public boolean HasEnoughEnergy(int energyAmounth)
    {
        return getEnergyStored() >= energyAmounth;
    }


    public void setEnergy(@NotNull int energy) {
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
                if (cap.getName().equals("mekanism.api.energy.IStrictEnergyHandler")) {
                    return LazyOptional.of(() -> new EnergyStorageAdapter(MyEnergyStorage.this)).cast();
                }
                return LazyOptional.empty();
            }
        };
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
