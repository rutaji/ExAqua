package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CauldronEntity extends TileEntity implements IMyLiquidTankTIle, ITickableTileEntity {
    public CauldronEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    public CauldronEntity(){
        this(ModTileEntities.CAULDRON_ENTITY.get());
    }
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
    //region Liquid
    public MyLiquidTank Tank = new MyLiquidTank(this::TankChange,1000);

    @Override
    public MyLiquidTank GetTank() {
        return this.Tank;
    }
    @Override
    public void TankChange() {
        if(world != null &&!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyFluidStackPacket(Tank.FluidStored, pos));
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
        return super.getCapability(cap,side);
    }
    @Override
    public void tick() {

    }

    public CauldronTemperature GetTemp()
    {
        BlockState blockState = world.getBlockState(pos.add(0,-1,0));
        Block block = blockState.getBlock();
        if(block == Blocks.TORCH || block == Blocks.WALL_TORCH || (block == Blocks.CAMPFIRE && blockState.get(CampfireBlock.LIT)) || block == Blocks.LAVA || block == Blocks.MAGMA_BLOCK || block == Blocks.FIRE)
        {return CauldronTemperature.hot;}
        if(block == Blocks.SOUL_FIRE || block == Blocks.SOUL_TORCH || block == Blocks.SOUL_WALL_TORCH || (block == Blocks.SOUL_CAMPFIRE && blockState.get(CampfireBlock.LIT)) )
        {return CauldronTemperature.cold;}
        return  CauldronTemperature.neutral;
    }
}
