package com.veilwalker.item;

import com.veilwalker.VeilFx;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Right-click while looking at a creature (up to 10 blocks away) to phase through the veil
 * and appear behind it. The target is struck from behind, slowed and revealed, and you
 * gain a burst of Speed. Left-click still swings like a normal diamond-tier sword.
 */
public class PhaseBladeItem extends SwordItem {
    private static final double RANGE = 10.0;
    private static final int COOLDOWN_TICKS = 50;
    private static final double[] BEHIND_DISTANCES = {1.6, 1.2, 2.2};

    public PhaseBladeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof ServerPlayerEntity player)) {
            return TypedActionResult.pass(stack);
        }

        LivingEntity target = findTarget(serverWorld, player);
        if (target == null) {
            player.sendMessage(Text.translatable("item.veilwalker.phase_blade.no_target").formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }

        Vec3d facing = target.getRotationVec(1.0f).multiply(1, 0, 1);
        if (facing.lengthSquared() < 1.0E-4) {
            facing = player.getRotationVec(1.0f).multiply(1, 0, 1);
        }
        if (facing.lengthSquared() < 1.0E-4) {
            return TypedActionResult.fail(stack);
        }
        facing = facing.normalize();

        Vec3d destination = null;
        for (double dist : BEHIND_DISTANCES) {
            Vec3d candidate = target.getPos().subtract(facing.multiply(dist));
            Vec3d delta = candidate.subtract(player.getPos());
            if (serverWorld.isSpaceEmpty(player, player.getBoundingBox().offset(delta))) {
                destination = candidate;
                break;
            }
        }
        if (destination == null) {
            player.sendMessage(Text.translatable("item.veilwalker.phase_blade.blocked").formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }

        // Vanish, leaving an afterimage streak behind
        Vec3d from = player.getPos();
        VeilFx.vanishEffect(serverWorld, from);
        VeilFx.trail(serverWorld, from.add(0, 1, 0), destination.add(0, 1, 0));

        // Reappear behind the target, facing it
        double dx = target.getX() - destination.x;
        double dz = target.getZ() - destination.z;
        float yaw = (float) (MathHelper.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        player.teleport(serverWorld, destination.x, destination.y, destination.z, Set.of(), yaw, player.getPitch());
        VeilFx.arriveEffect(serverWorld, destination);

        // Backstab
        target.damage(serverWorld.getDamageSources().playerAttack(player), 6.0f);
        VeilFx.slashEffect(serverWorld, target);
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1));
        target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 80, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 1));

        player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
        stack.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return TypedActionResult.success(stack);
    }

    /** Normal swings leave a small shimmer of veil light on the target. */
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld() instanceof ServerWorld sw) {
            sw.spawnParticles(VeilFx.VEIL_DUST, target.getX(), target.getBodyY(0.5), target.getZ(), 12, 0.3, 0.4, 0.3, 0.1);
            sw.spawnParticles(ParticleTypes.ENCHANTED_HIT, target.getX(), target.getBodyY(0.5), target.getZ(), 8, 0.3, 0.4, 0.3, 0.2);
        }
        return super.postHit(stack, target, attacker);
    }

    /** A lone mote drifts around whoever is holding the blade. */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (selected && world instanceof ServerWorld sw && world.getTime() % 4 == 0) {
            VeilFx.orbit(sw, entity, world.getTime(), 0.7, 1);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    /** The closest living creature along the player's line of sight, or null. */
    private static LivingEntity findTarget(ServerWorld world, PlayerEntity user) {
        Vec3d eye = user.getEyePos();
        Vec3d look = user.getRotationVec(1.0f);
        Vec3d end = eye.add(look.multiply(RANGE));
        Box search = user.getBoundingBox().stretch(look.multiply(RANGE)).expand(1.0);

        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity candidate : world.getEntitiesByClass(LivingEntity.class, search,
                e -> e != user && e.isAlive() && !e.isSpectator())) {
            Box box = candidate.getBoundingBox().expand(0.3);
            Optional<Vec3d> hit = box.raycast(eye, end);
            if (hit.isPresent()) {
                double d = eye.squaredDistanceTo(hit.get());
                if (d < bestDistance) {
                    bestDistance = d;
                    best = candidate;
                }
            }
        }
        return best;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.veilwalker.phase_blade.tooltip").formatted(Formatting.DARK_AQUA));
        tooltip.add(Text.translatable("item.veilwalker.phase_blade.tooltip2").formatted(Formatting.DARK_GRAY));
    }
}
