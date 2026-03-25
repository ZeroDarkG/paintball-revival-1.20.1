package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.item.PaintbrushItem;
import com.zerokg2004.paintball.registry.ModBlocks;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3f;

public class InstaBaseBlock extends Block {

    private final DyeColor color;

    public InstaBaseBlock(Properties props, DyeColor color) {
        super(props);
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);

        if (!held.isEmpty() && held.getItem() instanceof PaintbrushItem brush) {
            DyeColor target = brush.getColor();
            if (target != this.color) {
                Block replacement = instaBaseBlockForColor(target);
                if (replacement != null) {
                    if (!level.isClientSide) {
                        ServerLevel server = (ServerLevel) level;

                        server.setBlock(pos, replacement.defaultBlockState(), 3);

                        server.playSound(
                                null,
                                pos,
                                SoundEventsRegistry.PAINTBRUSH.get(),
                                SoundSource.PLAYERS,
                                0.8F,
                                1.0F + (server.random.nextFloat() - server.random.nextFloat()) * 0.2F
                        );

                        spawnPaintParticles(server, pos, target);
                    }

                    player.swing(hand, true);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;
        ServerLevel server = (ServerLevel) level;

        if (!hasEnoughSpace(server, pos)) {
            player.sendSystemMessage(Component.literal("There isn't enough space for the Insta-Base to be created."));
            return InteractionResult.CONSUME;
        }

        server.removeBlock(pos, false);
        build(server, pos, color);

        return InteractionResult.CONSUME;
    }

    private static void spawnPaintParticles(ServerLevel server, BlockPos pos, DyeColor dye) {
        float[] c = dye.getTextureDiffuseColors();
        Vector3f v = new Vector3f(c[0], c[1], c[2]);
        DustParticleOptions dust = new DustParticleOptions(v, 1.0F);

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.8D;
        double z = pos.getZ() + 0.5D;

        server.sendParticles(dust, x, y, z, 18, 0.35D, 0.25D, 0.35D, 0.02D);
    }

    private static boolean hasEnoughSpace(ServerLevel level, BlockPos origin) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        for (int i = x - 4; i <= x + 4; i++) {
            for (int j = y; j <= y + 8; j++) {
                for (int k = z - 4; k <= z + 4; k++) {
                    BlockPos p = new BlockPos(i, j, k);

                    if (p.equals(origin)) continue;

                    BlockState bs = level.getBlockState(p);

                    if (bs.is(Blocks.BEDROCK)) return false;

                    if (!bs.isAir() && bs.isSolidRender(level, p)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static void build(ServerLevel level, BlockPos origin, DyeColor color) {
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        BlockState wool = woolState(color);
        BlockState glass = stainedGlassState(color);
        BlockState scanner = scannerState(color);

        BlockState stairs = Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(StairBlock.FACING, Direction.NORTH);

        set(level, x,     y - 1, z,     wool);
        set(level, x,     y - 1, z + 1, wool);
        set(level, x,     y - 1, z + 2, wool);
        set(level, x,     y - 1, z + 3, wool);
        set(level, x,     y - 1, z - 1, wool);
        set(level, x,     y - 1, z - 2, wool);
        set(level, x,     y - 1, z - 3, wool);

        set(level, x + 1, y - 1, z,     wool);
        set(level, x + 1, y - 1, z + 1, wool);
        set(level, x + 1, y - 1, z + 2, wool);
        set(level, x + 1, y - 1, z + 3, wool);
        set(level, x + 1, y - 1, z + 4, wool);
        set(level, x + 1, y - 1, z - 1, wool);
        set(level, x + 1, y - 1, z - 2, wool);
        set(level, x + 1, y - 1, z - 3, wool);

        set(level, x + 2, y - 1, z,     wool);
        set(level, x + 2, y - 1, z + 1, wool);
        set(level, x + 2, y - 1, z + 2, wool);
        set(level, x + 2, y - 1, z + 3, wool);
        set(level, x + 2, y - 1, z - 1, wool);
        set(level, x + 2, y - 1, z - 2, wool);
        set(level, x + 2, y - 1, z - 3, wool);

        set(level, x + 3, y - 1, z,     wool);
        set(level, x + 3, y - 1, z + 1, wool);
        set(level, x + 3, y - 1, z + 2, wool);
        set(level, x + 3, y - 1, z - 1, wool);
        set(level, x + 3, y - 1, z - 2, wool);

        set(level, x + 4, y - 1, z - 1, wool);

        set(level, x - 1, y - 1, z,     wool);
        set(level, x - 1, y - 1, z + 1, wool);
        set(level, x - 1, y - 1, z + 2, wool);
        set(level, x - 1, y - 1, z + 3, wool);
        set(level, x - 1, y - 1, z - 1, wool);
        set(level, x - 1, y - 1, z - 2, wool);
        set(level, x - 1, y - 1, z - 3, wool);
        set(level, x - 1, y - 1, z - 4, wool);

        set(level, x - 2, y - 1, z,     wool);
        set(level, x - 2, y - 1, z + 1, wool);
        set(level, x - 2, y - 1, z + 2, wool);
        set(level, x - 2, y - 1, z + 3, wool);
        set(level, x - 2, y - 1, z - 1, wool);
        set(level, x - 2, y - 1, z - 2, wool);
        set(level, x - 2, y - 1, z - 3, wool);

        set(level, x - 3, y - 1, z,     wool);
        set(level, x - 3, y - 1, z + 1, wool);
        set(level, x - 3, y - 1, z + 2, wool);
        set(level, x - 3, y - 1, z - 1, wool);
        set(level, x - 3, y - 1, z - 2, wool);

        set(level, x - 4, y - 1, z + 1, wool);

        set(level, x,     y, z + 3, wool);
        set(level, x,     y, z - 3, wool);

        set(level, x + 1, y, z + 2, scanner);
        set(level, x + 1, y, z + 4, scanner);
        set(level, x + 1, y, z - 3, wool);

        set(level, x + 2, y, z + 1, stairs);
        set(level, x + 2, y, z - 1, scanner);
        set(level, x + 2, y, z + 3, wool);
        set(level, x + 2, y, z - 3, wool);

        set(level, x + 3, y, z,     wool);
        set(level, x + 3, y, z + 1, wool);
        set(level, x + 3, y, z + 2, wool);
        set(level, x + 3, y, z - 2, wool);

        set(level, x + 4, y, z - 1, scanner);

        set(level, x - 1, y, z + 3, wool);
        set(level, x - 1, y, z - 2, scanner);
        set(level, x - 1, y, z - 4, scanner);

        set(level, x - 2, y, z + 1, scanner);
        set(level, x - 2, y, z + 3, wool);
        set(level, x - 2, y, z - 3, wool);

        set(level, x - 3, y, z,     wool);
        set(level, x - 3, y, z + 2, wool);
        set(level, x - 3, y, z - 1, wool);
        set(level, x - 3, y, z - 2, wool);

        set(level, x - 4, y, z + 1, scanner);

        set(level, x,     y + 1, z + 3, wool);
        set(level, x,     y + 1, z - 3, wool);

        set(level, x + 1, y + 1, z - 3, wool);

        set(level, x + 2, y + 1, z,     stairs);
        set(level, x + 2, y + 1, z + 3, wool);
        set(level, x + 2, y + 1, z - 3, wool);

        set(level, x + 3, y + 1, z,     wool);
        set(level, x + 3, y + 1, z + 1, wool);
        set(level, x + 3, y + 1, z + 2, wool);
        set(level, x + 3, y + 1, z - 2, wool);

        set(level, x - 1, y + 1, z + 3, wool);
        set(level, x - 2, y + 1, z + 3, wool);
        set(level, x - 2, y + 1, z - 3, wool);

        set(level, x - 3, y + 1, z,     wool);
        set(level, x - 3, y + 1, z + 2, wool);
        set(level, x - 3, y + 1, z - 1, wool);
        set(level, x - 3, y + 1, z - 2, wool);

        set(level, x,     y + 2, z + 3, wool);
        set(level, x,     y + 2, z - 3, wool);

        set(level, x + 1, y + 2, z + 3, wool);
        set(level, x + 1, y + 2, z - 3, wool);

        set(level, x + 2, y + 2, z + 3, wool);
        set(level, x + 2, y + 2, z - 1, stairs);
        set(level, x + 2, y + 2, z - 3, wool);

        set(level, x + 3, y + 2, z,     wool);
        set(level, x + 3, y + 2, z + 1, wool);
        set(level, x + 3, y + 2, z + 2, wool);
        set(level, x + 3, y + 2, z - 1, wool);
        set(level, x + 3, y + 2, z - 2, wool);

        set(level, x - 1, y + 2, z + 3, wool);
        set(level, x - 1, y + 2, z - 3, wool);

        set(level, x - 2, y + 2, z + 3, wool);
        set(level, x - 2, y + 2, z - 3, wool);

        set(level, x - 3, y + 2, z,     wool);
        set(level, x - 3, y + 2, z + 1, wool);
        set(level, x - 3, y + 2, z + 2, wool);
        set(level, x - 3, y + 2, z - 1, wool);
        set(level, x - 3, y + 2, z - 2, wool);

        set(level, x,     y + 3, z,     glass);
        set(level, x,     y + 3, z + 1, glass);
        set(level, x,     y + 3, z + 2, glass);
        set(level, x,     y + 3, z + 3, wool);
        set(level, x,     y + 3, z - 1, glass);
        set(level, x,     y + 3, z - 2, glass);
        set(level, x,     y + 3, z - 3, wool);

        set(level, x + 1, y + 3, z,     wool);
        set(level, x + 1, y + 3, z + 1, wool);
        set(level, x + 1, y + 3, z + 2, wool);
        set(level, x + 1, y + 3, z + 3, wool);
        set(level, x + 1, y + 3, z - 1, wool);
        set(level, x + 1, y + 3, z - 2, wool);
        set(level, x + 1, y + 3, z - 3, wool);

        set(level, x + 2, y + 3, z + 3, wool);
        set(level, x + 2, y + 3, z - 2, stairs);
        set(level, x + 2, y + 3, z - 3, wool);

        set(level, x + 3, y + 3, z,     wool);
        set(level, x + 3, y + 3, z + 1, wool);
        set(level, x + 3, y + 3, z + 2, wool);
        set(level, x + 3, y + 3, z - 1, wool);
        set(level, x + 3, y + 3, z - 2, wool);

        set(level, x - 1, y + 3, z,     glass);
        set(level, x - 1, y + 3, z + 1, glass);
        set(level, x - 1, y + 3, z + 2, glass);
        set(level, x - 1, y + 3, z + 3, wool);
        set(level, x - 1, y + 3, z - 1, glass);
        set(level, x - 1, y + 3, z - 2, glass);
        set(level, x - 1, y + 3, z - 3, wool);

        set(level, x - 2, y + 3, z,     glass);
        set(level, x - 2, y + 3, z + 1, glass);
        set(level, x - 2, y + 3, z + 2, wool);
        set(level, x - 2, y + 3, z + 3, wool);
        set(level, x - 2, y + 3, z - 1, glass);
        set(level, x - 2, y + 3, z - 2, wool);
        set(level, x - 2, y + 3, z - 3, wool);

        set(level, x - 3, y + 3, z,     wool);
        set(level, x - 3, y + 3, z + 1, wool);
        set(level, x - 3, y + 3, z + 2, wool);
        set(level, x - 3, y + 3, z - 1, wool);
        set(level, x - 3, y + 3, z - 2, wool);

        set(level, x,     y + 4, z + 4, wool);
        set(level, x,     y + 4, z - 4, wool);
        set(level, x + 1, y + 4, z + 4, wool);
        set(level, x + 1, y + 4, z - 4, wool);
        set(level, x + 2, y + 4, z + 4, wool);
        set(level, x + 2, y + 4, z - 4, wool);
        set(level, x + 3, y + 4, z + 3, wool);
        set(level, x + 3, y + 4, z - 3, wool);
        set(level, x + 4, y + 4, z,     wool);
        set(level, x + 4, y + 4, z + 1, wool);
        set(level, x + 4, y + 4, z + 2, wool);
        set(level, x + 4, y + 4, z - 1, wool);
        set(level, x + 4, y + 4, z - 2, wool);

        set(level, x - 1, y + 4, z + 4, wool);
        set(level, x - 1, y + 4, z - 4, wool);
        set(level, x - 2, y + 4, z + 4, wool);
        set(level, x - 2, y + 4, z - 4, wool);
        set(level, x - 3, y + 4, z + 3, wool);
        set(level, x - 3, y + 4, z - 3, wool);
        set(level, x - 4, y + 4, z,     wool);
        set(level, x - 4, y + 4, z + 1, wool);
        set(level, x - 4, y + 4, z + 2, wool);
        set(level, x - 4, y + 4, z - 1, wool);
        set(level, x - 4, y + 4, z - 2, wool);

        set(level, x + 1, y + 5, z + 4, wool);
        set(level, x + 1, y + 5, z - 4, wool);
        set(level, x + 3, y + 5, z + 3, wool);
        set(level, x + 3, y + 5, z - 3, wool);
        set(level, x + 4, y + 5, z + 1, wool);
        set(level, x + 4, y + 5, z - 1, wool);

        set(level, x - 1, y + 5, z + 4, wool);
        set(level, x - 1, y + 5, z - 4, wool);
        set(level, x - 3, y + 5, z + 3, wool);
        set(level, x - 3, y + 5, z - 3, wool);
        set(level, x - 4, y + 5, z + 1, wool);
        set(level, x - 4, y + 5, z - 1, wool);

        placeIronDoor(level, new BlockPos(x + 1, y, z + 3), Direction.NORTH, DoorHingeSide.RIGHT);
        placeIronDoor(level, new BlockPos(x + 3, y, z - 1), Direction.WEST, DoorHingeSide.LEFT);
        placeIronDoor(level, new BlockPos(x - 1, y, z - 3), Direction.SOUTH, DoorHingeSide.RIGHT);
        placeIronDoor(level, new BlockPos(x - 3, y, z + 1), Direction.EAST, DoorHingeSide.LEFT);

        BlockPos chestPos = new BlockPos(x + 2, y, z);
        placeChestAndFill(level, chestPos, color);
    }

    private static void set(ServerLevel level, int x, int y, int z, BlockState state) {
        level.setBlock(new BlockPos(x, y, z), state, 3);
    }

    private static void placeIronDoor(ServerLevel level, BlockPos lowerPos, Direction facing, DoorHingeSide hinge) {
        BlockState lower = Blocks.IRON_DOOR.defaultBlockState()
                .setValue(DoorBlock.FACING, facing)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, hinge)
                .setValue(DoorBlock.OPEN, false)
                .setValue(DoorBlock.POWERED, false);

        BlockState upper = lower.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);

        level.setBlock(lowerPos, lower, 3);
        level.setBlock(lowerPos.above(), upper, 3);
    }

    private static BlockState scannerState(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_SCANNER.get().defaultBlockState();
            case BLUE -> ModBlocks.BLUE_SCANNER.get().defaultBlockState();
            case GREEN -> ModBlocks.GREEN_SCANNER.get().defaultBlockState();
            case YELLOW -> ModBlocks.YELLOW_SCANNER.get().defaultBlockState();
            case ORANGE -> ModBlocks.ORANGE_SCANNER.get().defaultBlockState();
            case PURPLE -> ModBlocks.PURPLE_SCANNER.get().defaultBlockState();
            default -> ModBlocks.RED_SCANNER.get().defaultBlockState();
        };
    }

    private static BlockState woolState(DyeColor color) {
        return switch (color) {
            case RED -> Blocks.RED_WOOL.defaultBlockState();
            case BLUE -> Blocks.BLUE_WOOL.defaultBlockState();
            case GREEN -> Blocks.GREEN_WOOL.defaultBlockState();
            case YELLOW -> Blocks.YELLOW_WOOL.defaultBlockState();
            case ORANGE -> Blocks.ORANGE_WOOL.defaultBlockState();
            case PURPLE -> Blocks.PURPLE_WOOL.defaultBlockState();
            default -> Blocks.WHITE_WOOL.defaultBlockState();
        };
    }

    private static BlockState stainedGlassState(DyeColor color) {
        return switch (color) {
            case RED -> Blocks.RED_STAINED_GLASS.defaultBlockState();
            case BLUE -> Blocks.BLUE_STAINED_GLASS.defaultBlockState();
            case GREEN -> Blocks.GREEN_STAINED_GLASS.defaultBlockState();
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS.defaultBlockState();
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS.defaultBlockState();
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS.defaultBlockState();
            default -> Blocks.WHITE_STAINED_GLASS.defaultBlockState();
        };
    }

    private static void placeChestAndFill(ServerLevel level, BlockPos chestPos, DyeColor color) {
        BlockState chestState = Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.WEST)
                .setValue(ChestBlock.TYPE, ChestType.SINGLE);

        level.setBlock(chestPos, chestState, 3);

        BlockEntity be = level.getBlockEntity(chestPos);
        if (!(be instanceof ChestBlockEntity chest)) return;

        var pod = switch (color) {
            case RED -> ModItems.RED_POD;
            case BLUE -> ModItems.BLUE_POD;
            case GREEN -> ModItems.GREEN_POD;
            case YELLOW -> ModItems.YELLOW_POD;
            case ORANGE -> ModItems.ORANGE_POD;
            case PURPLE -> ModItems.PURPLE_POD;
            default -> ModItems.RED_POD;
        };

        var weaponRack = switch (color) {
            case RED -> ModItems.RED_WEAPON_RACK;
            case BLUE -> ModItems.BLUE_WEAPON_RACK;
            case GREEN -> ModItems.GREEN_WEAPON_RACK;
            case YELLOW -> ModItems.YELLOW_WEAPON_RACK;
            case ORANGE -> ModItems.ORANGE_WEAPON_RACK;
            case PURPLE -> ModItems.PURPLE_WEAPON_RACK;
            default -> ModItems.RED_WEAPON_RACK;
        };

        var gearRack = switch (color) {
            case RED -> ModItems.RED_GEAR_RACK;
            case BLUE -> ModItems.BLUE_GEAR_RACK;
            case GREEN -> ModItems.GREEN_GEAR_RACK;
            case YELLOW -> ModItems.YELLOW_GEAR_RACK;
            case ORANGE -> ModItems.ORANGE_GEAR_RACK;
            case PURPLE -> ModItems.PURPLE_GEAR_RACK;
            default -> ModItems.RED_GEAR_RACK;
        };

        var medkit = switch (color) {
            case RED -> ModItems.RED_MEDKIT;
            case BLUE -> ModItems.BLUE_MEDKIT;
            case GREEN -> ModItems.GREEN_MEDKIT;
            case YELLOW -> ModItems.YELLOW_MEDKIT;
            case ORANGE -> ModItems.ORANGE_MEDKIT;
            case PURPLE -> ModItems.PURPLE_MEDKIT;
            default -> ModItems.RED_MEDKIT;
        };

        var paintbrush = switch (color) {
            case RED -> ModItems.RED_PAINTBRUSH;
            case BLUE -> ModItems.BLUE_PAINTBRUSH;
            case GREEN -> ModItems.GREEN_PAINTBRUSH;
            case YELLOW -> ModItems.YELLOW_PAINTBRUSH;
            case ORANGE -> ModItems.ORANGE_PAINTBRUSH;
            case PURPLE -> ModItems.PURPLE_PAINTBRUSH;
            default -> ModItems.RED_PAINTBRUSH;
        };

        var flag = switch (color) {
            case RED -> ModItems.RED_FLAG;
            case BLUE -> ModItems.BLUE_FLAG;
            case GREEN -> ModItems.GREEN_FLAG;
            case YELLOW -> ModItems.YELLOW_FLAG;
            case ORANGE -> ModItems.ORANGE_FLAG;
            case PURPLE -> ModItems.PURPLE_FLAG;
            default -> ModItems.RED_FLAG;
        };

        chest.setItem(0, new ItemStack(pod.get()));
        chest.setItem(1, new ItemStack(weaponRack.get()));
        chest.setItem(2, new ItemStack(gearRack.get()));
        chest.setItem(3, new ItemStack(medkit.get()));
        chest.setItem(4, new ItemStack(paintbrush.get()));

        chest.setItem(13, new ItemStack(flag.get()));

        chest.setItem(22, new ItemStack(pod.get()));
        chest.setItem(23, new ItemStack(weaponRack.get()));
        chest.setItem(24, new ItemStack(gearRack.get()));
        chest.setItem(25, new ItemStack(medkit.get()));
        chest.setItem(26, new ItemStack(paintbrush.get()));

        chest.setChanged();
    }

    private static Block instaBaseBlockForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_INSTA_BASE_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_INSTA_BASE_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_INSTA_BASE_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_INSTA_BASE_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_INSTA_BASE_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_INSTA_BASE_BLOCK.get();
            default -> null;
        };
    }
}