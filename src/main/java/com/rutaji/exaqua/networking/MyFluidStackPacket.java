package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.tileentity.IMyLiquidTankTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Used to send changes in {@link com.rutaji.exaqua.Fluids.MyLiquidTank liquid tank} from server to client.
 */
public class MyFluidStackPacket {

    /**
     * Fluid to set in {@link com.rutaji.exaqua.Fluids.MyLiquidTank liquid tank}.
     */
    public FluidStack PacketStack;
    /**
     * Position of  tile entity with the {@link com.rutaji.exaqua.Fluids.MyLiquidTank liquid tank}.
     */
    public BlockPos pos;
    //region Constructor
    public MyFluidStackPacket(FluidStack f,BlockPos p) {
        PacketStack = f;
        pos = p;
    }
    //endregion

    public static MyFluidStackPacket fromBytes(PacketBuffer buffer) {
       return new MyFluidStackPacket(FluidStack.readFromPacket(buffer),buffer.readBlockPos());
    }

    public static void toBytes(MyFluidStackPacket packet, PacketBuffer buffer) {
        packet.PacketStack.writeToPacket(buffer);
        buffer.writeBlockPos(packet.pos);
    }

    /**
     * Called on cliend. Sets fluid stored in {@link com.rutaji.exaqua.Fluids.MyLiquidTank liquid tank} to value stored in this packet.
     */
    public void handle(Supplier<NetworkEvent.Context> context) {
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        if ( tileEntity instanceof IMyLiquidTankTile) {
            ((IMyLiquidTankTile)tileEntity).GetTank().setFluid(PacketStack);
            context.get().setPacketHandled(true);
        }

    }
}

