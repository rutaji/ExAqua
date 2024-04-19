package com.rutaji.exaqua.container;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.tileentity.IMYEnergyStorageTile;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTile;
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

/**
 * Container for AutoSqueezer UI.
 * This class handles logic behind UI. Runs on server and client side. Communicates with tile entity directly.
 */

public class AutoSqueezerContainer extends Container {
    @NotNull private  final  TileEntity TILEENTITY;
    private final PlayerEntity PLAYERENTITY;
    private final IItemHandler PLAYERINVENTORY;

    /**
     *
     * @param windowId
     * @param world world of the interacted block
     * @param pos position of the interacted block
     * @param playerInventory inventory of the player opening the UI
     * @param playerEntity player opening the UI
     */
    public AutoSqueezerContainer(int windowId, @NotNull World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity){
        super(ModContainers.AUTO_SQUEEZER_CONTAINER.get(),windowId);
        this.TILEENTITY = world.getTileEntity(pos);
        this.PLAYERENTITY = playerEntity;
        this.PLAYERINVENTORY = new InvWrapper(playerInventory);

        layoutPlayerInventorySlots(8,86);
        TILEENTITY.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h,0,80,43)));

    }
    //region Get
    /**
     * Return's -1 if tile entity doesn't implement IMyLiquidTankTile.
     * @return amount of fluid in tile entity
     */
    public int GetLiquidAmount()
    {
        if (TILEENTITY instanceof IMyLiquidTankTile){
            return ((IMyLiquidTankTile) TILEENTITY).GetTank().getFluidAmount();
        }
        ExAqua.LOGGER.warn("Error in {} .Tile entity {} doesn't implement ImyLiquidTankTile.",this,TILEENTITY);
        return -1;
    }
    /**
     * Returns "Empty" if tile entity is empty. If tile entity doesn't implement IMyLiquidTankTile returns "Cannot store fluids!".
     * @return translated name of the fluid inside tile entity
     */
    public String GetLiquid()
    {
        if (TILEENTITY instanceof IMyLiquidTankTile){
            if(((IMyLiquidTankTile) TILEENTITY).GetTank().isEmpty()){return "Empty";}
            return new TranslationTextComponent(((IMyLiquidTankTile) TILEENTITY).GetTank().getFluid().getFluid().getAttributes().getTranslationKey()).getString();
        }
        ExAqua.LOGGER.warn("Error in {} .Tile entity {} doesn't implement IMyLiquidTankTile.",this,TILEENTITY);
        return "Cannot store fluids!";
    }

    /**
     * returns -1 if tile entity doesn't implement IMYEnergyStorageTile
     * @return returns amount of energy stored in tile entity (in FE)
     */
    public long GetEnergyAmount(){
        if (TILEENTITY instanceof IMYEnergyStorageTile){
            return (long)(((IMYEnergyStorageTile) TILEENTITY).GetEnergyStorage().getEnergyStored());
        }
        ExAqua.LOGGER.warn("Error in {} .Tile entity {} doesn't implement IMYEnergyStorageTile.",this,TILEENTITY);
        return -1;
    }
    //endregion

    /**
     * returns if player can interact with block is connected with this container
     * @param player player trying to interact
     * @return
     */
    @Override
    public boolean canInteractWith(@NotNull PlayerEntity player){
        return  isWithinUsableDistance(IWorldPosCallable.of(TILEENTITY.getWorld(), TILEENTITY.getPos()),
                                                            player, ModBlocks.AUTO_SQUEEZER.get());
    }
    //region inventory

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

    private static final int TE_INVENTORY_SLOT_COUNT = 1;

    @Override
    public @NotNull ItemStack transferStackInSlot(@NotNull PlayerEntity playerIn, int index) {
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
