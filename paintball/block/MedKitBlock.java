package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.block.entity.MedKitBlockEntity;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MedKitBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private final DyeColor color;
    private static final VoxelShape SHAPE_NS = Block.box(
            2.0D, 0.0D, 4.0D,
            14.0D, 11.75D, 12.0D
    );

    private static final VoxelShape SHAPE_EW = Block.box(
            4.0D, 0.0D, 2.0D,
            12.0D, 11.75D, 14.0D
    );

    public MedKitBlock(DyeColor color, Properties props) {
        super(props);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public DyeColor getColor() {
        return this.color;
    }

    private static VoxelShape getShapeForDirection(Direction dir) {
        return (dir == Direction.NORTH || dir == Direction.SOUTH) ? SHAPE_NS : SHAPE_EW;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                               CollisionContext context) {
        return getShapeForDirection(state.getValue(FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return getShapeForDirection(state.getValue(FACING));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MedKitBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) return null;

        if (type == ModBlockEntities.MEDKIT_BLOCK_ENTITY.get()) {
            return (lvl, pos, st, be) ->
                    MedKitBlockEntity.serverTick(lvl, pos, st, (MedKitBlockEntity) be);
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (player != null && player.getItemInHand(hand).getItem()
                instanceof com.zerokg2004.paintball.item.PaintbrushItem) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MedKitBlockEntity medkit) {
            boolean healed = medkit.tryHeal(player);

            if (healed) {
                level.playSound(null, pos, SoundEventsRegistry.MEDKIT_HEAL.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}