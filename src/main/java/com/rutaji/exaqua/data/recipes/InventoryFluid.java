package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.block.SieveTiers;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class InventoryFluid extends Inventory {
    public InventoryFluid(){
        super(1);
    }
    //region fluidstack
    private Fluid fluid;

    public void setFluidStack(Fluid fluidStack) {
        this.fluid = fluidStack;
    }
    public Fluid getFluid(){
        return fluid;
    }
    //endregion
}
