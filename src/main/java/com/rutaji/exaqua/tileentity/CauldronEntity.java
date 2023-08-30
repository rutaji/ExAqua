package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.data.recipes.CauldronRecipie;
import com.rutaji.exaqua.data.recipes.InventoryCauldron;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
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
    @Override
    public void tick() {
        craft();
    }
    private void craft()
    {
        if(!world.isRemote())
        {
            InventoryCauldron inv = new InventoryCauldron();
            for (int i = 0; i < ITEM_STACK_HANDLER.getSlots(); i++) {
                inv.setInventorySlotContents(i, ITEM_STACK_HANDLER.getStackInSlot(i));
            }
            inv.setFluid(Tank.getFluid().getFluid() == Fluids.EMPTY ? null:Tank.getFluid().getFluid());//todo rework null Empty !!!!
            inv.setTemp(this.GetTemp());
            inv.amount = Tank.getFluidAmount();

            Optional<CauldronRecipie> recipe = world.getRecipeManager()
                    .getRecipe(ModRecipeTypes.CAULDRON_RECIPE, inv, world);


            recipe.ifPresent(iRecipe -> {
                if(iRecipe.OUTPUT != null)//output is fluid
                {
                    if(iRecipe.OUTPUT == Tank.getFluid().getFluid() || Tank.isEmpty()) {
                        if (Tank.IsFull()) return;
                        Tank.fill(new FluidStack(iRecipe.OUTPUT, 50), IFluidHandler.FluidAction.EXECUTE);
                    }
                    else
                    {
                        Tank.setStack(new FluidStack(iRecipe.OUTPUT,Tank.getFluidAmount()));
                    }
                    if (iRecipe.INPUT_ITEM != null) {ITEM_STACK_HANDLER.extractItem(0,iRecipe.INPUT_ITEM.getCount(),false);}

                }
                else if (iRecipe.OUTPUT_ITEM != null) //output is item
                {
                    if (iRecipe.INPUT_ITEM != null) {ITEM_STACK_HANDLER.extractItem(0,iRecipe.INPUT_ITEM.getCount(),false);}
                    if(iRecipe.INPUT != null){Tank.drain(new FluidStack(iRecipe.INPUT,iRecipe.AMOUNT), IFluidHandler.FluidAction.EXECUTE);}
                    ItemStack result = ITEM_STACK_HANDLER.insertItem(0, iRecipe.OUTPUT_ITEM, false);
                    if(result != ItemStack.EMPTY)
                    {
                        dispence(result);
                    }

                }

            });
        }
    }
    //region dispence
    private void dispence(ItemStack stack){ dispence(stack,6);}
    private void dispence(ItemStack stack,int speed){
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();
        /*Direction facing = getBlockState().get(BlockStateProperties.FACING);
        if (facing.getAxis() == Direction.Axis.Y) {*/
        d1 = d1 - 0.125D;
        /*} else {
            d1 = d1 - 0.15625D;
        }*/

        ItemEntity itementity = new ItemEntity(world, d0, d1, d2, stack);
        double d3 = world.rand.nextDouble() * 0.1D + 0.2D;
        itementity.setMotion(world.rand.nextGaussian() * (double)0.0075F * (double)speed +  d3, world.rand.nextGaussian() * (double)0.0075F * (double)speed + (double)0.2F, world.rand.nextGaussian() * (double)0.0075F * (double)speed + d3);
        world.addEntity(itementity);
    }
    //endregion

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
