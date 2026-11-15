package com.example.utilclient.gui;

import com.example.utilclient.module.Module;
import com.example.utilclient.module.Module.*;
import com.example.utilclient.module.Modules;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ClickGui extends Screen {
    private static final int W = 125, H = 15;
    private static final int HEAD = 0xFF1E1E2E, OFF = 0xFF2A2A3A, ON = 0xFF2F6FDB;
    private static final List<Panel> PANELS = new ArrayList<>();

    private Panel dragPanel; private int dragOx, dragOy;
    private NumSetting slider; private Panel sliderPanel;

    private static class Panel {
        final Module m; int x, y; boolean open = true;
        Panel(Module m, int x, int y) { this.m = m; this.x = x; this.y = y; }
    }

    public ClickGui() {
        super(Text.literal("UtilClient"));
        if (PANELS.isEmpty()) {
            int x = 8;
            for (Module m : Modules.ALL) { PANELS.add(new Panel(m, x, 20)); x += W + 6; }
        }
    }

    @Override public boolean shouldPause() { return false; }   // game keeps running while open

    @Override
    public void renderBackground(DrawContext c, int mx, int my, float d) { c.fill(0, 0, width, height, 0x55000000); }

    private void label(DrawContext c, String s, int x, int y) { c.drawText(textRenderer, s, x + 4, y + 4, 0xFFFFFFFF, false); }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        super.render(c, mx, my, delta);
        for (Panel p : PANELS) {
            c.fill(p.x, p.y, p.x + W, p.y + H, HEAD);
            label(c, p.m.name + (p.open ? "  [-]" : "  [+]"), p.x, p.y);
            if (!p.open) continue;
            int ry = p.y + H;
            c.fill(p.x, ry, p.x + W, ry + H, p.m.enabled ? ON : OFF);
            label(c, "Enabled: " + (p.m.enabled ? "ON" : "OFF"), p.x, ry);
            for (Setting s : p.m.settings) {
                ry += H;
                if (s instanceof BoolSetting b) {
                    c.fill(p.x, ry, p.x + W, ry + H, b.value ? ON : OFF);
                    label(c, s.name + ": " + (b.value ? "ON" : "OFF"), p.x, ry);
                } else if (s instanceof NumSetting n) {
                    c.fill(p.x, ry, p.x + W, ry + H, OFF);
                    int fill = (int) (W * (n.value - n.min) / (n.max - n.min));
                    c.fill(p.x, ry, p.x + fill, ry + H, ON);
                    label(c, String.format("%s: %.2f", s.name, n.value), p.x, ry);
                }
            }
        }
    }

    private boolean hit(double mx, double my, int x, int y) { return mx >= x && mx < x + W && my >= y && my < y + H; }

    private void updateSlider(double mx) {
        double t = MathHelper.clamp((mx - sliderPanel.x) / W, 0, 1);
        slider.set(slider.min + t * (slider.max - slider.min));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (int i = PANELS.size() - 1; i >= 0; i--) {
            Panel p = PANELS.get(i);
            if (hit(mx, my, p.x, p.y)) {
                if (button == 1) p.open = !p.open;
                else if (button == 0) { dragPanel = p; dragOx = (int) mx - p.x; dragOy = (int) my - p.y; }
                return true;
            }
            if (!p.open) continue;
            int ry = p.y + H;
            if (hit(mx, my, p.x, ry)) { if (button == 0) p.m.enabled = !p.m.enabled; return true; }
            for (Setting s : p.m.settings) {
                ry += H;
                if (!hit(mx, my, p.x, ry)) continue;
                if (s instanceof BoolSetting b && button == 0) b.value = !b.value;
                else if (s instanceof NumSetting n && button == 0) { slider = n; sliderPanel = p; updateSlider(mx); }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragPanel != null) { dragPanel.x = (int) mx - dragOx; dragPanel.y = (int) my - dragOy; return true; }
        if (slider != null) { updateSlider(mx); return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragPanel = null; slider = null; sliderPanel = null;
        Modules.save();
        return super.mouseReleased(mx, my, button);
    }

    @Override public void removed() { Modules.save(); }
}
