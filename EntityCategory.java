package com.example.utilclient.feature;

import com.example.utilclient.module.Modules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public enum EntityCategory {
    PLAYER(0xFF5555), HOSTILE(0xFFAA00), PASSIVE(0x55FF55), VILLAGER(0x55FFFF), OTHER(0xFFFFFF);

    public final int color;
    EntityCategory(int color) { this.color = color; }

    public boolean enabled() {
        return switch (this) {
            case PLAYER -> Modules.ESP_PLAYERS.value;
            case HOSTILE -> Modules.ESP_HOSTILE.value;
            case PASSIVE -> Modules.ESP_PASSIVE.value;
            case VILLAGER -> Modules.ESP_VILLAGERS.value;
            case OTHER -> Modules.ESP_OTHER.value;
        };
    }

    public static EntityCategory of(Entity e) {
        if (e instanceof PlayerEntity) return PLAYER;
        if (e instanceof Monster) return HOSTILE;
        if (e instanceof MerchantEntity) return VILLAGER;
        if (e instanceof PassiveEntity || e instanceof WaterCreatureEntity) return PASSIVE;
        if (e instanceof LivingEntity) return OTHER;
        return null;
    }
}
