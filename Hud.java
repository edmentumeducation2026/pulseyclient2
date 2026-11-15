package com.example.utilclient.feature;

import com.example.utilclient.module.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class Hud {
    public static void render(DrawContext c, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.player == null) return;
        int y = 4;
        if (Modules.REACH.enabled && Modules.REACH_HUD.value) {
            c.drawText(mc.textRenderer, String.format("Reach: entity %.1f | block %.1f",
                    Modules.REACH_ENTITY.value, Modules.REACH_BLOCK.value), 4, y, 0xFF55FFFF, true);
            y += 10;
        }
        if (Modules.FLIGHT.enabled) {
            c.drawText(mc.textRenderer, String.format("Flight: ON (%.2f b/t%s)", Modules.FLIGHT_SPEED.value,
                    Modules.FLIGHT_HOVER.value ? ", hover" : ""), 4, y, 0xFF55FF55, true);
            y += 10;
        }
        if (Freecam.active()) {
            c.drawText(mc.textRenderer, String.format("Freecam: ON (%.1f b/t)", Modules.FREECAM_SPEED.value),
                    4, y, 0xFFFFAA00, true);
        }
    }
}
