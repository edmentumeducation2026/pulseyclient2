package com.example.utilclient.mixin;

import com.example.utilclient.feature.EntityCategory;
import com.example.utilclient.feature.Freecam;
import com.example.utilclient.module.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    private EntityCategory utilclient$cat() {
        Entity self = (Entity) (Object) this;
        if (!Modules.ESP.enabled || !self.getWorld().isClient) return null;
        if (self == MinecraftClient.getInstance().player) return null;
        EntityCategory c = EntityCategory.of(self);
        return (c != null && c.enabled()) ? c : null;
    }

    /** ESP: vanilla glow outline (visible through walls), colored per category. */
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void utilclient$glow(CallbackInfoReturnable<Boolean> cir) {
        if (utilclient$cat() != null) cir.setReturnValue(true);
    }

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void utilclient$color(CallbackInfoReturnable<Integer> cir) {
        EntityCategory c = utilclient$cat();
        if (c != null) cir.setReturnValue(c.color);
    }

    /** Freecam: mouse movement rotates the camera instead of the real player. */
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void utilclient$look(double dx, double dy, CallbackInfo ci) {
        if (Freecam.active() && (Object) this == MinecraftClient.getInstance().player) {
            Freecam.look(dx, dy);
            ci.cancel();
        }
    }
}
