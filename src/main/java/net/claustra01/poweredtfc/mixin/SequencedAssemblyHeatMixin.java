package net.claustra01.poweredtfc.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.HeatComponent;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.component.heat.IHeatView;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SequencedAssemblyRecipe.class, remap = false)
public abstract class SequencedAssemblyHeatMixin {
    @Inject(method = "advance", at = @At("RETURN"))
    private void poweredtfc$carryHeatThroughAssembly(ResourceLocation recipeId, ItemStack input,
            RandomSource random, CallbackInfoReturnable<ItemStack> cir) {
        final IHeatView inputHeat = HeatCapability.view(input);
        final ItemStack output = cir.getReturnValue();
        if (inputHeat == null || output.isEmpty()) {
            return;
        }

        final float temperature = inputHeat.getTemperature();
        if (output.has(AllDataComponents.SEQUENCED_ASSEMBLY)) {
            output.set(TFCComponents.HEAT, HeatComponent.of(inputHeat.getHeatCapacity(), temperature));
            return;
        }

        final IHeat outputHeat = HeatCapability.get(output);
        if (outputHeat != null) {
            outputHeat.setTemperature(temperature);
        }
    }
}
