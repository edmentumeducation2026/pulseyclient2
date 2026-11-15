package com.example.utilclient.module;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<>();

    public Module(String name, boolean enabled) { this.name = name; this.enabled = enabled; }

    public BoolSetting bool(String n, boolean def) { BoolSetting s = new BoolSetting(n, def); settings.add(s); return s; }
    public NumSetting num(String n, double def, double min, double max, double step) {
        NumSetting s = new NumSetting(n, def, min, max, step); settings.add(s); return s;
    }

    public static abstract class Setting {
        public final String name;
        Setting(String name) { this.name = name; }
    }

    public static class BoolSetting extends Setting {
        public boolean value;
        BoolSetting(String n, boolean v) { super(n); value = v; }
    }

    public static class NumSetting extends Setting {
        public double value; public final double min, max, step;
        NumSetting(String n, double v, double min, double max, double step) { super(n); this.min = min; this.max = max; this.step = step; set(v); }
        public void set(double v) {
            v = Math.round(v / step) * step;
            value = Math.max(min, Math.min(max, v));
        }
    }
}
