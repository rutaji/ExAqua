package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.others.SieveTiers;
import net.minecraft.inventory.Inventory;
import net.minecraftforge.fluids.FluidStack;
/**
 * Inventory used to comunicate with {@link SieveRecipe SieveRecipe} and {@link HandSieveRecipe HandSieveRecipe}.
 * Stores 1 item, sieve tier, 1 fluid, and it's amount. Used as parametr for {@link SieveRecipe#matches CauldronRecipe.matches()} and {@link HandSieveRecipe#matches CauldronRecipe.matches()}.
 * If used with {@link SieveRecipe SieveRecipe} tier variable is ignored and is not required.
 */
public class InventorySieve extends Inventory {
    public InventorySieve(){
        super(1);
    }
    //region fluidstack
    private FluidStack fluidStack = FluidStack.EMPTY;

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
