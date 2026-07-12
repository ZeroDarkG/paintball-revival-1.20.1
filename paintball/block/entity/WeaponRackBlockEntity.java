package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.block.WeaponRackBlock;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeaponRackBlockEntity extends BlockEntity {

    private static final int RECHARGE_STEP_TICKS = 20 * 10;
    public static final int MAX_STAGE = 5;

    private int chargeStage = 0;
    private int rechargeTicks = 0;

    public WeaponRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WEAPON_RACK_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean tryGiveWeapons(Player player) {
        if (level == null || level.isClientSide) return false;
        if (player == null) return false;

        boolean isCreative = player.getAbilities().instabuild;
        DyeColor rackColor = getRackColor();

        // 1. BLOQUEO DE EQUIPO: Ahora se aplica siempre (Survival y Creativo)
        // Esto verifica que el jugador lleve el set completo del color del Rack
        if (!canUseWeaponRack(player, rackColor)) {
            return false;
        }

        // 2. BLOQUEO DE RECARGA: Solo se aplica si NO es creativo
        if (!isCreative && this.chargeStage != 0) {
            return false;
        }

        // 3. Chequeo de inventario (mantenido para ambos modos)
        if (!canReceiveAnything(player, rackColor)) {
            player.sendSystemMessage(Component.literal("Your inventory is full."));
            return false;
        }

        // 4. Entrega de armas
        giveAllWeapons(player, rackColor);
        playPop(player);

        // 5. Iniciar recarga: Solo si NO es creativo
        if (!isCreative) {
            chargeStage = MAX_STAGE;
            rechargeTicks = 0;
            setChanged();
        }

        return true;
    }

    private boolean canReceiveAnything(Player player, DyeColor c) {
        var inv = player.getInventory();

        for (ItemStack stack : getWeaponBundleStacks(c)) {
            if (canFit(inv, stack)) return true;
        }

        Item gravityGun = resolveGravityGunItem(c);
        if (gravityGun != null && gravityGun != Items.AIR) {
            if (canFit(inv, new ItemStack(gravityGun, 1))) return true;
        }

        return false;
    }

    private boolean canFit(net.minecraft.world.entity.player.Inventory inv, ItemStack stack) {
        if (stack.isEmpty()) return true;
        if (inv.getFreeSlot() != -1) return true;
        return inv.getSlotWithRemainingSpace(stack) != -1;
    }

    private void playPop(Player player) {
        if (level == null) return;
        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS,
                0.25F,
                0.9F + (level.random.nextFloat() * 0.2F)
        );
    }

    private DyeColor getRackColor() {
        BlockState st = getBlockState();
        if (st.getBlock() instanceof WeaponRackBlock b) return b.getColor();
        return DyeColor.BLUE;
    }

    private boolean canUseWeaponRack(Player player, DyeColor rackColor) {
        // Comprobamos si lleva las 4 piezas del color exacto
        if (!isFullyWearingTeamColor(player, rackColor)) {
            sendTeamMessage(player, "You aren't on the " + rackColor.getName() + " team.", rackColor);
            return false;
        }
        return true;
    }

    private boolean isFullyWearingTeamColor(Player player, DyeColor teamColor) {
        int count = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (!armor.isEmpty() && armor.getItem() instanceof PaintballArmorItem paintballArmor) {
                // Usamos el método de tu clase PaintballArmorItem para validar el color
                if (paintballArmor.isArmorPieceOfColor(armor, teamColor)) {
                    count++;
                }
            }
        }
        // Solo permitimos el uso si tiene el set completo (casco, peto, pantalones y botas)
        return count >= 4;
    }

    private void sendTeamMessage(Player player, String message, DyeColor color) {
        ChatFormatting formatting = switch (color) {
            case RED -> ChatFormatting.RED;
            case GREEN -> ChatFormatting.GREEN;
            case BLUE -> ChatFormatting.BLUE;
            case YELLOW -> ChatFormatting.YELLOW;
            case PURPLE -> ChatFormatting.LIGHT_PURPLE;
            case ORANGE -> ChatFormatting.GOLD;
            default -> ChatFormatting.WHITE;
        };

        player.sendSystemMessage(Component.literal(message).withStyle(formatting));
    }

    private void giveAllWeapons(Player player, DyeColor c) {
        int explosiveCount = 2;

        giveOrDrop(player, new ItemStack(getRemote(c), 1));
        giveOrDrop(player, new ItemStack(getPistol(c), 1));
        giveOrDrop(player, new ItemStack(getShotgun(c), 1));
        giveOrDrop(player, new ItemStack(getRifle(c), 1));
        giveOrDrop(player, new ItemStack(getSniper(c), 1));
        giveOrDrop(player, new ItemStack(getLauncher(c), 1));

        giveOrDrop(player, new ItemStack(getGrenade(c), explosiveCount));
        giveGravityGunIfLoaded(player, c);
        giveGravityBookIfLoaded(player);
        giveOrDrop(player, new ItemStack(getClaymore(c), explosiveCount));
        giveOrDrop(player, new ItemStack(getC4(c), explosiveCount));
    }

    private void giveOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private void giveGravityGunIfLoaded(Player player, DyeColor c) {
        Item gravityGun = resolveGravityGunItem(c);
        if (gravityGun == null || gravityGun == Items.AIR) return;
        giveOrDrop(player, new ItemStack(gravityGun, 1));
    }

    private void giveGravityBookIfLoaded(Player player) {
        if (!ModList.get().isLoaded("gravitygun")) return;

        ResourceLocation bookId = new ResourceLocation("gravitygun", "gravity_book");
        Item bookItem = ForgeRegistries.ITEMS.getValue(bookId);

        if (bookItem != null && bookItem != Items.AIR) {
            giveOrDrop(player, new ItemStack(bookItem, 1));
        }
    }

    @Nullable
    private Item resolveGravityGunItem(DyeColor c) {
        if (!ModList.get().isLoaded("gravitygun")) return null;
        ResourceLocation id = new ResourceLocation("gravitygun", colorToGravityGunId(c));
        return ForgeRegistries.ITEMS.getValue(id);
    }

    private String colorToGravityGunId(DyeColor c) {
        return switch (c) {
            case RED -> "red_gravity_gun";
            case BLUE -> "blue_gravity_gun";
            case GREEN -> "green_gravity_gun";
            case YELLOW -> "yellow_gravity_gun";
            case ORANGE -> "orange_gravity_gun";
            case PURPLE -> "purple_gravity_gun";
            default -> "red_gravity_gun";
        };
    }

    private List<ItemStack> getWeaponBundleStacks(DyeColor c) {
        int explosiveCount = 2;
        List<ItemStack> list = new ArrayList<>(10);
        list.add(new ItemStack(getRemote(c), 1));
        list.add(new ItemStack(getPistol(c), 1));
        list.add(new ItemStack(getShotgun(c), 1));
        list.add(new ItemStack(getRifle(c), 1));
        list.add(new ItemStack(getSniper(c), 1));
        list.add(new ItemStack(getLauncher(c), 1));
        list.add(new ItemStack(getGrenade(c), explosiveCount));
        list.add(new ItemStack(getClaymore(c), explosiveCount));
        list.add(new ItemStack(getC4(c), explosiveCount));
        return list;
    }

    private static Item getRemote(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_REMOTE.get();
            case BLUE -> ModItems.BLUE_REMOTE.get();
            case GREEN -> ModItems.GREEN_REMOTE.get();
            case YELLOW -> ModItems.YELLOW_REMOTE.get();
            case ORANGE -> ModItems.ORANGE_REMOTE.get();
            case PURPLE -> ModItems.PURPLE_REMOTE.get();
            default -> ModItems.RED_REMOTE.get();
        };
    }

    private static Item getPistol(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_PISTOL.get();
            case BLUE -> ModItems.BLUE_PISTOL.get();
            case GREEN -> ModItems.GREEN_PISTOL.get();
            case YELLOW -> ModItems.YELLOW_PISTOL.get();
            case ORANGE -> ModItems.ORANGE_PISTOL.get();
            case PURPLE -> ModItems.PURPLE_PISTOL.get();
            default -> ModItems.RED_PISTOL.get();
        };
    }

    private static Item getShotgun(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_SHOTGUN.get();
            case BLUE -> ModItems.BLUE_SHOTGUN.get();
            case GREEN -> ModItems.GREEN_SHOTGUN.get();
            case YELLOW -> ModItems.YELLOW_SHOTGUN.get();
            case ORANGE -> ModItems.ORANGE_SHOTGUN.get();
            case PURPLE -> ModItems.PURPLE_SHOTGUN.get();
            default -> ModItems.RED_SHOTGUN.get();
        };
    }

    private static Item getRifle(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_RIFLE.get();
            case BLUE -> ModItems.BLUE_RIFLE.get();
            case GREEN -> ModItems.GREEN_RIFLE.get();
            case YELLOW -> ModItems.YELLOW_RIFLE.get();
            case ORANGE -> ModItems.ORANGE_RIFLE.get();
            case PURPLE -> ModItems.PURPLE_RIFLE.get();
            default -> ModItems.RED_RIFLE.get();
        };
    }

    private static Item getSniper(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_SNIPER.get();
            case BLUE -> ModItems.BLUE_SNIPER.get();
            case GREEN -> ModItems.GREEN_SNIPER.get();
            case YELLOW -> ModItems.YELLOW_SNIPER.get();
            case ORANGE -> ModItems.ORANGE_SNIPER.get();
            case PURPLE -> ModItems.PURPLE_SNIPER.get();
            default -> ModItems.RED_SNIPER.get();
        };
    }

    private static Item getLauncher(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_LAUNCHER.get();
            case BLUE -> ModItems.BLUE_LAUNCHER.get();
            case GREEN -> ModItems.GREEN_LAUNCHER.get();
            case YELLOW -> ModItems.YELLOW_LAUNCHER.get();
            case ORANGE -> ModItems.ORANGE_LAUNCHER.get();
            case PURPLE -> ModItems.PURPLE_LAUNCHER.get();
            default -> ModItems.RED_LAUNCHER.get();
        };
    }

    private static Item getGrenade(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_GRENADE.get();
            case BLUE -> ModItems.BLUE_GRENADE.get();
            case GREEN -> ModItems.GREEN_GRENADE.get();
            case YELLOW -> ModItems.YELLOW_GRENADE.get();
            case ORANGE -> ModItems.ORANGE_GRENADE.get();
            case PURPLE -> ModItems.PURPLE_GRENADE.get();
            default -> ModItems.RED_GRENADE.get();
        };
    }

    private static Item getClaymore(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_CLAYMORE.get();
            case BLUE -> ModItems.BLUE_CLAYMORE.get();
            case GREEN -> ModItems.GREEN_CLAYMORE.get();
            case YELLOW -> ModItems.YELLOW_CLAYMORE.get();
            case ORANGE -> ModItems.ORANGE_CLAYMORE.get();
            case PURPLE -> ModItems.PURPLE_CLAYMORE.get();
            default -> ModItems.RED_CLAYMORE.get();
        };
    }

    private static Item getC4(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_C4.get();
            case BLUE -> ModItems.BLUE_C4.get();
            case GREEN -> ModItems.GREEN_C4.get();
            case YELLOW -> ModItems.YELLOW_C4.get();
            case ORANGE -> ModItems.ORANGE_C4.get();
            case PURPLE -> ModItems.PURPLE_C4.get();
            default -> ModItems.RED_C4.get();
        };
    }

    public int getTextureIndex() {
        int stage = Math.max(0, Math.min(MAX_STAGE, chargeStage));
        return stage + 1;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WeaponRackBlockEntity be) {
        if (level.isClientSide) return;

        if (be.chargeStage > 0) {
            be.rechargeTicks++;

            if (be.rechargeTicks >= RECHARGE_STEP_TICKS) {
                be.rechargeTicks = 0;
                be.chargeStage--;
                be.setChanged();
            }
        } else if (be.rechargeTicks != 0) {
            be.rechargeTicks = 0;
            be.setChanged();
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ChargeStage", this.chargeStage);
        tag.putInt("RechargeTicks", this.rechargeTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.chargeStage = tag.getInt("ChargeStage");
        this.rechargeTicks = tag.getInt("RechargeTicks");
    }

    public CompoundTag saveWeaponRackData() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}