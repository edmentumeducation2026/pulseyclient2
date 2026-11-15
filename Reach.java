package com.example.utilclient.feature;

import com.example.utilclient.module.Modules;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public final class Reach {
    private static boolean applied;

    public static void tick(ClientPlayerEntity p) {
        EntityAttributeInstance e = p.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
        EntityAttributeInstance b = p.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
        if (e == null || b == null) return;
        if (Modules.REACH.enabled) {
            e.setBaseValue(Modules.REACH_ENTITY.value);
            b.setBaseValue(Modules.REACH_BLOCK.value);
            applied = true;
        } else if (applied) {
            e.setBaseValue(3.0);
            b.setBaseValue(4.5);
            applied = false;
        }
    }

    public static void adjust(double delta) {
        Modules.REACH_ENTITY.set(Modules.REACH_ENTITY.value + delta);
        Modules.REACH_BLOCK.set(Modules.REACH_BLOCK.value + delta);
    }
}
