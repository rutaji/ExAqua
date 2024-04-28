package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.Fluids.MyLiquidTank;

/**
 * Interface used for tile entities that have {@link MyLiquidTank MyLiquidTank}.
 */
public interface IMyLiquidTankTile {
    /**
     * @return reference to tank.
     */
    MyLiquidTank GetTank();
    /**
     * Method used for syncing client with the server.
     * Pass into {@link MyLiquidTank MyLiquidTank} as delegate. Called every time liquid stored in {@link MyLiquidTank MyLiquidTank} changes.
     * @see MyLiquidTank#OnChange
     * @see com.rutaji.exaqua.networking.MyFluidStackPacket
     */
    void TankChange();
}
