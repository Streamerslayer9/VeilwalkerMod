package com.veilwalker.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Each hit has a 25% chance to "phase strike": you slip behind the target
 * and they are briefly slowed and shaken by the veil, while you gain a burst of speed.
 */
public class PhaseBladeItem extends SwordItem {
    private static final float PHASE_CHANCE = 0.25f;

    public PhaseBladeItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getWorld() instanceof ServerWorld world && attacker.getRandom().nextFloat() < PHASE_CHANCE) {
            Vec3d facing = target.getRotationVec(1.0f).multiply(1, 0, 1);
            if (facing.lengthSquared() > 1.0E-4) {
                Vec3d behind = target.getPos().subtract(facing.normalize().multiply(1.6));
                Vec3d delta = behind.subtract(attacker.getPos());

                if (world.isSpaceEmpty(attacker, attacker.getBoundingBox().offset(delta))) {
                    world.spawnParticles(ParticleTypes.REVERSE_PORTAL,
                            attacker.getX(), attacker.getY() + 1, attacker.getZ(), 25, 0.3, 0.6, 0.3, 0.1);
                    attacker.requestTeleport(behind.x, behind.y, behind.z);
                    world.spawnParticles(ParticleTypes.PORTAL,
                            behind.x, behind.y + 1, behind.z, 25, 0.3, 0.6, 0.3, 0.2);
                    world.playSound(null, behind.x, behind.y, behind.z,
                            SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.8f, 1.5f);

                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 1));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60, 0));
                    attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 60, 1));
                }
            }
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.veilwalker.phase_blade.tooltip").formatted(Formatting.DARK_AQUA));
    }
}
