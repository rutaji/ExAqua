package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipe;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Tile entity for {@link com.rutaji.exaqua.block.SqueezerBlock squeezer block}.
 */
public class SqueezerTileEntity extends TileEntity implements IMyLiquidTankTile, ITickableTileEntity {

    //region Constructor
    public SqueezerTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public SqueezerTileEntity(){
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
    /**
     * Reads data from NBT.
     */
    @Override
    public void read(@NotNull BlockState state, CompoundNBT nbt){
        ITEM_STACK_HANDLER.deserializeNBT(nbt.getCompound("inv"));
        Tank.readFromNBT(nbt);
        super.read(state,nbt);
    }
    /**
     *Writes data to NBT.
     */
    @Override
    public @NotNull CompoundNBT write(CompoundNBT nbt){
        nbt.put("inv", ITEM_STACK_HANDLER.serializeNBT());
        nbt = Tank.writeToNBT(nbt);
        return super.write(nbt);
    }
    /**
     * Writes data to NBT. Just calls {@link SqueezerTileEntity#write write}.
     * Used to send chunks to a client.
     */
    @Override
    public @NotNull CompoundNBT getUpdateTag(){
        CompoundNBT nbt = new CompoundNBT();
        return write(nbt);
    }
    /**
     * Reads data from NBT. Just calls {@link SqueezerTileEntity#read read}.
     * Used to update chunks on a client.
     */
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt){
        read(state,nbt);
    }
    //endregion


    /**
     * Returns capabilities for Energy, fluid and items.
     * For CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, returns {@link com.rutaji.exaqua.Fluids.WaterFluidTankCapabilityAdapter WaterFluidTankCapabilityAdapter}.
     * For CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, returns {@link ItemStackHandler ItemStackHandler}.
     */
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return Tank.getCapabilityProvider().getCapability(cap, side);
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return HANDLER.cast();
        }
        return super.getCapability(cap,side);
    }
    private int Tocraft = 0;
    /**
     * Called every tick. Calls {@link AutoSqueezerTileEntity#craft } if squeezr was recently squeezed.
     * @see SqueezerTileEntity#squeez()
     */
    @Override
    public void tick()
    {
            if(Tocraft > 0)
            {
                craft();
                Tocraft--;
            }
    }

    /**
     * Called when squeezer is squeezzed. Adds 5 to Tocraft variable.
     * This variable shows how many items can be crafted by this squeezer, and it is reduced by 1 at the end of every tick.
     */
    public void squeez()
    {
        Tocraft += 5;
    }
    public void craft() {

        if(!world.isRemote() && !Tank.IsFull()) {
            Inventory inv = new Inventory(ITEM_STACK_HANDLER.getSlots());
            for (int i = 0; i < ITEM_STACK_HANDLER.getSlots(); i++) {
                inv.setInventorySlotContents(i, ITEM_STACK_HANDLER.getStackInSlot(i));
            }

            Optional<SqueezerRecipe> recipe = world.getRecipeManager()
                    .getRecipe(ModRecipeTypes.SQUEEZER_RECIPE, inv, world);

            recipe.ifPresent(iRecipe -> {
                FluidStack output = iRecipe.getRealOutput();
                if(!Tank.isFluidValid(new FluidStack(output.getFluid(),output.getAmount()))){return;}
                ITEM_STACK_HANDLER.extractItem(0, 1, false);

                this.Tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                markDirty();
            });
        }
    }
    /**
     * Called every time {@link MyLiquidTank liquid tank} in tile entity changes.
     * Send changes to client from server.
     * @see MyFluidStackPacket
     */
    @Override
    public void TankChange()
    {

        if(world != null &&!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyFluidStackPacket(Tank.GetFluidstack(), pos));
        }

    }
    //region Liquid
    private MyLiquidTank Tank = new MyLiquidTank(this::TankChange);
    /**
     * @return liquid tank in this tile entity.
     * @see MyLiquidTank
     */
    @Override
    public MyLiquidTank GetTank() {
        return this.Tank;
    }




    //endregion
}
