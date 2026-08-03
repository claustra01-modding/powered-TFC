package net.claustra01.poweredtfc.mixin;

import com.simibubi.create.content.processing.basin.BasinRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Keeps Basin recipe validation in sync with the four input tank sections. */
@Mixin(value = BasinRecipe.class, remap = false)
public abstract class BasinRecipeFluidInputLimitMixin {
    private static final int MAX_FLUID_INPUTS = 4;

    @Inject(method = "getMaxFluidInputCount", at = @At("HEAD"), cancellable = true, remap = false)
    private void poweredtfc$allowFourFluidInputs(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(MAX_FLUID_INPUTS);
    }
}
