package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import com.rutaji.exaqua.networking.MyEnergyPacket;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class AutoSqueezerTileEntity extends TileEntity implements IMyLiquidTankTIle, ITickableTileEntity ,IMYEnergyStorageTile{

    //region Constructor
    public AutoSqueezerTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public AutoSqueezerTileEntity(){
        this(ModTileEntities.AUTO_SQUEEZER_ENTITY.get());
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
        GetEnergyStorage().deserializeNBT(nbt);
        super.read(state,nbt);
    }

    @Override
    public CompoundNBT write( CompoundNBT nbt){
        nbt.put("inv", ITEM_STACK_HANDLER.serializeNBT());
        nbt = Tank.serializeNBT(nbt);
        nbt = GetEnergyStorage().serializeNBT(nbt);
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
    //region Energy
    private final MyEnergyStorage MY_ENERGY_STORAGE = new MyEnergyStorage(MyEnergyStorage.fromRF(9000),this::EnergyChangePacket);
    @Override
    public MyEnergyStorage GetEnergyStorage() {
        return this.MY_ENERGY_STORAGE;
    }


    public void EnergyChangePacket()
    {
        if(world != null && !world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyEnergyPacket(this.GetEnergyStorage().getEnergy(), pos));
        }
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
        if(cap.getName() == "mekanism.api.energy.IStrictEnergyHandler"){
            return MY_ENERGY_STORAGE.getCapabilityProvider().getCapability(cap,side);
        }
        return super.getCapability(cap,side);
    }
    private final int CraftingCooldownMax = 20;
    private int CraftingCooldown = CraftingCooldownMax;
    private final int CraftingRF = 5;
    @Override
    public void tick()
    {
        if(CraftingCooldown > 0 )
        {
           if( GetEnergyStorage().DrainRF(5))
           {
               CraftingCooldown--;
           }
        }
        else{
            craft();
        }

    }
    public void craft() {

        if(!world.isRemote() && !Tank.IsFull()) {
            Inventory inv = new Inventory(ITEM_STACK_HANDLER.getSlots());
            for (int i = 0; i < ITEM_STACK_HANDLER.getSlots(); i++) {
                inv.setInventorySlotContents(i, ITEM_STACK_HANDLER.getStackInSlot(i));
            }

            Optional<SqueezerRecipie> recipe = world.getRecipeManager()
                    .getRecipe(ModRecipeTypes.SQUEEZER_RECIPE, inv, world);

            recipe.ifPresent(iRecipe -> {
                FluidStack output = iRecipe.getRealOutput();
                if(!Tank.CanTakeFluid(output.getFluid())){return;}
                ITEM_STACK_HANDLER.extractItem(0, 1, false);

                this.Tank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                CraftingCooldown = CraftingCooldownMax;
                markDirty();
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
