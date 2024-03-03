package com.rutaji.exaqua.container;

import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.block.SieveTiers;
import com.rutaji.exaqua.tileentity.IMYEnergyStorageTile;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTIle;
import com.rutaji.exaqua.tileentity.SieveTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class SieveContainer extends Container {

    private final TileEntity TILEENTITY;
    private final PlayerEntity PLAYERENTITY;
    private final IItemHandler PLAYERINVENTORY;

    public SieveContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity){
        super(ModContainers.SIEVECONTAINER.get(),windowId);
        this.TILEENTITY = world.getTileEntity(pos);
        this.PLAYERENTITY = playerEntity;
        this.PLAYERINVENTORY = new InvWrapper(playerInventory);
        layoutPlayerInventorySlots(8,86);

        if(TILEENTITY != null){
            TILEENTITY.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                addSlot(new SlotItemHandler(h,0,49,35));
                addSlot(new SlotItemHandler(h,1,69,35));
                addSlot(new SlotItemHandler(h,2,89,35));
                addSlot(new SlotItemHandler(h,3,109,35));
                addSlot(new SlotItemHandler(h,4,49,55));
                addSlot(new SlotItemHandler(h,5,69,55));
                addSlot(new SlotItemHandler(h,6,89,55));
                addSlot(new SlotItemHandler(h,7,109,55));
            });
        }

    }
    //region Get

    public int GetLiquidAmount()
    {
        if (TILEENTITY instanceof IMyLiquidTankTIle){
            return ((IMyLiquidTankTIle) TILEENTITY).GetTank().getFluidAmount();
        }
        return -1;
    }
    public String GetLiquid()
    {
        if (TILEENTITY instanceof IMyLiquidTankTIle){
            if(((IMyLiquidTankTIle) TILEENTITY).GetTank().isEmpty()){return "Empty";}
            return new TranslationTextComponent(((IMyLiquidTankTIle) TILEENTITY).GetTank().getFluid().getFluid().getAttributes().getTranslationKey()).getString();
        }
        return "Does not contain storage";
    }
    public long GetEnergyAmount(){
        if (TILEENTITY instanceof IMYEnergyStorageTile){
            return (long)(((IMYEnergyStorageTile) TILEENTITY).GetEnergyStorage().getEnergyStored());
        }
        return -1;
    }

    @Override
    public boolean canInteractWith(@NotNull PlayerEntity player){
        return  isWithinUsableDistance(IWorldPosCallable.of(TILEENTITY.getWorld(),TILEENTITY.getPos()),
                player, ModBlocks.GetSIEVE(GetTier()).get());
    }
    public SieveTiers GetTier()
    {
        if(TILEENTITY instanceof SieveTileEntity){
           return  ((SieveTileEntity) TILEENTITY).GetTier();
        }
        return SieveTiers.error;
    }
    //endregion
    //region player inventory
    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }

        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }

        return index;
    }
    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(PLAYERINVENTORY, 9, leftCol, topRow, 9, 18, 3, 18);

        topRow += 58;
        addSlotRange(PLAYERINVENTORY, 0, leftCol, topRow, 9, 18);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private static final int TE_INVENTORY_SLOT_COUNT = 8;

    @Override
    public ItemStack transferStackInSlot(@NotNull PlayerEntity playerIn, int index) {
        Slot sourceSlot = inventorySlots.get(index);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!mergeItemStack(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.putStack(ItemStack.EMPTY);
        } else {
            sourceSlot.onSlotChanged();
        }
        sourceSlot.onTake(PLAYERENTITY, sourceStack);
        return copyOfSourceStack;
    }
    //endregion
}
