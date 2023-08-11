package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.Energy.MyEnergyStorage;
import com.rutaji.exaqua.Fluids.MyLiquidTank;
import com.rutaji.exaqua.data.recipes.InventoryWithFluids;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SieveRecipie;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import com.rutaji.exaqua.integration.mekanism.EnergyStorageAdapter;
import com.rutaji.exaqua.networking.MyFluidStackPacket;
import com.rutaji.exaqua.networking.PacketHandler;
import mekanism.api.energy.IMekanismStrictEnergyHandler;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SieveTileEntity extends TileEntity implements ITickableTileEntity,IMyLiquidTankTIle {

    //region Items
    private ItemStackHandler createHandler()
    {
        return new ItemStackHandler(8){
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
    public SieveTileEntity(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }
    public SieveTileEntity(){
        this(ModTileEntities.SIEVERTILE.get());
    }


    public MyLiquidTank Tank = new MyLiquidTank(this::TankChange,5000);

    //region Energy
    private final MyEnergyStorage energyStorage = new MyEnergyStorage(MyEnergyStorage.CountRF(10000));
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
    //endregion
    @Override
    public void tick() {
        if (!world.isRemote) {
            // Your machine's logic here. Consume energy based on its functionality.
            craft();
        }
    }
    public void craft() {

        if(!world.isRemote())
        {
            System.out.println("Sieve crafting");
            Tank.setStack(new FluidStack(Fluids.WATER,50)); //todo delete this
            if (Tank.FluidStored.getAmount() == 0) {
                return;
            }

            InventoryWithFluids inv = new InventoryWithFluids();
            inv.setFluidStack(Tank.FluidStored);

            Optional<SieveRecipie> recipe = world.getRecipeManager()
                    .getRecipe(ModRecipeTypes.SIEVE_RECIPE, inv, world);

            recipe.ifPresent(iRecipe -> {

                if (iRecipe instanceof SieveRecipie) {
                    Tank.drain(iRecipe.InputFluid.getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    ItemStack result = iRecipe.getRandomItemStack();
                    itemStackHandler.insertItem(0,result,false);//todo channge
                    markDirty();
                }
            });


        }
    }

    @Override
    public MyLiquidTank GetTank() {
        return this.Tank;
    }
    public void TankChange()
    {
        if(!world.isRemote) {
            PacketHandler.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new MyFluidStackPacket(Tank.FluidStored, pos));
        }

    }
}
