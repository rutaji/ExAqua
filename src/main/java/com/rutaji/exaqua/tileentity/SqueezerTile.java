package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Fluids.OneWayTank;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter;
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

import static com.rutaji.exaqua.integration.mekanism.WaterFluidTankCapabilityAdapter.MEKANISM_CAPABILITY;

public class SqueezerTile extends TileEntity  {

    public OneWayTank Tank = new OneWayTank();
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

    private final WaterFluidTankCapabilityAdapter CapabilityProvider = new WaterFluidTankCapabilityAdapter(Tank);
    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side){
        if(cap.getName() == "net.minecraftforge.fluids.capability.IFluidHandler") //todo net.minecraftforge.fluids.capability.IFluidHandler
            return Tank.getCapabilityProvider().getCapability(cap, side);
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }
        Capability<T> test = cap;
        return super.getCapability(cap,side);
    }
    public void craft() {
        System.out.println(Tank.FluidStored.getAmount());
        if(Tank.FluidStored.getAmount() == Tank.getCapacity()){return;}
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
                this.Tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                System.out.println(output);
                markDirty();
            }
        });
    }



}
