package com.zerokg2004.paintball.item;

import com.zerokg2004.paintball.block.entity.C4BlockEntity;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RemoteItem extends Item {
    private static final Map<String, Map<UUID, BlockPos>> REMOTE_LINKS = new HashMap<>();
    private final DyeColor color;

    private static final float VISUAL_EXPLOSION_STRENGTH = 1.5F;
    private static final float C4_DAMAGE = 15.0F;
    private static final double DAMAGE_RADIUS = 4.0D;

    public RemoteItem(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    private void sendTeamMessage(Player player, String message, DyeColor color) {
        ChatFormatting formatting = switch (color) {
            case RED -> ChatFormatting.RED;
            case GREEN -> ChatFormatting.GREEN;
            case BLUE -> ChatFormatting.BLUE;
            case YELLOW -> ChatFormatting.YELLOW;
            case PURPLE -> ChatFormatting.LIGHT_PURPLE;
            case BLACK -> ChatFormatting.DARK_GRAY;
            case WHITE -> ChatFormatting.WHITE;
            case ORANGE -> ChatFormatting.GOLD;
            case CYAN -> ChatFormatting.AQUA;
            case GRAY -> ChatFormatting.GRAY;
            case LIGHT_BLUE -> ChatFormatting.BLUE;
            case LIME -> ChatFormatting.GREEN;
            case PINK -> ChatFormatting.LIGHT_PURPLE;
            case BROWN -> ChatFormatting.DARK_RED;
            case LIGHT_GRAY -> ChatFormatting.GRAY;
            case MAGENTA -> ChatFormatting.DARK_PURPLE;
        };
        player.sendSystemMessage(Component.literal(message).withStyle(formatting));
    }

    private String getKey(Player player) {
        return player.getUUID() + "_" + color.getName();
    }

    private boolean isWearingColorArmor(Player player, DyeColor armorColor) {
        int count = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof PaintballArmorItem paintballArmor) {
                if (paintballArmor.isArmorPieceOfColor(armor, armorColor)) {
                    count++;
                }
            }
        }
        return count >= 4;
    }

    public boolean isFullyWearingTeamColor(Player player) {
        return isWearingColorArmor(player, this.color);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide || player == null) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof C4BlockEntity c4)) {
            return InteractionResult.PASS;
        }

        if (!isFullyWearingTeamColor(player)) {
            sendTeamMessage(player, "You aren't on the " + this.color.getName() + " team.", this.color);
            return InteractionResult.FAIL;
        }

        if (c4.getColor() != this.color || (c4.isLinked() && !c4.isLinkedTo(player))) {
            sendTeamMessage(player, "This C4 belongs to a different team or player.", this.color);
            return InteractionResult.FAIL;
        }

        UUID c4Id = c4.getC4Id();
        if (c4Id == null) return InteractionResult.FAIL;

        String key = getKey(player);
        Map<UUID, BlockPos> linkedC4 = REMOTE_LINKS.computeIfAbsent(key, k -> new HashMap<>());

        if (linkedC4.containsKey(c4Id)) {
            linkedC4.remove(c4Id);
            c4.unlink();
            sendTeamMessage(player, "The C4 is no longer connected to the remote.", this.color);
        } else {
            if (!c4.tryLinkTo(player)) {
                return InteractionResult.FAIL;
            }
            linkedC4.put(c4Id, pos);
            sendTeamMessage(player, "The C4 is now connected to the remote.", this.color);
        }

        return InteractionResult.CONSUME_PARTIAL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.pass(itemInHand);
        }

        var hitResult = player.pick(5.0D, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = BlockPos.containing(hitResult.getLocation());
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof C4BlockEntity) {
                return InteractionResultHolder.pass(itemInHand);
            }
        }

        if (!isFullyWearingTeamColor(player)) {
            sendTeamMessage(player, "You aren't on the " + this.color.getName() + " team.", this.color);
            return InteractionResultHolder.consume(itemInHand);
        }

        tryRemoteDetonation(player);

        return InteractionResultHolder.consume(itemInHand);
    }

    public void tryRemoteDetonation(Player player) {
        if (player.level().isClientSide) return;

        if (!isFullyWearingTeamColor(player)) {
            sendTeamMessage(player, "You aren't on the " + this.color.getName() + " team.", this.color);
            return;
        }

        String key = getKey(player);
        Map<UUID, BlockPos> linkedC4 = REMOTE_LINKS.get(key);

        if (linkedC4 == null || linkedC4.isEmpty()) {
            sendTeamMessage(player, "The remote does not have any C4 connected to it.", this.color);
            return;
        }

        handleDetonation(player);
    }

    private void handleDetonation(Player player) {
        String key = getKey(player);
        Map<UUID, BlockPos> linkedC4 = REMOTE_LINKS.get(key);
        if (linkedC4 == null || linkedC4.isEmpty()) return;

        Level level = player.level();
        boolean anyDetonated = false;
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, BlockPos> entry : linkedC4.entrySet()) {
            BlockPos pos = entry.getValue();

            if (!level.hasChunkAt(pos)) {
                toRemove.add(entry.getKey());
                continue;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof C4BlockEntity c4) || c4.getColor() != this.color) {
                toRemove.add(entry.getKey());
                continue;
            }

            if (!c4.isLinkedTo(player) || !entry.getKey().equals(c4.getC4Id())) {
                continue;
            }

            DyeColor explosionColor = c4.getColor();

            c4.unlink();
            level.removeBlock(pos, false);
            level.explode(
                    null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    VISUAL_EXPLOSION_STRENGTH,
                    false,
                    Level.ExplosionInteraction.NONE
            );

            List<Entity> nearbyEntities = level.getEntities(null, new AABB(pos).inflate(DAMAGE_RADIUS));
            for (Entity entity : nearbyEntities) {
                if (entity instanceof Player targetPlayer && isWearingColorArmor(targetPlayer, explosionColor)) {
                    continue;
                }
                entity.hurt(level.damageSources().explosion(null), C4_DAMAGE);
            }

            toRemove.add(entry.getKey());
            anyDetonated = true;
        }

        toRemove.forEach(linkedC4::remove);
        if (linkedC4.isEmpty()) {
            REMOTE_LINKS.remove(key);
        }

        if (anyDetonated) {
            sendTeamMessage(player, "The connected C4 has been detonated.", this.color);
        }
    }
}