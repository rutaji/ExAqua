package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.SqueezerContainer;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTIle;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import com.rutaji.exaqua.tileentity.SqueezerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
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
import net.minecraft.util.*;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class squeezerBlock extends Block implements IBucketPickupHandler, ILiquidContainer {

    //region constructor
    public squeezerBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.setDefaultState(this.getStateContainer().getBaseState().with(SQUEEZED,false));
    }
    //endregion
    @Override
    public void onEntityCollision(@NotNull BlockState state, net.minecraft.world.World worldIn, @NotNull BlockPos pos, Entity entity) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        double speed = entity.getMotion().y;
        if(speed < -0.1 && tileEntity instanceof SqueezerTile){
            if(!state.get(SQUEEZED))
            {

                worldIn.playSound(null,pos,SoundEvents.BLOCK_WOOD_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                ((SqueezerTile) tileEntity).squeez();
                changeBlockModel(worldIn, pos,true );
            }
        }
        super.onEntityCollision(state,worldIn,pos,entity);

    }
    //region model
    public void changeBlockModel(World world, BlockPos pos, boolean value) {
        world.setBlockState(pos, world.getBlockState(pos).with(SQUEEZED, value));
    }
    public final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(1, 0, 1, 15, 1, 15),
            Block.makeCuboidShape(1, 1, 1, 2, 10, 2),
            Block.makeCuboidShape(14, 1, 1, 15, 10, 2),
            Block.makeCuboidShape(14, 1, 14, 15, 10, 15),
            Block.makeCuboidShape(1, 1, 14, 2, 10, 15),
            Block.makeCuboidShape(1, 10, 1, 15, 13, 15),
            Block.makeCuboidShape(4, 6, 4, 12, 10, 12),
            Block.makeCuboidShape(4, 1, 4, 12, 4, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public final VoxelShape SHAPE2 = Stream.of(
            Block.makeCuboidShape(1, 0, 1, 15, 1, 15),
            Block.makeCuboidShape(1, 1, 1, 2, 8, 2),
            Block.makeCuboidShape(14, 1, 1, 15, 8, 2),
            Block.makeCuboidShape(14, 1, 14, 15, 8, 15),
            Block.makeCuboidShape(1, 1, 14, 2, 8, 15),
            Block.makeCuboidShape(1, 8, 1, 15, 10, 15),
            Block.makeCuboidShape(4, 1, 4, 12, 8, 12),
            Block.makeCuboidShape(1, 10, 1, 2, 12, 2),
            Block.makeCuboidShape(1, 10, 14, 2, 12, 15),
            Block.makeCuboidShape(14, 10, 14, 15, 12, 15),
            Block.makeCuboidShape(14, 10, 1, 15, 12, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public static final BooleanProperty SQUEEZED = BooleanProperty.create("squeezed");

    @Override
    public void fillStateContainer(StateContainer.Builder builder){builder.add(SQUEEZED);}

    @Override
    public BlockState getStateForPlacement(@NotNull BlockItemUseContext context) {
        return this.getDefaultState().with(SQUEEZED, false);
    }
    public @NotNull VoxelShape getShape(BlockState blockState, @NotNull IBlockReader worlIn, @NotNull BlockPos pos, @NotNull ISelectionContext context)
    {
        if(blockState.get(SQUEEZED)){
           return SHAPE2;
        }
        return SHAPE;
    }
    //endregion

    @Override
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        if(!worldIn.isRemote()) {
            if(state.get(SQUEEZED))
            {
                changeBlockModel(worldIn,pos,false);
                return ActionResultType.SUCCESS;
            }
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof SqueezerTile) {
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
            public @NotNull ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.exaqua.squeezercontainer");
            }
            @Override
            public @NotNull Container createMenu(int i, @NotNull PlayerInventory inventory, @NotNull PlayerEntity playerEn) {
                return new SqueezerContainer(i,worldIn,pos,inventory,playerEn);
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
        return ModTileEntities.SQUEEZERTILE.get().create();
    }
    //endregion
    //region bucket implementation
    @Override
    public @NotNull Fluid pickupFluid(IWorld worldIn, @NotNull BlockPos pos, @NotNull BlockState state) {
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
    public boolean canContainFluid(IBlockReader worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid fluidIn) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle)
        {
            return  ((IMyLiquidTankTIle)tileEntity).GetTank().isFluidValid(new FluidStack(fluidIn,1000));
        }
        return false;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluidStateIn) {

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle)
        {
            ((IMyLiquidTankTIle)tileEntity).GetTank().AddBucket(fluidStateIn.getFluid());
        }
        return true;
    }
    //endregion
}
