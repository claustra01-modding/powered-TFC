package net.claustra01.poweredtfc.mixin;

import java.util.List;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Expands Create Basin's input fluid handler to four independent 1000 mB tanks. */
@Mixin(value = BasinBlockEntity.class, remap = false)
public abstract class BasinInputTankMixin extends SmartBlockEntity {
    private static final int INPUT_TANK_COUNT = 4;
    private static final int INPUT_TANK_CAPACITY = 1000;

    @Shadow
    public SmartFluidTankBehaviour inputTank;

    protected BasinInputTankMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "addBehaviours",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;inputTank:Lcom/simibubi/create/foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            ),
            remap = false
    )
    private void poweredtfc$expandInputTank(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        BasinBlockEntity basin = (BasinBlockEntity) (Object) this;
        inputTank = new SmartFluidTankBehaviour(
                SmartFluidTankBehaviour.INPUT,
                basin,
                INPUT_TANK_COUNT,
                INPUT_TANK_CAPACITY,
                true
        ).whenFluidUpdates(basin::notifyChangeOfContents);
    }
}
