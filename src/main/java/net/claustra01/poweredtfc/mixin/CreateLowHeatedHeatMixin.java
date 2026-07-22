package net.claustra01.poweredtfc.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity", remap = false)
public abstract class CreateLowHeatedHeatMixin extends SmartBlockEntity {
    private static final float LOW_HEATED_TEMPERATURE = 480.0F;

    protected CreateLowHeatedHeatMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow public abstract boolean getLitFromBlock();

    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    private void poweredtfc$provideHeatToTfc(CallbackInfo ci) {
        if (level == null || level.isClientSide() || !getLitFromBlock()) {
            return;
        }

        HeatCapability.provideHeatTo(level, worldPosition.above(), Direction.DOWN, LOW_HEATED_TEMPERATURE);
    }
}
