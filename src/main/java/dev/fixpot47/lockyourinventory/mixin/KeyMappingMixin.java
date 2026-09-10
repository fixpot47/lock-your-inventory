package dev.fixpot47.lockyourinventory.mixin;

import dev.fixpot47.lockyourinventory.LockedKeyConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {
    @Shadow
    private boolean isDown;

    @Shadow
    private int clickCount;

    private KeyMapping lockyourinventory$self() {
        return (KeyMapping) (Object) this;
    }

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    private void lockyourinventory$blockHeldState(CallbackInfoReturnable<Boolean> cir) {
        if (LockedKeyConfig.isLocked(lockyourinventory$self())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "consumeClick", at = @At("HEAD"), cancellable = true)
    private void lockyourinventory$blockClicks(CallbackInfoReturnable<Boolean> cir) {
        if (LockedKeyConfig.isLocked(lockyourinventory$self())) {
            this.clickCount = 0;
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void lockyourinventory$blockPressedState(boolean down, CallbackInfo ci) {
        if (down && LockedKeyConfig.isLocked(lockyourinventory$self())) {
            this.isDown = false;
            ci.cancel();
        }
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private void lockyourinventory$blockKeyboardMatch(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (LockedKeyConfig.isLocked(lockyourinventory$self())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "matchesMouse", at = @At("HEAD"), cancellable = true)
    private void lockyourinventory$blockMouseMatch(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (LockedKeyConfig.isLocked(lockyourinventory$self())) {
            cir.setReturnValue(false);
        }
    }
}
