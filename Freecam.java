package com.example.utilclient.feature;

import com.example.utilclient.module.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Purely client-side camera detach: no packets are sent, the real player stays where it is. */
public final class Freecam {
    private static boolean active;
    private static Vec3d pos = Vec3d.ZERO, prev = Vec3d.ZERO, vel = Vec3d.ZERO;
    private static float yaw, pitch;

    public static boolean active() { return active; }
    public static float yaw() { return yaw; }
    public static float pitch() { return pitch; }
    public static Vec3d pos(float td) { return prev.lerp(pos, td); }

    public static void look(double dx, double dy) {
        yaw += (float) dx * 0.15f;
        pitch = MathHelper.clamp(pitch + (float) dy * 0.15f, -90f, 90f);
    }

    private static void start(MinecraftClient mc) {
        pos = prev = mc.gameRenderer.getCamera().getPos();
        vel = Vec3d.ZERO;
        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
        active = true;
    }

    public static void stop() { active = false; }

    public static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) {
            if (active) { active = false; Modules.FREECAM.enabled = false; }
            return;
        }
        if (Modules.FREECAM.enabled && !active) start(mc);
        else if (!Modules.FREECAM.enabled && active) { stop(); return; }
        if (!active) return;

        GameOptions o = mc.options;
        boolean free = mc.currentScreen == null;
        int fwd = (free && o.forwardKey.isPressed() ? 1 : 0) - (free && o.backKey.isPressed() ? 1 : 0);
        int left = (free && o.leftKey.isPressed() ? 1 : 0) - (free && o.rightKey.isPressed() ? 1 : 0);
        int up = (free && o.jumpKey.isPressed() ? 1 : 0) - (free && o.sneakKey.isPressed() ? 1 : 0);

        double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
        Vec3d look = new Vec3d(-Math.sin(yr) * Math.cos(pr), -Math.sin(pr), Math.cos(yr) * Math.cos(pr));
        Vec3d leftV = new Vec3d(Math.cos(yr), 0, Math.sin(yr));
        Vec3d dir = look.multiply(fwd).add(leftV.multiply(left)).add(0, up, 0);
        double speed = Modules.FREECAM_SPEED.value * (free && o.sprintKey.isPressed() ? 2.0 : 1.0);
        Vec3d target = dir.lengthSquared() > 1e-6 ? dir.normalize().multiply(speed) : Vec3d.ZERO;

        double k = Modules.FREECAM_SMOOTH.value;
        vel = vel.add(target.subtract(vel).multiply(k));
        prev = pos;
        pos = pos.add(vel);
    }
}
