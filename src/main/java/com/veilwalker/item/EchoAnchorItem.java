package com.veilwalker.item;

import com.veilwalker.ModComponents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

/**
 * Sneak + right-click: bind the anchor to where you stand (any dimension).
 * Right-click: blink back to the anchor, even across dimensions. Costs durability.
 */
public class EchoAnchorItem extends Item {
    public EchoAnchorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }

        if (user.isSneaking()) {
            GlobalPos here = GlobalPos.create(world.getRegistryKey(), user.getBlockPos());
            stack.set(ModComponents.ANCHOR, here);
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.PLAYERS, 1.0f, 1.4f);
            user.sendMessage(Text.translatable("item.veilwalker.echo_anchor.bound").formatted(Formatting.LIGHT_PURPLE), true);
            return TypedActionResult.success(stack);
        }

        GlobalPos anchor = stack.get(ModComponents.ANCHOR);
        if (anchor == null) {
            user.sendMessage(Text.translatable("item.veilwalker.echo_anchor.unbound").formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }

        ServerWorld destination = ((ServerWorld) world).getServer().getWorld(anchor.dimension());
        if (destination == null) {
            user.sendMessage(Text.translatable("item.veilwalker.echo_anchor.lost").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        ServerWorld origin = (ServerWorld) world;
        origin.spawnParticles(ParticleTypes.REVERSE_PORTAL, user.getX(), user.getY() + 1, user.getZ(), 40, 0.3, 0.6, 0.3, 0.1);

        BlockPos p = anchor.pos();
        user.teleport(destination, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, Set.of(), user.getYaw(), user.getPitch());

        destination.spawnParticles(ParticleTypes.PORTAL, p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5, 60, 0.4, 0.8, 0.4, 0.3);
        destination.playSound(null, p.getX() + 0.5, p.getY(), p.getZ() + 0.5,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 0.8f);

        user.getItemCooldownManager().set(this, 60);
        stack.damage(1, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return TypedActionResult.success(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.contains(ModComponents.ANCHOR);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        GlobalPos anchor = stack.get(ModComponents.ANCHOR);
        if (anchor == null) {
            tooltip.add(Text.translatable("item.veilwalker.echo_anchor.tooltip.unbound").formatted(Formatting.GRAY));
        } else {
            BlockPos p = anchor.pos();
            tooltip.add(Text.literal(p.getX() + ", " + p.getY() + ", " + p.getZ()).formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.literal(anchor.dimension().getValue().toString()).formatted(Formatting.DARK_GRAY));
        }
        tooltip.add(Text.translatable("item.veilwalker.echo_anchor.tooltip.help").formatted(Formatting.DARK_AQUA));
    }
}
