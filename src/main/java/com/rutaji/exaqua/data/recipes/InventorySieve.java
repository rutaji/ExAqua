package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.block.SieveTiers;
import net.minecraft.inventory.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class InventorySieve extends Inventory {
    public InventorySieve(){
        super(1);
    }
    //region fluidstack
    private FluidStack fluidStack;

    public void setFluidStack(FluidStack fluidStack) {
        this.fluidStack = fluidStack;
    }
    public FluidStack getFluid(){
        return fluidStack;
    }
    //endregion
    private SieveTiers tier;

    public void setTier(SieveTiers tier) {
        this.tier = tier;
    }
    public SieveTiers GetTier(){
        return tier;
    }


}
