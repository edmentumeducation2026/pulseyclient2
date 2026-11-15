package com.example.utilclient;

import com.example.utilclient.feature.*;
import com.example.utilclient.gui.ClickGui;
import com.example.utilclient.module.Modules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class UtilClient implements ClientModInitializer {
    private static final String CAT = "category.utilclient";
    private static KeyBinding guiKey, flightKey, freecamKey, reachUp, reachDown;

    private static KeyBinding key(String id, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id, InputUtil.Type.KEYSYM, code, CAT));
    }

    @Override
    public void onInitializeClient() {
        Modules.load();
        guiKey = key("key.utilclient.gui", GLFW.GLFW_KEY_RIGHT_SHIFT);
        flightKey = key("key.utilclient.flight", GLFW.GLFW_KEY_G);
        freecamKey = key("key.utilclient.freecam", GLFW.GLFW_KEY_H);
        reachUp = key("key.utilclient.reach_up", GLFW.GLFW_KEY_RIGHT_BRACKET);
        reachDown = key("key.utilclient.reach_down", GLFW.GLFW_KEY_LEFT_BRACKET);

        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        WorldRenderEvents.AFTER_ENTITIES.register(InfoRenderer::render);
        HudRenderCallback.EVENT.register(Hud::render);
    }

    private void tick(MinecraftClient mc) {
        Freecam.tick(mc);   // also resets freecam when leaving a world
        if (mc.player == null) return;
        while (guiKey.wasPressed()) if (mc.currentScreen == null) mc.setScreen(new ClickGui());
        while (flightKey.wasPressed()) { Modules.FLIGHT.enabled ^= true; Modules.save(); }
        while (freecamKey.wasPressed()) { Modules.FREECAM.enabled ^= true; }
        while (reachUp.wasPressed()) { Reach.adjust(0.5); Modules.save(); }
        while (reachDown.wasPressed()) { Reach.adjust(-0.5); Modules.save(); }
        Reach.tick(mc.player);
        Flight.tick(mc);
    }
}
