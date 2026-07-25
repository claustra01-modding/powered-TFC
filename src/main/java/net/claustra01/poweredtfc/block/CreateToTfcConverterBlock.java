package net.claustra01.poweredtfc.block;

import com.simibubi.create.foundation.block.IBE;
import net.claustra01.poweredtfc.PoweredTFCBlocks;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreateToTfcConverterBlock extends AbstractMechanicalConverterBlock
        implements IBE<CreateToTfcConverterBlockEntity> {
    public CreateToTfcConverterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<CreateToTfcConverterBlockEntity> getBlockEntityClass() {
        return CreateToTfcConverterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CreateToTfcConverterBlockEntity> getBlockEntityType() {
        return PoweredTFCBlocks.CREATE_TO_TFC_CONVERTER_BLOCK_ENTITY.get();
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof RotatingBlockEntity rotating) {
            rotating.destroyIfInvalid(level, pos);
        }
        super.tick(state, level, pos, random);
    }
}
