package com.rutaji.exaqua.data.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class InventoryWithFluids extends Inventory {
    public InventoryWithFluids(){
        super(1);
    }
    private FluidStack fluidStack;

    public void setFluidStack(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }
    public FluidStack getFluid(){
        return fluidStack;
    }
}
