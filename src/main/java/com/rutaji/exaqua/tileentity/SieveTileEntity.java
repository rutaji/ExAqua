package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.integration.mekanism.EnergyStorageAdapter;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class SieveTileEntity extends TileEntity implements ITickableTileEntity {
    public SieveTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public SieveTileEntity(){
        this(ModTileEntities.SIEVERTILE.get());
    }

    private final MyEnergyStorage energyStorage = new MyEnergyStorage(MyEnergyStorage.CountRF(10000));




    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if(cap.getName() == "mekanism.api.energy.IStrictEnergyHandler"){
            return energyStorage.getCapabilityProvider().getCapability(cap,side);
        }
        return null;
    }
    @Override
    public void tick() {
        if (!world.isRemote) {
            // Your machine's logic here. Consume energy based on its functionality.

        }
    }

}
