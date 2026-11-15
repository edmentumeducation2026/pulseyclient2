package com.example.utilclient.mixin;

import com.example.utilclient.feature.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void utilclient$freecam(BlockView area, Entity focused, boolean thirdPerson, boolean inverse,
                                    float tickDelta, CallbackInfo ci) {
        if (!Freecam.active()) return;
        Vec3d p = Freecam.pos(tickDelta);
        setPos(p.x, p.y, p.z);
        setRotation(Freecam.yaw(), Freecam.pitch());
    }

    /** Makes the real player body render while the camera is detached. */
    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
    private void utilclient$third(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.active()) cir.setReturnValue(true);
    }
}
