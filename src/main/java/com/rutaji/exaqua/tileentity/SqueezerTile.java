package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;


public class SqueezerTile extends TileEntity implements IMyLiquidTankTIle {

    //region Constructor
    public SqueezerTile(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public SqueezerTile(){
        this(ModTileEntities.SQUEEZERTILE.get());
    }
    //endregion

    //region inventory
    private final ItemStackHandler ITEM_STACK_HANDLER = createHandler();
    private final LazyOptional<IItemHandler> HANDLER = LazyOptional.of(() -> ITEM_STACK_HANDLER);
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
    //endregion
    //region nbt
    @Override
    public void read(BlockState state, CompoundNBT nbt){
        ITEM_STACK_HANDLER.deserializeNBT(nbt.getCompound("inv"));
        Tank.deserializeNBT(nbt);
        super.read(state,nbt);
    }

    @Override
    public CompoundNBT write( CompoundNBT nbt){
        nbt.put("inv", ITEM_STACK_HANDLER.serializeNBT());
        nbt = Tank.serializeNBT(nbt);
        return super.write(nbt);
    }
    @Override //server send on chung load
    public CompoundNBT getUpdateTag(){
        CompoundNBT nbt = new CompoundNBT();
        return write(nbt);
    }
    //clients receives getUpdateTag
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt){
        read(state,nbt);
    }
    //endregion

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side){
        if(cap.getName() == "net.minecraftforge.fluids.capability.IFluidHandler" )
            return Tank.getCapabilityProvider().getCapability(cap, side);
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return HANDLER.cast();
        }
        return super.getCapability(cap,side);
    }
    public void craft() {

        if(!world.isRemote()) {
            System.out.println(Tank.FluidStored.getAmount());
            if (Tank.FluidStored.getAmount() == Tank.getCapacity()) {
                return;
            }
            Inventory inv = new Inventory(ITEM_STACK_HANDLER.getSlots());
            for (int i = 0; i < ITEM_STACK_HANDLER.getSlots(); i++) {
                inv.setInventorySlotContents(i, ITEM_STACK_HANDLER.getStackInSlot(i));
            }

            Optional<SqueezerRecipie> recipe = world.getRecipeManager()
                    .getRecipe(ModRecipeTypes.SQUEEZER_RECIPE, inv, world);

            recipe.ifPresent(iRecipe -> {

                if (iRecipe instanceof SqueezerRecipie) {
                    ITEM_STACK_HANDLER.extractItem(0, 1, false);
                    FluidStack output = iRecipe.getRealOutput();
                    this.Tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                    System.out.println(output);
                    markDirty();
                }
            });
        }
    }
    @Override
    public void TankChange()
    {
        if(world != null &&!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyFluidStackPacket(Tank.FluidStored, pos));
        }

    }
    //region Liquid
    public MyLiquidTank Tank = new MyLiquidTank(this::TankChange);

    @Override
    public MyLiquidTank GetTank() {
        return this.Tank;
    }
    //endregion
}
