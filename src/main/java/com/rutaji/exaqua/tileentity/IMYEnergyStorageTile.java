package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;

public interface IMYEnergyStorageTile {
    MyEnergyStorage GetEnergyStorage();
    void EnergyChangePacket();

}
