package com.veilwalker.item;

import com.veilwalker.ModComponents;
import com.veilwalker.VeilFx;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

/**
 * Sneak + right-click: bind the anchor to where you stand (any dimension).
 * Right-click: blink back to the anchor, even across dimensions. Costs durability.
 * While held and bound, motes of veil light orbit you.
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
        ServerWorld origin = (ServerWorld) world;

        if (user.isSneaking()) {
            stack.set(ModComponents.ANCHOR, GlobalPos.create(world.getRegistryKey(), user.getBlockPos()));
            VeilFx.bindEffect(origin, user.getPos());
            user.sendMessage(Text.translatable("item.veilwalker.echo_anchor.bound").formatted(Formatting.LIGHT_PURPLE), true);
            return TypedActionResult.success(stack);
        }

        GlobalPos anchor = stack.get(ModComponents.ANCHOR);
        if (anchor == null) {
            user.sendMessage(Text.translatable("item.veilwalker.echo_anchor.unbound").formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }

        ServerWorld destination = origin.getServer().getWorld(anchor.dimension());
        if (destination == null) {
            user.sendMessage(Text.translatable("item.veilwalker.echo_anchor.lost").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        VeilFx.vanishEffect(origin, user.getPos());

        BlockPos p = anchor.pos();
        user.teleport(destination, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, Set.of(), user.getYaw(), user.getPitch());

        VeilFx.arriveEffect(destination, new Vec3d(p.getX() + 0.5, p.getY(), p.getZ() + 0.5));

        user.getItemCooldownManager().set(this, 60);
        stack.damage(1, user, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return TypedActionResult.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world instanceof ServerWorld sw && entity instanceof PlayerEntity player
                && (selected || player.getOffHandStack() == stack)
                && stack.contains(ModComponents.ANCHOR)
                && world.getTime() % 3 == 0) {
            VeilFx.orbit(sw, player, world.getTime(), 0.9, 2);
        }
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
