package com.example.utilclient.feature;

import com.example.utilclient.module.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.Vec3d;

public final class Flight {
    public static void tick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p == null || !Modules.FLIGHT.enabled || Freecam.active()
                || p.isSpectator() || p.hasVehicle() || p.isFallFlying()) return;

        GameOptions o = mc.options;
        boolean free = mc.currentScreen == null;
        int fwd = (free && o.forwardKey.isPressed() ? 1 : 0) - (free && o.backKey.isPressed() ? 1 : 0);
        int left = (free && o.leftKey.isPressed() ? 1 : 0) - (free && o.rightKey.isPressed() ? 1 : 0);
        int up = (free && o.jumpKey.isPressed() ? 1 : 0) - (free && o.sneakKey.isPressed() ? 1 : 0);

        double speed = Modules.FLIGHT_SPEED.value;
        double yaw = Math.toRadians(p.getYaw());
        double dx = -Math.sin(yaw) * fwd + Math.cos(yaw) * left;
        double dz = Math.cos(yaw) * fwd + Math.sin(yaw) * left;
        double len = Math.sqrt(dx * dx + dz * dz);
        boolean moving = len > 1e-6;
        if (moving) { dx = dx / len * speed; dz = dz / len * speed; }

        boolean hover = Modules.FLIGHT_HOVER.value;
        Vec3d v = p.getVelocity();
        double tx = moving ? dx : (hover ? 0 : v.x);
        double tz = moving ? dz : (hover ? 0 : v.z);
        double ty = up != 0 ? up * speed : (hover ? 0 : v.y);

        double k = Modules.FLIGHT_SMOOTH.value;   // 1.0 = instant, lower = smoother
        p.setVelocity(v.x + (tx - v.x) * k, v.y + (ty - v.y) * k, v.z + (tz - v.z) * k);
        p.fallDistance = 0;
    }
}
