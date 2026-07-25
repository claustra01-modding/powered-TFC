package net.claustra01.poweredtfc.block;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.claustra01.poweredtfc.PoweredTFCBlocks;
import net.claustra01.poweredtfc.PoweredTFCConfig;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TfcToCreateConverterBlockEntity extends GeneratingKineticBlockEntity {
    private float generatedSpeed;

    public TfcToCreateConverterBlockEntity(BlockPos pos, BlockState state) {
        this(PoweredTFCBlocks.TFC_TO_CREATE_CONVERTER_BLOCK_ENTITY.get(), pos, state);
    }

    public TfcToCreateConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(10);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (level == null || level.isClientSide) {
            return;
        }

        float nextSpeed = readTfcInputSpeed();
        if (Float.compare(nextSpeed, generatedSpeed) != 0) {
            generatedSpeed = nextSpeed;
            updateGeneratedRotation();
            setChanged();
        }
    }

    private float readTfcInputSpeed() {
        if (getBlockState().getValue(AbstractMechanicalConverterBlock.POWERED)) {
            return 0.0f;
        }

        Direction createSide = getBlockState().getValue(AbstractMechanicalConverterBlock.FACING);
        Direction tfcSide = createSide.getOpposite();
        BlockEntity input = level.getBlockEntity(worldPosition.relative(tfcSide));
        if (!(input instanceof RotatingBlockEntity rotating) || input instanceof CreateToTfcConverterBlockEntity) {
            return 0.0f;
        }

        Node node = rotating.getRotationNode();
        Direction fromInputToConverter = createSide;
        if (!node.connections().contains(fromInputToConverter)) {
            return 0.0f;
        }

        Rotation rotation = node.rotation(fromInputToConverter);
        if (rotation == null) {
            return 0.0f;
        }

        return -MechanicalPowerConversion.radiansPerTickToRpm(rotation.speed());
    }

    @Override
    public float getGeneratedSpeed() {
        return generatedSpeed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float capacity = generatedSpeed == 0.0f
                ? 0.0f
                : PoweredTFCConfig.TFC_TO_CREATE_STRESS_CAPACITY.get().floatValue();
        lastCapacityProvided = capacity;
        return capacity;
    }
}
