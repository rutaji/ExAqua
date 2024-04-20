package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

/**
 * Inventory used to comunicate with {@link CauldronRecipe CauldronRecipe}.
 * Stores 1 item, temperature state, 1 fluid, and it's amount. Used as parametr for {@link CauldronRecipe#matches CauldronRecipe.matches()}.
 */
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
