package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.SqueezerContainer;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import com.rutaji.exaqua.tileentity.SqueezerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class squeezerBlock extends Block implements IBucketPickupHandler {



    public squeezerBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }
    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, @NotNull Entity entityIn) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof SqueezerTile){
            ((SqueezerTile) tileEntity).craft();
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos,
                                             PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);

            if(tileEntity instanceof SqueezerTile) {
                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);

                NetworkHooks.openGui(((ServerPlayerEntity)player), containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.exaqua.squeezercontainer");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory inventory, PlayerEntity playerEn) {
                return new SqueezerContainer(i,worldIn,pos,inventory,playerEn);
            }
        };

    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.SQUEEZERTILE.get().create();
    }

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof SqueezerTile){
            if(((SqueezerTile) tileEntity).Tank.getFluidAmount() >= 1000){
                return ((SqueezerTile) tileEntity).Tank.drain(1000, IFluidHandler.FluidAction.EXECUTE).getFluid();
            }
            return Fluids.EMPTY;
        }
        return Fluids.EMPTY;
    }
}
