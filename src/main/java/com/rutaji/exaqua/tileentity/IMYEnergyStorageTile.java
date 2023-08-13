package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;

public interface IMYEnergyStorageTile {
    public MyEnergyStorage GetEnergyStorage();
    public void EnergyChangePacket();

}
