package net.claustra01.poweredtfc.block;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.claustra01.poweredtfc.PoweredTFCBlocks;
import net.claustra01.poweredtfc.PoweredTFCConfig;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.Rotation;
import net.dries007.tfc.util.rotation.SourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

public class CreateToTfcConverterBlockEntity extends SplitShaftBlockEntity implements RotatingBlockEntity {
    private final SourceNode node;
    private boolean invalidInNetwork;

    public CreateToTfcConverterBlockEntity(BlockPos pos, BlockState state) {
        this(PoweredTFCBlocks.CREATE_TO_TFC_CONVERTER_BLOCK_ENTITY.get(), pos, state);
    }

    public CreateToTfcConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        Direction createSide = state.getValue(AbstractMechanicalConverterBlock.FACING);
        Direction tfcOutput = state.getValue(AbstractMechanicalConverterBlock.FACING).getOpposite();
        node = new SourceNode(pos, EnumSet.of(tfcOutput), createSide, 0.0f) {
            @Override
            public String toString() {
                return "PoweredTFC Create-to-TFC converter at " + pos();
            }
        };
    }

    @Override
    public void tick() {
        super.tick();

        Rotation.Tickable rotation = node.rotation();
        rotation.tick();
        float outputSpeed = getBlockState().getValue(AbstractMechanicalConverterBlock.POWERED)
                ? 0.0f
                : MechanicalPowerConversion.rpmToRadiansPerTick(getSpeed());
        Direction createSide = getBlockState().getValue(AbstractMechanicalConverterBlock.FACING);
        if (createSide == Direction.SOUTH || createSide == Direction.EAST || createSide == Direction.DOWN) {
            outputSpeed = -outputSpeed;
        }
        rotation.setSpeed(outputSpeed);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        return 1.0f;
    }

    @Override
    public float calculateStressApplied() {
        return getBlockState().getValue(AbstractMechanicalConverterBlock.POWERED)
                ? 0.0f
                : PoweredTFCConfig.CREATE_TO_TFC_STRESS_IMPACT.get().floatValue();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        node.rotation().saveToTag(tag);
        tag.putBoolean("InvalidInTfcNetwork", invalidInNetwork);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        node.rotation().loadFromTag(tag);
        invalidInNetwork = tag.getBoolean("InvalidInTfcNetwork");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    @Override
    public void invalidate() {
        performNetworkAction(NetworkAction.REMOVE);
        super.invalidate();
    }

    @Override
    public void onChunkUnloaded() {
        performNetworkAction(NetworkAction.REMOVE);
        super.onChunkUnloaded();
    }

    @Override
    public void markAsInvalidInNetwork() {
        invalidInNetwork = true;
    }

    @Override
    public boolean isInvalidInNetwork() {
        return invalidInNetwork;
    }

    @Override
    public Node getRotationNode() {
        return node;
    }
}
