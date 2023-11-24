package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;

public class InventoryCauldron extends Inventory {
    public InventoryCauldron(){
        super(1);
    }
    //region fluidstack
    private Fluid fluid;

    public CauldronTemperature getTemp() {
        return temp;
    }
    public int amount = 0;

    public void setTemp(CauldronTemperature temp) {
        this.temp = temp;
    }

    private CauldronTemperature temp;


    public void setFluid(Fluid fluid) {
        this.fluid = fluid;
    }
    public Fluid getFluid(){
        return fluid;
    }
    //endregion
}
