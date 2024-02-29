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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MyEnergyStorage implements IEnergyContainer,Capability.IStorage<IEnergyContainer>{
    //region RF conversion
    public static double fromRF(double d) {return (d*5)/2;}
    public static long fromRF(long d) {return (d*5)/2;}
    public static long ToRF(long rf) {return (rf*2)/5;}
    //endregion
    FloatingLong Energy = FloatingLong.create(0);
    //region Constructor
    public MyEnergyStorage(double capacity, MyDelegate m){
        MAXCAPACITY =FloatingLong.create(capacity);
        Onchange = m;
    }
    //endregion
    private final String NBTCONSTANT = "energystorage";
    public MyDelegate Onchange;
    private final FloatingLong MAXCAPACITY;
    public long GetAsRF(){
         FloatingLong test = getEnergy();
         return MyEnergyStorage.ToRF(test.getValue());
    }
    public boolean DrainRF(double rf){
        FloatingLong totake = FloatingLong.create(fromRF(rf));
        if(totake.smallerOrEqual(getEnergy())){
            extract(totake, Action.EXECUTE, AutomationType.MANUAL);
            return true;
        }
        return false;
    }
    @Override
    public @NotNull FloatingLong getEnergy() {
        return Energy;
    }

    @Override
    public void setEnergy(@NotNull FloatingLong energy) {
        Energy = energy;
        SendChangeToClient();
    }
    public void SendChangeToClient()
    {
        Onchange.Execute();
    }

    @Override
    public @NotNull FloatingLong getMaxEnergy() {
        return MAXCAPACITY;
    }

    @Override
    public void onContentsChanged() {

    }
    //region NBT
    public CompoundNBT serializeNBT(CompoundNBT nbt)
    {
        nbt.putString(NBTCONSTANT, getEnergy().toString());
        return nbt;
    }
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setEnergy(FloatingLong.parseFloatingLong(nbt.getString(NBTCONSTANT)));
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

    @Nullable
    @Override
    public INBT writeNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side) {
        CompoundNBT nbt = new CompoundNBT();
        if (instance instanceof MyEnergyStorage) {
            MyEnergyStorage EnergyTank = (MyEnergyStorage) instance;
            if (!EnergyTank.isEmpty()) {
                nbt.putDouble(NBTCONSTANT, Energy.getValue());
            }
        }
        return nbt;
    }

    @Override
    public void readNBT(Capability<IEnergyContainer> capability, IEnergyContainer instance, Direction side, INBT nbt) {
        if(instance instanceof MyEnergyStorage)
        {
            setEnergy( FloatingLong.create(((CompoundNBT) nbt).getDouble(NBTCONSTANT)));
        }

    }
    //endregion
}
