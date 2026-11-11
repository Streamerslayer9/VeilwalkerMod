package com.veilwalker;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** All of Veilwalker's visual and audio flair lives here. */
public final class VeilFx {
    private VeilFx() {}

    /** Purple -> cyan shifting dust, the signature "veil" colour. */
    public static final ParticleEffect VEIL_DUST = new DustColorTransitionParticleEffect(
            new Vector3f(0.69f, 0.40f, 0.95f), new Vector3f(0.30f, 0.90f, 0.98f), 1.0f);
    public static final ParticleEffect VEIL_DUST_SMALL = new DustColorTransitionParticleEffect(
            new Vector3f(0.69f, 0.40f, 0.95f), new Vector3f(0.30f, 0.90f, 0.98f), 0.6f);

    // ---------------------------------------------------------------- scheduler
    private record Task(long runAt, Runnable action) {}
    private static final List<Task> TASKS = new ArrayList<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(VeilFx::tick);
    }

    private static void tick(MinecraftServer server) {
        if (TASKS.isEmpty()) return;
        long now = server.getTicks();
        List<Runnable> due = new ArrayList<>();
        Iterator<Task> it = TASKS.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.runAt() <= now) {
                due.add(t.action());
                it.remove();
            }
        }
        for (Runnable r : due) {
            try {
                r.run();
            } catch (Exception ignored) {
                // effects must never crash the server
            }
        }
    }

    /** Run something a few ticks from now (used to animate effects over time). */
    public static void later(ServerWorld world, int delayTicks, Runnable action) {
        if (delayTicks <= 0) {
            action.run();
        } else {
            TASKS.add(new Task(world.getServer().getTicks() + delayTicks, action));
        }
    }

    // ---------------------------------------------------------------- primitives
    private static void sound(ServerWorld w, double x, double y, double z, SoundEvent s, float vol, float pitch) {
        w.playSound(null, x, y, z, s, SoundCategory.PLAYERS, vol, pitch);
    }

    public static void ring(ServerWorld w, double x, double y, double z, double r, int points, ParticleEffect p) {
        for (int i = 0; i < points; i++) {
            double a = Math.PI * 2 * i / points;
            w.spawnParticles(p, x + Math.cos(a) * r, y, z + Math.sin(a) * r, 1, 0, 0, 0, 0);
        }
    }

    /** Rings that sweep outward along the ground. */
    public static void expandingRings(ServerWorld w, double x, double y, double z) {
        for (int t = 0; t < 8; t++) {
            final int tt = t;
            final double r = 0.4 + t * 0.3;
            later(w, t, () -> {
                ring(w, x, y + 0.1, z, r, 12 + tt * 3, VEIL_DUST);
                if (tt % 2 == 0) ring(w, x, y + 0.15, z, r, 8, ParticleTypes.END_ROD);
            });
        }
    }

    /** Rings that collapse inward, like being pulled into the veil. */
    public static void contractingRings(ServerWorld w, double x, double y, double z) {
        for (int t = 0; t < 7; t++) {
            final int tt = t;
            final double r = 2.2 - t * 0.3;
            later(w, t, () -> ring(w, x, y + 0.1 + tt * 0.25, z, r, 18, VEIL_DUST));
        }
    }

    /** Two interleaved arms of light that climb upward. */
    public static void risingSpiral(ServerWorld w, double x, double y, double z, double height, double radius, int ticks) {
        for (int t = 0; t < ticks; t++) {
            final int tt = t;
            later(w, t, () -> {
                double frac = (double) tt / ticks;
                double a = tt * 0.75;
                for (int arm = 0; arm < 2; arm++) {
                    double ang = a + arm * Math.PI;
                    double rr = radius * (1.0 - 0.35 * frac);
                    double px = x + Math.cos(ang) * rr;
                    double py = y + height * frac;
                    double pz = z + Math.sin(ang) * rr;
                    w.spawnParticles(VEIL_DUST, px, py, pz, 1, 0, 0, 0, 0);
                    if (tt % 2 == 0) w.spawnParticles(ParticleTypes.END_ROD, px, py, pz, 0, 0, 1, 0, 0.02);
                }
            });
        }
    }

    public static void burstUp(ServerWorld w, double x, double y, double z) {
        w.spawnParticles(ParticleTypes.END_ROD, x, y + 0.5, z, 20, 0.25, 0.5, 0.25, 0.07);
        w.spawnParticles(VEIL_DUST, x, y + 1.0, z, 25, 0.35, 0.7, 0.35, 0.1);
    }

    /** A streak of light from a to b: the afterimage of a teleport. */
    public static void trail(ServerWorld w, Vec3d a, Vec3d b) {
        int steps = Math.max(2, Math.min(40, (int) (a.distanceTo(b) / 0.4)));
        for (int i = 0; i <= steps; i++) {
            double f = (double) i / steps;
            Vec3d p = a.lerp(b, f);
            w.spawnParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
            if (i % 2 == 0) w.spawnParticles(VEIL_DUST, p.x, p.y, p.z, 1, 0.08, 0.08, 0.08, 0.0);
        }
    }

    /** A small mote that circles an entity while it holds a veil item. */
    public static void orbit(ServerWorld w, Entity e, long time, double radius, int motes) {
        double base = time * 0.22;
        for (int i = 0; i < motes; i++) {
            double a = base + i * (Math.PI * 2 / motes);
            double y = e.getY() + 1.0 + Math.sin(time * 0.11 + i * 2) * 0.55;
            w.spawnParticles(VEIL_DUST_SMALL, e.getX() + Math.cos(a) * radius, y, e.getZ() + Math.sin(a) * radius, 1, 0, 0, 0, 0);
        }
        if (time % 12 == 0) {
            w.spawnParticles(ParticleTypes.END_ROD, e.getX(), e.getY() + 0.2, e.getZ(), 0, 0, 1, 0, 0.03);
        }
    }

    // ---------------------------------------------------------------- composed effects
    public static void bindEffect(ServerWorld w, Vec3d p) {
        expandingRings(w, p.x, p.y, p.z);
        risingSpiral(w, p.x, p.y, p.z, 2.3, 0.75, 16);
        sound(w, p.x, p.y, p.z, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, 1.2f, 1.3f);
        sound(w, p.x, p.y, p.z, SoundEvents.BLOCK_BEACON_POWER_SELECT, 0.9f, 1.7f);
    }

    public static void vanishEffect(ServerWorld w, Vec3d p) {
        contractingRings(w, p.x, p.y, p.z);
        w.spawnParticles(ParticleTypes.REVERSE_PORTAL, p.x, p.y + 1, p.z, 50, 0.3, 0.7, 0.3, 0.12);
        sound(w, p.x, p.y, p.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.9f, 1.7f);
        sound(w, p.x, p.y, p.z, SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, 1.0f, 0.8f);
    }

    public static void arriveEffect(ServerWorld w, Vec3d p) {
        expandingRings(w, p.x, p.y, p.z);
        burstUp(w, p.x, p.y, p.z);
        w.spawnParticles(ParticleTypes.PORTAL, p.x, p.y + 1, p.z, 50, 0.4, 0.8, 0.4, 0.35);
        sound(w, p.x, p.y, p.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.9f);
        sound(w, p.x, p.y, p.z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 1.2f, 1.2f);
    }

    public static void slashEffect(ServerWorld w, Entity target) {
        double x = target.getX(), y = target.getBodyY(0.55), z = target.getZ();
        w.spawnParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0, 0, 0, 0);
        w.spawnParticles(ParticleTypes.CRIT, x, y, z, 25, 0.3, 0.4, 0.3, 0.5);
        w.spawnParticles(VEIL_DUST, x, y, z, 25, 0.4, 0.5, 0.4, 0.15);
        sound(w, x, y, z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.3f);
        sound(w, x, y, z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, 0.8f, 1.1f);
    }

    // ---------------------------------------------------------------- ambient block sparkle (client side)
    public static void ambient(World world, BlockPos pos, Random random, ParticleEffect particle, int chance, boolean rising) {
        if (random.nextInt(chance) != 0) return;
        Direction dir = Direction.random(random);
        if (!world.getBlockState(pos.offset(dir)).isAir()) return;
        double x = pos.getX() + 0.5 + dir.getOffsetX() * 0.6 + (dir.getOffsetX() == 0 ? (random.nextDouble() - 0.5) * 0.9 : 0);
        double y = pos.getY() + 0.5 + dir.getOffsetY() * 0.6 + (dir.getOffsetY() == 0 ? (random.nextDouble() - 0.5) * 0.9 : 0);
        double z = pos.getZ() + 0.5 + dir.getOffsetZ() * 0.6 + (dir.getOffsetZ() == 0 ? (random.nextDouble() - 0.5) * 0.9 : 0);
        world.addParticle(particle, x, y, z, 0.0, rising ? 0.03 : 0.0, 0.0);
    }
}
