package com.rutaji.exaqua.container;

import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTIle;
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

public class SqueezerContainer extends Container {
    private final TileEntity TILEEMTITY;
    private final PlayerEntity PLAYERENTITY;
    private final IItemHandler PLAYERINVENTORY;

    public SqueezerContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity playerEntity){
        super(ModContainers.SQUEEZERCONTAINER.get(),windowId);
        this.TILEEMTITY = world.getTileEntity(pos);
        this.PLAYERENTITY = playerEntity;
        this.PLAYERINVENTORY = new InvWrapper(playerInventory);
        layoutPlayerInventorySlots(8,86);

        if(TILEEMTITY != null){
            TILEEMTITY.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> addSlot(new SlotItemHandler(h,0,80,43)));
        }

    }
    public int GetLiquidAmount()
    {
        if (TILEEMTITY instanceof IMyLiquidTankTIle){
            return ((IMyLiquidTankTIle) TILEEMTITY).GetTank().getFluidAmount();
        }
        return -1;
    }
    public String GetLiquid()
    {
        if (TILEEMTITY instanceof IMyLiquidTankTIle){
            if(((IMyLiquidTankTIle) TILEEMTITY).GetTank().isEmpty()){return "Empty";}
            return new TranslationTextComponent(((IMyLiquidTankTIle) TILEEMTITY).GetTank().getFluid().getFluid().getAttributes().getTranslationKey()).getString();
        }
        return "Doesnt have a container";
    }

    @Override
    public boolean canInteractWith(@NotNull PlayerEntity player){
        return  isWithinUsableDistance(IWorldPosCallable.of(TILEEMTITY.getWorld(), TILEEMTITY.getPos()),
                                                            player, ModBlocks.SQUEEZER.get());
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

    private void addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }

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
