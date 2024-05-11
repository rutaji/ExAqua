package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.data.recipes.SieveRecipe;
import com.rutaji.exaqua.others.SieveTiers;
import com.rutaji.exaqua.data.recipes.InventorySieve;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
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
import net.minecraftforge.energy.CapabilityEnergy;
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
 * Tile entity for {@link com.rutaji.exaqua.block.SieveBlock sieve block}.
 */
public class SieveTileEntity extends TileEntity implements ITickableTileEntity, IMyLiquidTankTile,IMYEnergyStorageTile {

    //region Items
    private final int NUMBER_OF_INVENTORY_SLOTS =8;
    private ItemStackHandler createHandler()
    {
        return new ItemStackHandler(NUMBER_OF_INVENTORY_SLOTS){
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
    private final ItemStackHandler ITEM_STACK_HANDLER = createHandler();
    private final LazyOptional<IItemHandler> HANDLER = LazyOptional.of(() -> ITEM_STACK_HANDLER);


    //endregion
    //region nbt
    /**
     * Reads data from NBT.
     */
    @Override
    public void read(@NotNull BlockState state, CompoundNBT nbt){
        ITEM_STACK_HANDLER.deserializeNBT(nbt.getCompound("inv"));
        tier = SieveTiers.valueOf(nbt.getString("tier"));
        Tank.readFromNBT(nbt);
        GetEnergyStorage().deserializeNBT(nbt);
        super.read(state,nbt);
    }
    /**
     *Writes data to NBT.
     */
    @Override
    public @NotNull CompoundNBT write(CompoundNBT nbt){
        nbt.put("inv", ITEM_STACK_HANDLER.serializeNBT());
        nbt.putString("tier",tier.name());
        nbt = Tank.writeToNBT(nbt);
        nbt = GetEnergyStorage().serializeNBT(nbt);
        return super.write(nbt);
    }
    /**
     * Writes data to NBT. Just calls {@link SieveTileEntity#write write}.
     * Used to send chunks to a client.
     */
    @Override
    public @NotNull CompoundNBT getUpdateTag(){
        CompoundNBT nbt = new CompoundNBT();
        return write(nbt);
    }
    /**
     * Reads data from NBT. Just calls {@link SieveTileEntity#read read}.
     * Used to update chunks on a client.
     */
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt){
        read(state,nbt);
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
    private final MyEnergyStorage MY_ENERGY_STORAGE = new MyEnergyStorage(9000,this::EnergyChangePacket);

    /**
     * @return energy storage in this tile entity.
     * @see MyEnergyStorage
     */
    @Override
    public MyEnergyStorage GetEnergyStorage() {
        return this.MY_ENERGY_STORAGE;
    }


    /**
     * Called every time {@link MyEnergyStorage energy storage} in tile entity changes.
     * Send changes to client from server.
     * @see MyEnergyPacket
     */
    public void EnergyChangePacket()
    {
        if(world != null && !world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyEnergyPacket(this.GetEnergyStorage().getEnergyStored(), pos));
        }
    }

    //endregion

    /**
     * Returns capabilities for Energy, fluid and items.
     * For CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, returns {@link com.rutaji.exaqua.Fluids.WaterFluidTankCapabilityAdapter WaterFluidTankCapabilityAdapter}.
     * For CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, returns {@link ItemStackHandler ItemStackHandler}.
     * For CapabilityEnergy.ENERGY, returns {@link com.rutaji.exaqua.Energy.EnergyStorageAdapter EnergyStorageAdapter}.
     */
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == CapabilityEnergy.ENERGY){
            return MY_ENERGY_STORAGE.getCapabilityProvider().getCapability(cap,side);
        }
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return Tank.getCapabilityProvider().getCapability(cap, side);
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return HANDLER.cast();
        }
        return super.getCapability(cap,side);
    }
    /**
     * Called every tick. Calls {@link SieveTileEntity#craft }.
     */
    @Override
    public void tick() {
        if (!world.isRemote) {

            craft();
        }
    }
    //region Tiers
    public SieveTiers GetTier(){return  tier;}
    public SieveTiers tier = SieveTiers.error;
    public void SetTier(SieveTiers tier){this.tier = tier;}
    //endregion
    //region crafting
    private boolean crafting = false;
    private ItemStack ItemTocraft;
    private int craftingTime;
    private int craftingTimeDone;
    private int rf;
    /**
     * Handles crafting.
     */
    public void craft() {

        if(!world.isRemote())
        {

            if (Tank.getFluidAmount() == 0 && !crafting) {
                return;
            }
            if(!crafting) {
                InventorySieve inv = new InventorySieve();
                inv.setFluidStack(Tank.GetFluidstack());
                inv.setTier(GetTier());

                Optional<SieveRecipe> recipe = world.getRecipeManager()
                        .getRecipe(ModRecipeTypes.SIEVE_RECIPE, inv, world);

                recipe.ifPresent(iRecipe -> {

                    if (iRecipe instanceof SieveRecipe) {
                        Tank.drain(iRecipe.INPUTFLUID.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                        ItemTocraft = iRecipe.getRandomItemStack();
                        craftingTime = 0;
                        craftingTimeDone = iRecipe.TIME;
                        crafting = true;
                        rf=iRecipe.RF;
                        markDirty();
                    }
                });
            }
            else if(craftingTime == craftingTimeDone)
            {
                ItemStack result = ItemTocraft;
                for (int i = 0; i < NUMBER_OF_INVENTORY_SLOTS; i++) {

                    result = ITEM_STACK_HANDLER.insertItem(i, result, false);
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
                if(this.GetEnergyStorage().TryDrainEnergy(rf)) {
                    craftingTime++;
                    markDirty();
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

    private MyLiquidTank Tank = new MyLiquidTank(this::TankChange,5000,e->true);
    /**
     * @return liquid tank in this tile entity.
     * @see MyLiquidTank
     */
    @Override
    public MyLiquidTank GetTank() {
        return this.Tank;
    }
    /**
     * Called every time {@link MyLiquidTank liquid tank} in tile entity changes.
     * Send changes to client from server.
     * @see MyFluidStackPacket
     */
    @Override
    public void TankChange()
    {
        if(world != null && !world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyFluidStackPacket(Tank.GetFluidstack(), pos));
        }

    }
    //endregion





}
