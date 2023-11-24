package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.AutoSqueezerContainer;
import com.rutaji.exaqua.container.SqueezerContainer;
import com.rutaji.exaqua.tileentity.AutoSqueezerTileEntity;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTIle;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import com.rutaji.exaqua.tileentity.SqueezerTile;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AutoSqueezerBlock extends Block implements IBucketPickupHandler, ILiquidContainer {

    //region constructor
    public AutoSqueezerBlock(AbstractBlock.Properties p_i48440_1_) {
        super(p_i48440_1_);
    }
    //endregion

    //region model
    public final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(1, 0, 1, 15, 1, 15),
            Block.makeCuboidShape(1, 1, 1, 2, 10, 2),
            Block.makeCuboidShape(14, 1, 1, 15, 10, 2),
            Block.makeCuboidShape(14, 1, 14, 15, 10, 15),
            Block.makeCuboidShape(1, 1, 14, 2, 10, 15),
            Block.makeCuboidShape(1, 10, 1, 15, 13, 15),
            Block.makeCuboidShape(4, 6, 4, 12, 10, 12),
            Block.makeCuboidShape(4, 1, 4, 12, 5, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();


    public VoxelShape getShape(BlockState blockState, IBlockReader worlIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }
    //endregion

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof AutoSqueezerTileEntity) {
                //region UI
                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
                NetworkHooks.openGui(((ServerPlayerEntity) player), containerProvider, tileEntity.getPos());
                //endregion
            } else {
                throw new IllegalStateException("Wrong TileEntity!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.exaqua.autosqueezercontainer");
            }
            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory inventory, PlayerEntity playerEn) {
                return new AutoSqueezerContainer(i,worldIn,pos,inventory,playerEn);
            }
        };

    }
    //region tile entity
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.AUTO_SQUEEZER_ENTITY.get().create();
    }
    //endregion
    //region bucket implementation
    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle){
            if(((IMyLiquidTankTIle) tileEntity).GetTank().getFluidAmount() >= 1000){
                return ((IMyLiquidTankTIle) tileEntity).GetTank().drain(1000, IFluidHandler.FluidAction.EXECUTE).getFluid();
            }
            return Fluids.EMPTY;
        }
        return Fluids.EMPTY;
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle)
        {
            return  ((IMyLiquidTankTIle)tileEntity).GetTank().CanTakeFluid(fluidIn);
        }
        return false;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle)
        {
            ((IMyLiquidTankTIle)tileEntity).GetTank().AddBucket(fluidStateIn.getFluid());
        }
        return true;
    }
    //endregion
}
