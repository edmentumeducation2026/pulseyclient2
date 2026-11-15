package com.example.utilclient.module;

import com.example.utilclient.module.Module.*;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.*;
import java.util.List;

public final class Modules {
    // 1. Entity ESP
    public static final Module ESP = new Module("Entity ESP", false);
    public static final BoolSetting ESP_PLAYERS = ESP.bool("Players", true);
    public static final BoolSetting ESP_HOSTILE = ESP.bool("Hostile mobs", true);
    public static final BoolSetting ESP_PASSIVE = ESP.bool("Passive mobs", true);
    public static final BoolSetting ESP_VILLAGERS = ESP.bool("Villagers", true);
    public static final BoolSetting ESP_OTHER = ESP.bool("Other living", false);

    // 2. Entity Info (uses the category toggles from the ESP panel)
    public static final Module INFO = new Module("Entity Info", false);
    public static final BoolSetting INFO_HEALTH = INFO.bool("Show health", true);
    public static final BoolSetting INFO_DIST = INFO.bool("Show distance", true);
    public static final NumSetting INFO_RANGE = INFO.num("Range", 64, 8, 256, 1);
    public static final NumSetting INFO_SCALE = INFO.num("Text scale", 1.0, 0.5, 3.0, 0.1);

    // 3. Reach
    public static final Module REACH = new Module("Reach", false);
    public static final NumSetting REACH_ENTITY = REACH.num("Entity reach", 3.0, 3.0, 8.0, 0.1);
    public static final NumSetting REACH_BLOCK = REACH.num("Block reach", 4.5, 4.5, 8.0, 0.1);
    public static final BoolSetting REACH_HUD = REACH.bool("Show on HUD", true);

    // 4. Flight
    public static final Module FLIGHT = new Module("Flight", false);
    public static final NumSetting FLIGHT_SPEED = FLIGHT.num("Speed", 0.5, 0.05, 3.0, 0.05);
    public static final NumSetting FLIGHT_SMOOTH = FLIGHT.num("Smoothing", 0.3, 0.05, 1.0, 0.05);
    public static final BoolSetting FLIGHT_HOVER = FLIGHT.bool("Hover mode", true);

    // 5. Freecam (client-side only; works in singleplayer and multiplayer)
    public static final Module FREECAM = new Module("Freecam", false);
    public static final NumSetting FREECAM_SPEED = FREECAM.num("Speed", 1.0, 0.1, 5.0, 0.1);
    public static final NumSetting FREECAM_SMOOTH = FREECAM.num("Smoothing", 0.35, 0.05, 1.0, 0.05);

    public static final List<Module> ALL = List.of(ESP, INFO, REACH, FLIGHT, FREECAM);
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("utilclient.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ALL) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.enabled);
                for (Setting s : m.settings) {
                    if (s instanceof BoolSetting b) o.addProperty(s.name, b.value);
                    else if (s instanceof NumSetting n) o.addProperty(s.name, n.value);
                }
                root.add(m.name, o);
            }
            Files.writeString(PATH, GSON.toJson(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() {
        try {
            if (!Files.exists(PATH)) return;
            JsonObject root = JsonParser.parseString(Files.readString(PATH)).getAsJsonObject();
            for (Module m : ALL) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                // Freecam always starts off; everything else restores its saved state.
                if (o.has("enabled") && m != FREECAM) m.enabled = o.get("enabled").getAsBoolean();
                for (Setting s : m.settings) {
                    if (!o.has(s.name)) continue;
                    if (s instanceof BoolSetting b) b.value = o.get(s.name).getAsBoolean();
                    else if (s instanceof NumSetting n) n.set(o.get(s.name).getAsDouble());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
