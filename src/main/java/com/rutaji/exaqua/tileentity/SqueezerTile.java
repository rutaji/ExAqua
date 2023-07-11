package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SqueezerTile extends TileEntity implements IFluidTank,IFluidHandler {

    private  FluidStack FluidStored =  FluidStack.EMPTY;
    private final ItemStackHandler itemStackHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);
    public SqueezerTile(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public SqueezerTile(){
        this(ModTileEntities.SQUEEZERTILE.get());
    }
    private ItemStackHandler createHandler()
    {
        return new ItemStackHandler(1){
            @Override
            protected void onContentsChanged(int slot){
                markDirty();
            }
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack){
                return super.isItemValid(slot, stack);
            }
        };
    }
    @Override
    public void read(BlockState state, CompoundNBT nbt){
        itemStackHandler.deserializeNBT(nbt.getCompound("inv"));
        super.read(state,nbt);
    }
    @Override
    public CompoundNBT write( CompoundNBT nbt){
        nbt.put("inv",itemStackHandler.serializeNBT());
        return super.write(nbt) ;
    }


    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side){
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }
        return super.getCapability(cap,side);
    }
    public void craft() {
        System.out.println(FluidStored.getAmount());
        if(FluidStored.getAmount() == getCapacity()){return;}
        Inventory inv = new Inventory(itemStackHandler.getSlots());
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inv.setInventorySlotContents(i, itemStackHandler.getStackInSlot(i));
        }

        Optional<SqueezerRecipie> recipe = world.getRecipeManager()
                .getRecipe(ModRecipeTypes.SQUEEZER_RECIPE, inv, world);

        recipe.ifPresent(iRecipe -> {

            if(iRecipe instanceof SqueezerRecipie) {
                itemStackHandler.extractItem(0, 1, false);
                FluidStack output = iRecipe.getRealOutput();
                this.fill(output, IFluidHandler.FluidAction.EXECUTE);
                System.out.println(output);
                markDirty();
            }
        });
    }


    @Nonnull
    @Override
    public FluidStack getFluid() {
        return FluidStored;
    }

    @Override
    public int getFluidAmount() {
        return FluidStored.getAmount();
    }

    @Override
    public int getCapacity() {
        return 2000;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return stack.getFluid() == Fluids.WATER;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return FluidStored;
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;//todo change later
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (action.simulate())
        {
            if (FluidStored.isEmpty())
            {
                return Math.min(getCapacity(), resource.getAmount());
            }
            if (!FluidStored.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(getCapacity() - FluidStored.getAmount(), resource.getAmount());
        }
        if (FluidStored.isEmpty())
        {
            FluidStored = new FluidStack(resource, Math.min(getCapacity(), resource.getAmount()));
            return FluidStored.getAmount();
        }
        if (!FluidStored.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = getCapacity() - FluidStored.getAmount();

        if (resource.getAmount() < filled)
        {
            FluidStored.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            FluidStored.setAmount(getCapacity());
        }
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        int drained = maxDrain;
        if (FluidStored.getAmount() < drained)
        {
            drained = FluidStored.getAmount();
        }
        FluidStack stack = new FluidStack(FluidStored, drained);
        if (action.execute() && drained > 0)
        {
            FluidStored.shrink(drained);

        }
        return stack;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !resource.isFluidEqual(FluidStored))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }
}
