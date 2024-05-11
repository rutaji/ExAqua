package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;

/**
 * Interface used for tile entities that have {@link MyEnergyStorage MyEnergyStorage}.
 */
public interface IMYEnergyStorageTile {
    /**
     * @return reference to energy storage.
     */
    MyEnergyStorage GetEnergyStorage();

    /**
     * Method used for syncing client with the server.
     * Pass into {@link MyEnergyStorage MyEnergyStorage} as delegate. Called every time energy stored in {@link MyEnergyStorage MyEnergyStorage} changes.
     * @see MyEnergyStorage#OnChange
     * @see com.rutaji.exaqua.networking.MyEnergyPacket
     */
    void EnergyChangePacket();

}
