package com.example.utilclient.feature;

import com.example.utilclient.module.Modules;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public final class InfoRenderer {
    private static final int MAX_LABELS = 40;

    public static void render(WorldRenderContext ctx) {
        if (!Modules.INFO.enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        MatrixStack ms = ctx.matrixStack();
        VertexConsumerProvider vc = ctx.consumers();
        if (ms == null || vc == null) return;

        Camera cam = ctx.camera();
        Vec3d camPos = cam.getPos();
        float td = mc.getRenderTickCounter().getTickDelta(false);
        double range = Modules.INFO_RANGE.value;
        TextRenderer tr = mc.textRenderer;

        List<LivingEntity> list = new ArrayList<>();
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player || !(e instanceof LivingEntity le) || !le.isAlive()) continue;
            EntityCategory c = EntityCategory.of(e);
            if (c == null || !c.enabled() || mc.player.distanceTo(e) > range) continue;
            list.add(le);
        }
        list.sort(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)));
        if (list.size() > MAX_LABELS) list = list.subList(0, MAX_LABELS);

        List<Vec3d> placed = new ArrayList<>();
        for (LivingEntity e : list) {
            double dist = mc.player.distanceTo(e);   // exact distance from the local player
            Vec3d anchor = e.getLerpedPos(td).add(0, e.getHeight() + 0.5, 0);

            // Distance-compensated scale: grows with distance (capped) so text stays readable.
            float s = 0.025f * (float) Modules.INFO_SCALE.value
                    * (float) MathHelper.clamp(dist / 6.0, 1.0, 5.0);

            // Overlap prevention: stack labels upward when a nearer label is already close by.
            int stack = 0;
            for (Vec3d p : placed) if (p.squaredDistanceTo(anchor) < 2.5 * 2.5) stack++;
            placed.add(anchor);
            double lift = stack * 11.0 * s;

            StringBuilder sb = new StringBuilder(e.getDisplayName().getString());
            if (Modules.INFO_HEALTH.value)
                sb.append("  ").append(String.format("%.1f/%.1f HP", e.getHealth(), e.getMaxHealth()));
            if (Modules.INFO_DIST.value)
                sb.append("  ").append(String.format("%.1fm", dist));
            String text = sb.toString();
            int color = EntityCategory.of(e).color | 0xFF000000;

            ms.push();
            ms.translate(anchor.x - camPos.x, anchor.y - camPos.y + lift, anchor.z - camPos.z);
            ms.multiply(cam.getRotation());
            ms.scale(s, -s, s);
            tr.draw(text, -tr.getWidth(text) / 2f, 0, color, false, ms.peek().getPositionMatrix(), vc,
                    TextRenderer.TextLayerType.SEE_THROUGH, 0x66000000, 0xF000F0);
            ms.pop();
        }
    }
}
