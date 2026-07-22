package net.claustra01.poweredtfc.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "com.simibubi.create.compat.jei.category.SequencedAssemblyCategory", remap = false)
public abstract class SequencedAssemblyJeiMixin {
    private static final String HEAT_TOOLTIP = "poweredtfc.jei.sequenced_assembly.retains_heat";

    @ModifyExpressionValue(
            method = "setRecipe",
            at = @At(
                    value = "INVOKE",
                    target = "Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;addTooltipCallback(Lmezz/jei/api/gui/ingredient/IRecipeSlotTooltipCallback;)Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;"
            ),
            remap = false
    )
    private IRecipeSlotBuilder poweredtfc$addRetainedHeatTooltip(IRecipeSlotBuilder outputSlot,
            IRecipeLayoutBuilder builder, SequencedAssemblyRecipe recipe, IFocusGroup focuses) {
        if (poweredtfc$transfersHeat(recipe)) {
            outputSlot.addRichTooltipCallback((slot, tooltip) ->
                    tooltip.add(Component.translatable(HEAT_TOOLTIP).withStyle(ChatFormatting.GRAY)));
        }
        return outputSlot;
    }

    private static boolean poweredtfc$transfersHeat(SequencedAssemblyRecipe recipe) {
        final ItemStack output = recipe.resultPool.getFirst().getStack();
        if (HeatCapability.getDefinition(output) == null) {
            return false;
        }
        for (ItemStack input : recipe.getIngredient().getItems()) {
            if (HeatCapability.getDefinition(input) != null) {
                return true;
            }
        }
        return false;
    }
}
