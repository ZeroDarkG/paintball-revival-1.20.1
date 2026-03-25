package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.block.entity.DecisionRouletteBlockEntity;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DecisionRouletteBlock extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<RoulettePart> PART = EnumProperty.create("part", RoulettePart.class);

    private static final VoxelShape SHAPE_NS = Block.box(
            0.0D, 0.0D, 5.0D,
            16.0D, 16.0D, 11.0D
    );

    private static final VoxelShape SHAPE_EW = Block.box(
            5.0D, 0.0D, 0.0D,
            11.0D, 16.0D, 16.0D
    );

    private static final BlockPos[] RELATIVE_POSITIONS = new BlockPos[] {
            new BlockPos(0, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(0, 2, 0),
            new BlockPos(-1, 2, 0),
            new BlockPos(1, 2, 0),
    };

    public DecisionRouletteBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, RoulettePart.ROOT));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == RoulettePart.ROOT ? new DecisionRouletteBlockEntity(pos, state) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockPos basePos = ctx.getClickedPos();
        Level level = ctx.getLevel();

        if (!canPlaceMultiblock(level, basePos, facing)) {
            return null;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, RoulettePart.ROOT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) return;

        Direction facing = state.getValue(FACING);

        for (BlockPos rel : RELATIVE_POSITIONS) {
            BlockPos target = rotateRelative(pos, rel, facing);
            if (target.equals(pos)) continue;

            BlockState partState = this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(PART, RoulettePart.PART);

            level.setBlock(target, partState, 3);
        }
    }

    private static VoxelShape shapeFor(BlockState state) {
        Direction f = state.getValue(FACING);
        return (f == Direction.EAST || f == Direction.WEST) ? SHAPE_EW : SHAPE_NS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return shapeFor(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return shapeFor(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return shapeFor(state);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return shapeFor(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        BlockPos rootPos = (state.getValue(PART) == RoulettePart.ROOT) ? pos : findRoot(level, pos);
        if (rootPos == null) return InteractionResult.FAIL;

        BlockEntity be = level.getBlockEntity(rootPos);
        if (!(be instanceof DecisionRouletteBlockEntity roulette)) {
            return InteractionResult.PASS;
        }

        float currentAngle = roulette.getArrowAngle();
        int angleBits = Float.floatToIntBits(currentAngle);

        long seed =
                rootPos.asLong()
                        ^ player.getUUID().getMostSignificantBits()
                        ^ player.getUUID().getLeastSignificantBits()
                        ^ angleBits;

        if (level.isClientSide) {
            roulette.startClientSpin(seed);
            return InteractionResult.SUCCESS;
        } else {
            roulette.onUse(player, seed);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.isClientSide) {
            super.playerWillDestroy(level, pos, state, player);
            return;
        }

        BlockPos rootPos;
        Direction facing;

        if (state.getValue(PART) == RoulettePart.ROOT) {
            rootPos = pos;
            facing = state.getValue(FACING);
        } else {
            rootPos = findRoot(level, pos);
            if (rootPos == null) {
                level.removeBlock(pos, false);
                return;
            }
            facing = level.getBlockState(rootPos).getValue(FACING);
        }

        for (BlockPos rel : RELATIVE_POSITIONS) {
            BlockPos target = rotateRelative(rootPos, rel, facing);
            BlockState st = level.getBlockState(target);
            if (st.getBlock() == this) {
                level.levelEvent(2001, target, Block.getId(st));
                level.removeBlock(target, false);
            }
        }

        if (!player.isCreative()) {
            popResource(level, rootPos, new ItemStack(this));
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntities.ROULETTE_BLOCK_ENTITY.get()) return null;
        return (lvl, pos, st, be) -> DecisionRouletteBlockEntity.tick(lvl, pos, st, (DecisionRouletteBlockEntity) be);
    }

    private boolean canPlaceMultiblock(Level level, BlockPos pos, Direction facing) {
        for (BlockPos rel : RELATIVE_POSITIONS) {
            BlockPos target = rotateRelative(pos, rel, facing);
            if (!level.getBlockState(target).canBeReplaced()) {
                return false;
            }
        }
        return true;
    }

    private static BlockPos rotateRelative(BlockPos origin, BlockPos rel, Direction facing) {
        int x = rel.getX();
        int y = rel.getY();
        int z = rel.getZ();

        int rx;
        int rz;

        switch (facing) {
            case SOUTH -> { rx = x;  rz = z; }
            case NORTH -> { rx = -x; rz = -z; }
            case WEST  -> { rx = z;  rz = -x; }
            case EAST  -> { rx = -z; rz = x; }
            default    -> { rx = x;  rz = z; }
        }

        return origin.offset(rx, y, rz);
    }

    @Nullable
    private BlockPos findRoot(Level level, BlockPos from) {
        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos p = from.offset(dx, dy, dz);
                    BlockState st = level.getBlockState(p);
                    if (st.getBlock() == this && st.getValue(PART) == RoulettePart.ROOT) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public enum RoulettePart implements StringRepresentable {
        ROOT("root"),
        PART("part");

        private final String name;

        RoulettePart(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}