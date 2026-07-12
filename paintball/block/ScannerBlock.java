package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.item.PaintballArmorItem;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ScannerBlock extends Block {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final VoxelShape SHAPE_UP = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    private static final VoxelShape SHAPE_DOWN = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
    private static final AABB TOUCH_AABB = new AABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

    private final DyeColor color;

    public ScannerBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.WOOL)
                .strength(0.8F, 0.0F)
                .noOcclusion()
        );
        this.color = color;
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        if (player.getMainHandItem().getItem() instanceof ShearsItem) {
            return 0.2F;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide) return;

        int power = getSignalStrength(world, pos);
        boolean powered = state.getValue(POWERED);

        if (power > 0 && !powered) {
            world.setBlock(pos, state.setValue(POWERED, true), 3);
            playOnSound(world, pos);
            world.updateNeighborsAt(pos, this);
            world.scheduleTick(pos, this, 20);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int power = getSignalStrength(world, pos);
        boolean powered = state.getValue(POWERED);

        if (power > 0) {
            world.scheduleTick(pos, this, 20);
            return;
        }

        if (powered) {
            world.setBlock(pos, state.setValue(POWERED, false), 3);
            playOffSound(world, pos);
            world.updateNeighborsAt(pos, this);
        }
    }

    protected int getSignalStrength(Level world, BlockPos pos) {
        AABB aabb = TOUCH_AABB.move(pos);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, aabb, e -> e.isAlive());

        for (LivingEntity e : entities) {
            // ignorar espectadores (solo aplica a players)
            if (e instanceof Player p && p.isSpectator()) continue;

            if (isWearingFullArmorOfColor(e, color)) {
                return 15;
            }
        }

        return 0;
    }

    private boolean isWearingFullArmorOfColor(LivingEntity entity, DyeColor expectedColor) {
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof PaintballArmorItem armorItem)) return false;
            if (armorItem.getColor() != expectedColor) return false;
        }

        return true;
    }

    protected void playOnSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEventsRegistry.SCANNER_ON.get(), SoundSource.BLOCKS, 0.3F, 0.6F);
    }

    protected void playOffSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEventsRegistry.SCANNER_OFF.get(), SoundSource.BLOCKS, 0.3F, 0.5F);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(POWERED) ? SHAPE_DOWN : SHAPE_UP;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}