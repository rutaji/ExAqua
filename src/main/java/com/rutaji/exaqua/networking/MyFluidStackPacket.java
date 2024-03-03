package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.tileentity.IMyLiquidTankTIle;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MyFluidStackPacket {

    public FluidStack PacketStack;
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

    public void handle(Supplier<NetworkEvent.Context> context) {
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        if ( tileEntity instanceof IMyLiquidTankTIle) {
            ((IMyLiquidTankTIle)tileEntity).GetTank().setFluid(PacketStack);
            context.get().setPacketHandled(true);
        }

    }
}

