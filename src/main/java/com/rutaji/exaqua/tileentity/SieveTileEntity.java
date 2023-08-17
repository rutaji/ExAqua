package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.block.Tiers;
import com.rutaji.exaqua.data.recipes.InventoryWithFluids;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SieveRecipie;
import com.rutaji.exaqua.networking.MyEnergyPacket;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SieveTileEntity extends TileEntity implements ITickableTileEntity,IMyLiquidTankTIle,IMYEnergyStorageTile {

    //region Items
    private int NumberOfInventorySlots =8;
    private ItemStackHandler createHandler()
    {
        return new ItemStackHandler(NumberOfInventorySlots){
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
    private final ItemStackHandler itemStackHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemStackHandler);

    @Override
    public void read(BlockState state, CompoundNBT nbt){
        itemStackHandler.deserializeNBT(nbt.getCompound("inv"));
        super.read(state,nbt);
    }
    @Override
    public CompoundNBT write( CompoundNBT nbt){
        nbt.put("inv",itemStackHandler.serializeNBT());
        return super.write(nbt);
    }
    //endregion
    //region Constructor
    public SieveTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public SieveTileEntity(){
        this(ModTileEntities.SIEVERTILE.get());
    }
    //endregion
    //region Energy
    private final MyEnergyStorage energyStorage = new MyEnergyStorage(MyEnergyStorage.fromRF(10000),this::EnergyChangePacket);
    @Override
    public MyEnergyStorage GetEnergyStorage() {
        return this.energyStorage;
    }


    public void EnergyChangePacket()
    {
        if(!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyEnergyPacket(this.GetEnergyStorage().getEnergy(), pos));
        }
    }

    //endregion
    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nullable Capability<T> cap, @Nullable Direction side){
        if(cap.getName() == "mekanism.api.energy.IStrictEnergyHandler"){
            return energyStorage.getCapabilityProvider().getCapability(cap,side);
        }
        if(cap.getName() == "net.minecraftforge.fluids.capability.IFluidHandler" )
            return Tank.getCapabilityProvider().getCapability(cap, side);
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }
        return super.getCapability(cap,side);
    }
    @Override
    public void tick() {
        if (!world.isRemote) {
            // Your machine's logic here. Consume energy based on its functionality.
            craft();
        }
    }
    public Tiers GetTier(){return  tier;}
    public Tiers tier;
    //region crafting
    private boolean crafting = false;
    private ItemStack ItemTocraft;
    private int craftingTime;
    private int craftingTimeDone;
    private double rf;
    public void craft() {

        if(!world.isRemote())
        {

            //Tank.setStack(new FluidStack(Fluids.WATER,50));
            if (Tank.FluidStored.getAmount() == 0 && !crafting) {
                return;
            }
            if(crafting == false) {
                InventoryWithFluids inv = new InventoryWithFluids();
                inv.setFluidStack(Tank.FluidStored);

                Optional<SieveRecipie> recipe = world.getRecipeManager()
                        .getRecipe(ModRecipeTypes.SIEVE_RECIPE, inv, world);

                recipe.ifPresent(iRecipe -> {

                    if (iRecipe instanceof SieveRecipie) {
                        Tank.drain(iRecipe.InputFluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                        ItemTocraft = iRecipe.getRandomItemStack();
                        craftingTime = 0;
                        craftingTimeDone = iRecipe.TIME;
                        crafting = true;
                        rf=iRecipe.RF;
                    }
                });
            }
            else if(craftingTime == craftingTimeDone)
            {
                ItemStack result = ItemTocraft;
                for (int i = 0; i < NumberOfInventorySlots; i++) {

                    result = itemStackHandler.insertItem(i, result, false);
                    if (result == ItemStack.EMPTY) {
                        break;
                    }
                }
                markDirty();
                if (result != ItemStack.EMPTY) {
                    dispence(result);
                }
                crafting = false;

            }
            else{
                if(this.GetEnergyStorage().DrainRF(rf)) {
                    craftingTime++;
                }
            }
        }
    }
    //endregion
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
    //region IMyLiquidTankTile
    public MyLiquidTank Tank = new MyLiquidTank(this::TankChange,5000);
    @Override
    public MyLiquidTank GetTank() {
        return this.Tank;
    }
    @Override
    public void TankChange()
    {
        if(!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyFluidStackPacket(Tank.FluidStored, pos));
        }

    }
    //endregion


}
