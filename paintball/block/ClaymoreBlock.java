package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.block.entity.ClaymoreBlockEntity;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ClaymoreBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty ROT = IntegerProperty.create("rot", 0, 15);
    private static final VoxelShape OUTLINE = Block.box(4, 0, 4, 12, 8, 12);
    private static final VoxelShape COLLISION = Block.box(4, 0, 4, 12, 8, 12);
    private static final SoundType CLAYMORE_SOUND = new SoundType(
            1.0F, 1.0F,
            SoundEventsRegistry.CLAYMORE_PLACE.get(),
            SoundEvents.STONE_STEP,
            SoundEventsRegistry.CLAYMORE_PLACE.get(),
            SoundEvents.STONE_HIT,
            SoundEvents.STONE_FALL
    );

    private final DyeColor color;

    public ClaymoreBlock(Properties properties, DyeColor color) {
        super(properties.sound(CLAYMORE_SOUND));
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ROT, 0));
    }

    public DyeColor getColor() { return color; }

    @Override public VoxelShape getShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) { return OUTLINE; }
    @Override public VoxelShape getCollisionShape(BlockState s, BlockGetter l, BlockPos p, CollisionContext c) { return COLLISION; }
    @Override public RenderShape getRenderShape(BlockState s) { return RenderShape.ENTITYBLOCK_ANIMATED; }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        int rot = 0;
        if (ctx.getPlayer() != null) {
            float yaw = ctx.getPlayer().getYRot();
            rot = (Mth.floor((yaw * 16.0F / 360.0F) + 0.5F) & 15);
        }
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(ROT, rot);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING, ROT);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        int steps = switch (rotation) {
            case NONE -> 0;
            case CLOCKWISE_90 -> 4;
            case CLOCKWISE_180 -> 8;
            case COUNTERCLOCKWISE_90 -> 12;
        };
        Direction newFacing = rotation.rotate(state.getValue(FACING));
        return state.setValue(FACING, newFacing)
                .setValue(ROT, (state.getValue(ROT) + steps) & 15);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ClaymoreBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ClaymoreBlockEntity clay) {
                clay.onPlacedBy(placer.getUUID());
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, p, st, be) -> {
            if (be instanceof ClaymoreBlockEntity clay) {
                ClaymoreBlockEntity.serverTick(lvl, p, st, clay);
            }
        };
    }
}