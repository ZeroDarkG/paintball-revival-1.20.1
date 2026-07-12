package com.zerokg2004.paintball.item;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.*;
import com.zerokg2004.paintball.block.entity.*;
import com.zerokg2004.paintball.registry.ModBlocks;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;


public class PaintbrushItem extends Item {

    private final DyeColor color;

    public PaintbrushItem(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 0;
    }

    /**
     * OJO: para camas/shulkers NO dependemos de useOn(), porque vanilla consume primero.
     * La lógica “primer click pinta / segundo click interactúa” se resuelve en el evento RightClickBlock.
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();

        boolean painted = false;

        if (state.getBlock() instanceof C4Block c4Block && c4Block.getColor() != this.color) {
            Block replacement = getC4ForColor(this.color);
            if (replacement != null) {
                BlockState newState = replacement.defaultBlockState()
                        .setValue(C4Block.FACING, state.getValue(C4Block.FACING));
                world.setBlock(pos, newState, 3);
                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof ScannerBlock scannerBlock && scannerBlock.getColor() != this.color) {
            Block replacement = getScannerForColor(this.color);
            if (replacement != null) {
                BlockState newState = replacement.defaultBlockState()
                        .setValue(ScannerBlock.POWERED, false);
                world.setBlock(pos, newState, 3);

                if (!world.isClientSide && player != null) {
                    ((ScannerBlock) replacement).entityInside(newState, world, pos, player);
                    world.scheduleTick(pos, replacement, 1);
                }

                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof ClaymoreBlock clayBlock && clayBlock.getColor() != this.color) {
            Block replacement = getClaymoreForColor(this.color);
            if (replacement != null) {
                Direction facing = state.getValue(ClaymoreBlock.FACING);
                int rot = state.getValue(ClaymoreBlock.ROT);

                CompoundTag beTag = null;
                var beOld = world.getBlockEntity(pos);
                if (beOld instanceof ClaymoreBlockEntity clayBE) {
                    beTag = clayBE.saveWithoutMetadata();
                }

                BlockState newState = replacement.defaultBlockState()
                        .setValue(ClaymoreBlock.FACING, facing)
                        .setValue(ClaymoreBlock.ROT, rot);
                world.setBlock(pos, newState, 3);

                var beNew = world.getBlockEntity(pos);
                if (beNew instanceof ClaymoreBlockEntity clayNew && beTag != null) {
                    clayNew.load(beTag);
                    clayNew.setChanged();
                }

                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof FlagBlock flagBlock && flagBlock.getColor() != this.color) {
            Block replacement = getFlagForColor(this.color);
            if (replacement != null) {
                BlockState newState = replacement.defaultBlockState();

                if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    newState = newState.setValue(
                            BlockStateProperties.HORIZONTAL_FACING,
                            state.getValue(BlockStateProperties.HORIZONTAL_FACING)
                    );
                }

                world.setBlock(pos, newState, 3);
                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof PodBlock podBlock && podBlock.getColor() != this.color) {
            Block replacement = getPodForColor(this.color);
            if (replacement != null) {
                Direction facing = state.hasProperty(PodBlock.FACING)
                        ? state.getValue(PodBlock.FACING)
                        : context.getHorizontalDirection().getOpposite();

                CompoundTag beTag = null;
                var beOld = world.getBlockEntity(pos);
                if (beOld instanceof PodBlockEntity podBE) {
                    beTag = podBE.savePodData();
                }

                BlockState newState = replacement.defaultBlockState()
                        .setValue(PodBlock.FACING, facing);
                world.setBlock(pos, newState, 3);

                if (beTag != null) {
                    var beNew = world.getBlockEntity(pos);
                    if (beNew instanceof PodBlockEntity podNew) {
                        podNew.load(beTag);
                        podNew.setChanged();
                    }
                }

                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof MedKitBlock medkitBlock && medkitBlock.getColor() != this.color) {
            Block replacement = getMedKitForColor(this.color);
            if (replacement != null) {
                Direction facing = state.hasProperty(MedKitBlock.FACING)
                        ? state.getValue(MedKitBlock.FACING)
                        : context.getHorizontalDirection().getOpposite();

                CompoundTag beTag = null;
                var beOld = world.getBlockEntity(pos);
                if (beOld instanceof MedKitBlockEntity medBE) {
                    beTag = medBE.saveMedkitData();
                }

                BlockState newState = replacement.defaultBlockState()
                        .setValue(MedKitBlock.FACING, facing);
                world.setBlock(pos, newState, 3);

                if (beTag != null) {
                    var beNew = world.getBlockEntity(pos);
                    if (beNew instanceof MedKitBlockEntity medNew) {
                        medNew.load(beTag);
                        medNew.setChanged();
                    }
                }

                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof GearRackBlock gearBlock && gearBlock.getColor() != this.color) {
            Block replacement = getGearRackForColor(this.color);
            if (replacement != null) {
                Direction facing = state.hasProperty(GearRackBlock.FACING)
                        ? state.getValue(GearRackBlock.FACING)
                        : context.getHorizontalDirection().getOpposite();

                CompoundTag beTag = null;
                var beOld = world.getBlockEntity(pos);
                if (beOld instanceof GearRackBlockEntity rackBE) {
                    beTag = rackBE.saveWithFullMetadata();
                }

                BlockState newState = replacement.defaultBlockState()
                        .setValue(GearRackBlock.FACING, facing);
                world.setBlock(pos, newState, 3);

                if (beTag != null) {
                    var beNew = world.getBlockEntity(pos);
                    if (beNew instanceof GearRackBlockEntity rackNew) {
                        rackNew.load(beTag);
                        rackNew.setChanged();
                    }
                }

                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof WeaponRackBlock weaponBlock && weaponBlock.getColor() != this.color) {
            Block replacement = getWeaponRackForColor(this.color);
            if (replacement != null) {
                Direction facing = state.hasProperty(WeaponRackBlock.FACING)
                        ? state.getValue(WeaponRackBlock.FACING)
                        : context.getHorizontalDirection().getOpposite();

                CompoundTag beTag = null;
                var beOld = world.getBlockEntity(pos);
                if (beOld instanceof WeaponRackBlockEntity rackBE) {
                    beTag = rackBE.saveWeaponRackData();
                }

                BlockState newState = replacement.defaultBlockState()
                        .setValue(WeaponRackBlock.FACING, facing);
                world.setBlock(pos, newState, 3);

                if (beTag != null) {
                    var beNew = world.getBlockEntity(pos);
                    if (beNew instanceof WeaponRackBlockEntity rackNew) {
                        rackNew.load(beTag);
                        rackNew.setChanged();
                    }
                }

                painted = true;
            }
        }

        if (!painted && state.getBlock() instanceof InstaBaseBlock instaBlock && instaBlock.getColor() != this.color) {
            Block replacement = getInstaBaseForColor(this.color);
            if (replacement != null) {
                world.setBlock(pos, replacement.defaultBlockState(), 3);
                painted = true;
            }
        }

        if (!painted) {
            for (DyeColor dye : DyeColor.values()) {

                if (state.is(getBannerBlock(dye)) && dye != this.color) {
                    BlockState newState = getBannerBlock(this.color).defaultBlockState()
                            .setValue(BannerBlock.ROTATION, state.getValue(BannerBlock.ROTATION));
                    world.setBlock(pos, newState, 3);
                    painted = true;
                    break;
                }

                if (state.is(getWallBannerBlock(dye)) && dye != this.color) {
                    BlockState newState = getWallBannerBlock(this.color).defaultBlockState()
                            .setValue(WallBannerBlock.FACING, state.getValue(WallBannerBlock.FACING));
                    world.setBlock(pos, newState, 3);
                    painted = true;
                    break;
                }

                if (state.is(getCandleBlock(dye)) && dye != this.color) {
                    BlockState newState = getCandleBlock(this.color).defaultBlockState()
                            .setValue(CandleBlock.CANDLES, state.getValue(CandleBlock.CANDLES))
                            .setValue(BlockStateProperties.LIT, state.getValue(BlockStateProperties.LIT))
                            .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
                    world.setBlock(pos, newState, 3);
                    painted = true;
                    break;
                }

                if (state.is(getGlazedTerracottaBlock(dye)) && dye != this.color) {
                    BlockState newState = getGlazedTerracottaBlock(this.color).defaultBlockState()
                            .setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
                    world.setBlock(pos, newState, 3);
                    painted = true;
                    break;
                }

                if (state.is(getWoolBlock(dye)) && dye != this.color) {
                    world.setBlock(pos, getWoolBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }

                if (state.is(getCarpetBlock(dye)) && dye != this.color) {
                    world.setBlock(pos, getCarpetBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }

                if (state.is(Blocks.GLASS)) {
                    world.setBlock(pos, getStainedGlassBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }

                if (state.is(getStainedGlassBlock(dye)) && dye != this.color) {
                    world.setBlock(pos, getStainedGlassBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }

                if (state.is(Blocks.GLASS_PANE)) {
                    world.setBlock(
                            pos,
                            getStainedGlassPaneBlock(this.color).defaultBlockState()
                                    .setValue(IronBarsBlock.NORTH, false)
                                    .setValue(IronBarsBlock.SOUTH, false)
                                    .setValue(IronBarsBlock.EAST, false)
                                    .setValue(IronBarsBlock.WEST, false)
                                    .setValue(BlockStateProperties.WATERLOGGED, false),
                            3
                    );
                    painted = true;
                    break;
                }

                if (state.is(getStainedGlassPaneBlock(dye)) && dye != this.color) {
                    world.setBlock(
                            pos,
                            getStainedGlassPaneBlock(this.color).defaultBlockState()
                                    .setValue(IronBarsBlock.NORTH, state.getValue(IronBarsBlock.NORTH))
                                    .setValue(IronBarsBlock.SOUTH, state.getValue(IronBarsBlock.SOUTH))
                                    .setValue(IronBarsBlock.EAST, state.getValue(IronBarsBlock.EAST))
                                    .setValue(IronBarsBlock.WEST, state.getValue(IronBarsBlock.WEST))
                                    .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED)),
                            3
                    );
                    painted = true;
                    break;
                }

                if (state.is(getTerracottaBlock(dye)) && dye != this.color) {
                    world.setBlock(pos, getTerracottaBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }

                if (state.is(getConcreteBlock(dye)) && dye != this.color) {
                    world.setBlock(pos, getConcreteBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }

                if (state.is(getConcretePowderBlock(dye)) && dye != this.color) {
                    world.setBlock(pos, getConcretePowderBlock(this.color).defaultBlockState(), 3);
                    painted = true;
                    break;
                }
            }

            if (!painted && state.is(Blocks.CANDLE)) {
                BlockState newState = getCandleBlock(this.color).defaultBlockState()
                        .setValue(CandleBlock.CANDLES, state.getValue(CandleBlock.CANDLES))
                        .setValue(BlockStateProperties.LIT, state.getValue(BlockStateProperties.LIT))
                        .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));
                world.setBlock(pos, newState, 3);
                painted = true;
            }
        }

        if (painted) {
            if (player != null) player.swing(context.getHand(), true);

            if (!world.isClientSide) {
                world.playSound(
                        null,
                        pos,
                        SoundEventsRegistry.PAINTBRUSH.get(),
                        SoundSource.PLAYERS,
                        0.8F,
                        1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F
                );
                if (world instanceof ServerLevel sl) {
                    spawnColorParticlesServer(sl, pos, this.color);
                }
            } else {
                spawnColorParticlesClient(world, pos, this.color);
            }

            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Mod.EventBusSubscriber(modid = PaintballMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class BrushInteractHooks {

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            ItemStack stack = event.getItemStack();
            if (!(stack.getItem() instanceof PaintbrushItem brush)) return;

            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof BedBlock bed) {
                DyeColor bedColor = bed.getColor();
                if (bedColor != brush.color) {
                    BlockPos otherPos = brush.getOtherBedPos(pos, state);

                    if (!level.isClientSide) {
                        boolean ok = brush.paintBed(level, pos, state, brush.color);
                        if (ok) {
                            playBrushSoundServer((ServerLevel) level, pos);

                            spawnColorParticlesServer((ServerLevel) level, pos, brush.color);
                            if (otherPos != null) {
                                spawnColorParticlesServer((ServerLevel) level, otherPos, brush.color);
                            }
                        }
                    } else {
                        spawnColorParticlesClient(level, pos, brush.color);
                        if (otherPos != null) {
                            spawnColorParticlesClient(level, otherPos, brush.color);
                        }
                    }

                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }
                return;
            }

            if (state.getBlock() instanceof ShulkerBoxBlock box) {
                DyeColor current = box.getColor();
                boolean needsPaint = (current == null || current != brush.color);

                if (needsPaint) {
                    if (!level.isClientSide) {
                        boolean ok = brush.paintShulker(level, pos, state, brush.color);
                        if (ok) {
                            playBrushSoundServer((ServerLevel) level, pos);
                            spawnColorParticlesServer((ServerLevel) level, pos, brush.color);
                        }
                    } else {
                        spawnColorParticlesClient(level, pos, brush.color);
                    }

                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }

        private static void playBrushSoundServer(ServerLevel level, BlockPos pos) {
            level.playSound(
                    null,
                    pos,
                    SoundEventsRegistry.PAINTBRUSH.get(),
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F
            );
        }
    }

    private boolean paintBed(Level world, BlockPos pos, BlockState state, DyeColor targetColor) {
        if (!(state.getBlock() instanceof BedBlock clickedBed)) return false;

        if (clickedBed.getColor() == targetColor) return false;

        Direction facing = state.getValue(BedBlock.FACING);
        BedPart part = state.getValue(BedBlock.PART);
        boolean occupied = state.getValue(BedBlock.OCCUPIED);

        BlockPos otherPos = (part == BedPart.FOOT) ? pos.relative(facing) : pos.relative(facing.getOpposite());
        BlockState otherState = world.getBlockState(otherPos);

        if (!(otherState.getBlock() instanceof BedBlock otherBed)) return false;
        if (otherBed.getColor() != clickedBed.getColor()) return false;
        if (otherState.getValue(BedBlock.FACING) != facing) return false;
        if (otherState.getValue(BedBlock.PART) == part) return false;

        Block newBedBlock = getBedBlock(targetColor);
        if (!(newBedBlock instanceof BedBlock)) return false;

        BlockState newThis = newBedBlock.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, part)
                .setValue(BedBlock.OCCUPIED, occupied);

        BlockState newOther = newBedBlock.defaultBlockState()
                .setValue(BedBlock.FACING, facing)
                .setValue(BedBlock.PART, (part == BedPart.FOOT) ? BedPart.HEAD : BedPart.FOOT)
                .setValue(BedBlock.OCCUPIED, occupied);

        final int FLAGS_NO_NEIGHBORS_NO_DROPS =
                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS;

        world.setBlock(pos, newThis, FLAGS_NO_NEIGHBORS_NO_DROPS);
        world.setBlock(otherPos, newOther, FLAGS_NO_NEIGHBORS_NO_DROPS);

        world.blockUpdated(pos, newBedBlock);
        world.blockUpdated(otherPos, newBedBlock);
        world.updateNeighborsAt(pos, newBedBlock);
        world.updateNeighborsAt(otherPos, newBedBlock);

        return true;
    }

    private BlockPos getOtherBedPos(BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BedBlock)) return null;
        Direction facing = state.getValue(BedBlock.FACING);
        BedPart part = state.getValue(BedBlock.PART);
        return (part == BedPart.FOOT) ? pos.relative(facing) : pos.relative(facing.getOpposite());
    }

    private boolean paintShulker(Level world, BlockPos pos, BlockState state, DyeColor targetColor) {
        if (!(state.getBlock() instanceof ShulkerBoxBlock)) return false;

        Direction facing = state.hasProperty(BlockStateProperties.FACING)
                ? state.getValue(BlockStateProperties.FACING)
                : Direction.UP;

        CompoundTag beTag = null;
        BlockEntity beOld = world.getBlockEntity(pos);
        if (beOld != null) {
            beTag = beOld.saveWithoutMetadata();
        }

        Block newBlock = getShulkerBoxBlock(targetColor);
        BlockState newState = newBlock.defaultBlockState();

        if (newState.hasProperty(BlockStateProperties.FACING)) {
            newState = newState.setValue(BlockStateProperties.FACING, facing);
        }

        world.setBlock(pos, newState, 3);

        if (beTag != null) {
            BlockEntity beNew = world.getBlockEntity(pos);
            if (beNew instanceof ShulkerBoxBlockEntity shulkerNew) {
                shulkerNew.load(beTag);
                shulkerNew.setChanged();
            } else if (beNew != null) {
                beNew.load(beTag);
                beNew.setChanged();
            }
        }

        return true;
    }

    private static void spawnColorParticlesClient(Level world, BlockPos pos, DyeColor color) {
        float[] c = color.getTextureDiffuseColors();
        Vector3f v = new Vector3f(c[0], c[1], c[2]);

        spawnParticlesForFaceClient(world, pos, v, Direction.UP);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.relative(dir)).isAir()) {
                spawnParticlesForFaceClient(world, pos, v, dir);
            }
        }
    }

    private static void spawnParticlesForFaceClient(Level world, BlockPos pos, Vector3f colorVector, Direction face) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;

        switch (face) {
            case UP -> y = pos.getY() + 0.9D;
            case DOWN -> y = pos.getY() + 0.1D;
            case NORTH -> z = pos.getZ() + 0.1D;
            case SOUTH -> z = pos.getZ() + 0.9D;
            case WEST -> x = pos.getX() + 0.1D;
            case EAST -> x = pos.getX() + 0.9D;
        }

        for (int i = 0; i < 6; i++) {
            double ox = (world.random.nextDouble() - 0.5D) * 0.5D;
            double oy = (world.random.nextDouble() - 0.5D) * 0.5D;
            double oz = (world.random.nextDouble() - 0.5D) * 0.5D;

            double vx = face.getStepX() * 0.15D + ox * 0.05D;
            double vy = face.getStepY() * 0.15D + oy * 0.05D;
            double vz = face.getStepZ() * 0.15D + oz * 0.05D;

            world.addParticle(
                    new DustParticleOptions(colorVector, 1.0F),
                    x + ox, y + oy, z + oz,
                    vx, vy, vz
            );
        }
    }

    private static void spawnColorParticlesServer(ServerLevel world, BlockPos pos, DyeColor color) {
        float[] c = color.getTextureDiffuseColors();
        Vector3f v = new Vector3f(c[0], c[1], c[2]);
        DustParticleOptions dust = new DustParticleOptions(v, 1.0F);

        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.6D;
        double cz = pos.getZ() + 0.5D;

        for (int i = 0; i < 18; i++) {
            double ox = (world.random.nextDouble() - 0.5D) * 0.9D;
            double oy = (world.random.nextDouble() - 0.5D) * 0.6D;
            double oz = (world.random.nextDouble() - 0.5D) * 0.9D;
            world.sendParticles(dust, cx + ox, cy + oy, cz + oz, 1, 0, 0.02D, 0, 0.0D);
        }
    }

    private Block getBedBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_BED;
            case ORANGE -> Blocks.ORANGE_BED;
            case MAGENTA -> Blocks.MAGENTA_BED;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BED;
            case YELLOW -> Blocks.YELLOW_BED;
            case LIME -> Blocks.LIME_BED;
            case PINK -> Blocks.PINK_BED;
            case GRAY -> Blocks.GRAY_BED;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BED;
            case CYAN -> Blocks.CYAN_BED;
            case PURPLE -> Blocks.PURPLE_BED;
            case BLUE -> Blocks.BLUE_BED;
            case BROWN -> Blocks.BROWN_BED;
            case GREEN -> Blocks.GREEN_BED;
            case RED -> Blocks.RED_BED;
            case BLACK -> Blocks.BLACK_BED;
        };
    }

    private Block getShulkerBoxBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_SHULKER_BOX;
            case ORANGE -> Blocks.ORANGE_SHULKER_BOX;
            case MAGENTA -> Blocks.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW -> Blocks.YELLOW_SHULKER_BOX;
            case LIME -> Blocks.LIME_SHULKER_BOX;
            case PINK -> Blocks.PINK_SHULKER_BOX;
            case GRAY -> Blocks.GRAY_SHULKER_BOX;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_SHULKER_BOX;
            case CYAN -> Blocks.CYAN_SHULKER_BOX;
            case PURPLE -> Blocks.PURPLE_SHULKER_BOX;
            case BLUE -> Blocks.BLUE_SHULKER_BOX;
            case BROWN -> Blocks.BROWN_SHULKER_BOX;
            case GREEN -> Blocks.GREEN_SHULKER_BOX;
            case RED -> Blocks.RED_SHULKER_BOX;
            case BLACK -> Blocks.BLACK_SHULKER_BOX;
        };
    }

    private Block getCandleBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_CANDLE;
            case ORANGE -> Blocks.ORANGE_CANDLE;
            case MAGENTA -> Blocks.MAGENTA_CANDLE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CANDLE;
            case YELLOW -> Blocks.YELLOW_CANDLE;
            case LIME -> Blocks.LIME_CANDLE;
            case PINK -> Blocks.PINK_CANDLE;
            case GRAY -> Blocks.GRAY_CANDLE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CANDLE;
            case CYAN -> Blocks.CYAN_CANDLE;
            case PURPLE -> Blocks.PURPLE_CANDLE;
            case BLUE -> Blocks.BLUE_CANDLE;
            case BROWN -> Blocks.BROWN_CANDLE;
            case GREEN -> Blocks.GREEN_CANDLE;
            case RED -> Blocks.RED_CANDLE;
            case BLACK -> Blocks.BLACK_CANDLE;
        };
    }

    private Block getBannerBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_BANNER;
            case ORANGE -> Blocks.ORANGE_BANNER;
            case MAGENTA -> Blocks.MAGENTA_BANNER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BANNER;
            case YELLOW -> Blocks.YELLOW_BANNER;
            case LIME -> Blocks.LIME_BANNER;
            case PINK -> Blocks.PINK_BANNER;
            case GRAY -> Blocks.GRAY_BANNER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BANNER;
            case CYAN -> Blocks.CYAN_BANNER;
            case PURPLE -> Blocks.PURPLE_BANNER;
            case BLUE -> Blocks.BLUE_BANNER;
            case BROWN -> Blocks.BROWN_BANNER;
            case GREEN -> Blocks.GREEN_BANNER;
            case RED -> Blocks.RED_BANNER;
            case BLACK -> Blocks.BLACK_BANNER;
        };
    }

    private Block getWallBannerBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_WALL_BANNER;
            case ORANGE -> Blocks.ORANGE_WALL_BANNER;
            case MAGENTA -> Blocks.MAGENTA_WALL_BANNER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WALL_BANNER;
            case YELLOW -> Blocks.YELLOW_WALL_BANNER;
            case LIME -> Blocks.LIME_WALL_BANNER;
            case PINK -> Blocks.PINK_WALL_BANNER;
            case GRAY -> Blocks.GRAY_WALL_BANNER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WALL_BANNER;
            case CYAN -> Blocks.CYAN_WALL_BANNER;
            case PURPLE -> Blocks.PURPLE_WALL_BANNER;
            case BLUE -> Blocks.BLUE_WALL_BANNER;
            case BROWN -> Blocks.BROWN_WALL_BANNER;
            case GREEN -> Blocks.GREEN_WALL_BANNER;
            case RED -> Blocks.RED_WALL_BANNER;
            case BLACK -> Blocks.BLACK_WALL_BANNER;
        };
    }

    private Block getGlazedTerracottaBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_GLAZED_TERRACOTTA;
            case ORANGE -> Blocks.ORANGE_GLAZED_TERRACOTTA;
            case MAGENTA -> Blocks.MAGENTA_GLAZED_TERRACOTTA;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA;
            case YELLOW -> Blocks.YELLOW_GLAZED_TERRACOTTA;
            case LIME -> Blocks.LIME_GLAZED_TERRACOTTA;
            case PINK -> Blocks.PINK_GLAZED_TERRACOTTA;
            case GRAY -> Blocks.GRAY_GLAZED_TERRACOTTA;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA;
            case CYAN -> Blocks.CYAN_GLAZED_TERRACOTTA;
            case PURPLE -> Blocks.PURPLE_GLAZED_TERRACOTTA;
            case BLUE -> Blocks.BLUE_GLAZED_TERRACOTTA;
            case BROWN -> Blocks.BROWN_GLAZED_TERRACOTTA;
            case GREEN -> Blocks.GREEN_GLAZED_TERRACOTTA;
            case RED -> Blocks.RED_GLAZED_TERRACOTTA;
            case BLACK -> Blocks.BLACK_GLAZED_TERRACOTTA;
        };
    }

    private Block getWoolBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_WOOL;
            case ORANGE -> Blocks.ORANGE_WOOL;
            case MAGENTA -> Blocks.MAGENTA_WOOL;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL;
            case YELLOW -> Blocks.YELLOW_WOOL;
            case LIME -> Blocks.LIME_WOOL;
            case PINK -> Blocks.PINK_WOOL;
            case GRAY -> Blocks.GRAY_WOOL;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL;
            case CYAN -> Blocks.CYAN_WOOL;
            case PURPLE -> Blocks.PURPLE_WOOL;
            case BLUE -> Blocks.BLUE_WOOL;
            case BROWN -> Blocks.BROWN_WOOL;
            case GREEN -> Blocks.GREEN_WOOL;
            case RED -> Blocks.RED_WOOL;
            case BLACK -> Blocks.BLACK_WOOL;
        };
    }

    private Block getCarpetBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_CARPET;
            case ORANGE -> Blocks.ORANGE_CARPET;
            case MAGENTA -> Blocks.MAGENTA_CARPET;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CARPET;
            case YELLOW -> Blocks.YELLOW_CARPET;
            case LIME -> Blocks.LIME_CARPET;
            case PINK -> Blocks.PINK_CARPET;
            case GRAY -> Blocks.GRAY_CARPET;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CARPET;
            case CYAN -> Blocks.CYAN_CARPET;
            case PURPLE -> Blocks.PURPLE_CARPET;
            case BLUE -> Blocks.BLUE_CARPET;
            case BROWN -> Blocks.BROWN_CARPET;
            case GREEN -> Blocks.GREEN_CARPET;
            case RED -> Blocks.RED_CARPET;
            case BLACK -> Blocks.BLACK_CARPET;
        };
    }

    private Block getStainedGlassBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_STAINED_GLASS;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS;
            case LIME -> Blocks.LIME_STAINED_GLASS;
            case PINK -> Blocks.PINK_STAINED_GLASS;
            case GRAY -> Blocks.GRAY_STAINED_GLASS;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS;
            case CYAN -> Blocks.CYAN_STAINED_GLASS;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS;
            case BLUE -> Blocks.BLUE_STAINED_GLASS;
            case BROWN -> Blocks.BROWN_STAINED_GLASS;
            case GREEN -> Blocks.GREEN_STAINED_GLASS;
            case RED -> Blocks.RED_STAINED_GLASS;
            case BLACK -> Blocks.BLACK_STAINED_GLASS;
        };
    }

    private Block getStainedGlassPaneBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_STAINED_GLASS_PANE;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS_PANE;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS_PANE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS_PANE;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS_PANE;
            case LIME -> Blocks.LIME_STAINED_GLASS_PANE;
            case PINK -> Blocks.PINK_STAINED_GLASS_PANE;
            case GRAY -> Blocks.GRAY_STAINED_GLASS_PANE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS_PANE;
            case CYAN -> Blocks.CYAN_STAINED_GLASS_PANE;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS_PANE;
            case BLUE -> Blocks.BLUE_STAINED_GLASS_PANE;
            case BROWN -> Blocks.BROWN_STAINED_GLASS_PANE;
            case GREEN -> Blocks.GREEN_STAINED_GLASS_PANE;
            case RED -> Blocks.RED_STAINED_GLASS_PANE;
            case BLACK -> Blocks.BLACK_STAINED_GLASS_PANE;
        };
    }

    private Block getTerracottaBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_TERRACOTTA;
            case ORANGE -> Blocks.ORANGE_TERRACOTTA;
            case MAGENTA -> Blocks.MAGENTA_TERRACOTTA;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_TERRACOTTA;
            case YELLOW -> Blocks.YELLOW_TERRACOTTA;
            case LIME -> Blocks.LIME_TERRACOTTA;
            case PINK -> Blocks.PINK_TERRACOTTA;
            case GRAY -> Blocks.GRAY_TERRACOTTA;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_TERRACOTTA;
            case CYAN -> Blocks.CYAN_TERRACOTTA;
            case PURPLE -> Blocks.PURPLE_TERRACOTTA;
            case BLUE -> Blocks.BLUE_TERRACOTTA;
            case BROWN -> Blocks.BROWN_TERRACOTTA;
            case GREEN -> Blocks.GREEN_TERRACOTTA;
            case RED -> Blocks.RED_TERRACOTTA;
            case BLACK -> Blocks.BLACK_TERRACOTTA;
        };
    }

    private Block getConcreteBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_CONCRETE;
            case ORANGE -> Blocks.ORANGE_CONCRETE;
            case MAGENTA -> Blocks.MAGENTA_CONCRETE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE;
            case YELLOW -> Blocks.YELLOW_CONCRETE;
            case LIME -> Blocks.LIME_CONCRETE;
            case PINK -> Blocks.PINK_CONCRETE;
            case GRAY -> Blocks.GRAY_CONCRETE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE;
            case CYAN -> Blocks.CYAN_CONCRETE;
            case PURPLE -> Blocks.PURPLE_CONCRETE;
            case BLUE -> Blocks.BLUE_CONCRETE;
            case BROWN -> Blocks.BROWN_CONCRETE;
            case GREEN -> Blocks.GREEN_CONCRETE;
            case RED -> Blocks.RED_CONCRETE;
            case BLACK -> Blocks.BLACK_CONCRETE;
        };
    }

    private Block getConcretePowderBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_CONCRETE_POWDER;
            case ORANGE -> Blocks.ORANGE_CONCRETE_POWDER;
            case MAGENTA -> Blocks.MAGENTA_CONCRETE_POWDER;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE_POWDER;
            case YELLOW -> Blocks.YELLOW_CONCRETE_POWDER;
            case LIME -> Blocks.LIME_CONCRETE_POWDER;
            case PINK -> Blocks.PINK_CONCRETE_POWDER;
            case GRAY -> Blocks.GRAY_CONCRETE_POWDER;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE_POWDER;
            case CYAN -> Blocks.CYAN_CONCRETE_POWDER;
            case PURPLE -> Blocks.PURPLE_CONCRETE_POWDER;
            case BLUE -> Blocks.BLUE_CONCRETE_POWDER;
            case BROWN -> Blocks.BROWN_CONCRETE_POWDER;
            case GREEN -> Blocks.GREEN_CONCRETE_POWDER;
            case RED -> Blocks.RED_CONCRETE_POWDER;
            case BLACK -> Blocks.BLACK_CONCRETE_POWDER;
        };
    }

    private Block getC4ForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_C4_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_C4_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_C4_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_C4_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_C4_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_C4_BLOCK.get();
            default -> null;
        };
    }

    private Block getScannerForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_SCANNER.get();
            case BLUE -> ModBlocks.BLUE_SCANNER.get();
            case GREEN -> ModBlocks.GREEN_SCANNER.get();
            case YELLOW -> ModBlocks.YELLOW_SCANNER.get();
            case ORANGE -> ModBlocks.ORANGE_SCANNER.get();
            case PURPLE -> ModBlocks.PURPLE_SCANNER.get();
            default -> null;
        };
    }

    private Block getClaymoreForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_CLAYMORE_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_CLAYMORE_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_CLAYMORE_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_CLAYMORE_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_CLAYMORE_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_CLAYMORE_BLOCK.get();
            default -> null;
        };
    }

    private Block getFlagForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_FLAG_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_FLAG_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_FLAG_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_FLAG_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_FLAG_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_FLAG_BLOCK.get();
            default -> null;
        };
    }

    private Block getMedKitForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_MEDKIT_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_MEDKIT_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_MEDKIT_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_MEDKIT_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_MEDKIT_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_MEDKIT_BLOCK.get();
            default -> null;
        };
    }

    private Block getPodForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_POD_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_POD_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_POD_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_POD_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_POD_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_POD_BLOCK.get();
            default -> null;
        };
    }

    private Block getGearRackForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_GEAR_RACK_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_GEAR_RACK_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_GEAR_RACK_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_GEAR_RACK_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_GEAR_RACK_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_GEAR_RACK_BLOCK.get();
            default -> null;
        };
    }

    private Block getWeaponRackForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_WEAPON_RACK_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_WEAPON_RACK_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_WEAPON_RACK_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_WEAPON_RACK_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_WEAPON_RACK_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_WEAPON_RACK_BLOCK.get();
            default -> null;
        };
    }

    private Block getInstaBaseForColor(DyeColor color) {
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