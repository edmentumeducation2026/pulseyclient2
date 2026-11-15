package com.example.utilclient.mixin;

import com.example.utilclient.feature.Freecam;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** While Freecam is on, the real player stands still. */
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void utilclient$freeze(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (!Freecam.active()) return;
        Input in = (Input) (Object) this;
        in.movementForward = 0;
        in.movementSideways = 0;
        in.pressingForward = false;
        in.pressingBack = false;
        in.pressingLeft = false;
        in.pressingRight = false;
        in.jumping = false;
        in.sneaking = false;
    }
}
