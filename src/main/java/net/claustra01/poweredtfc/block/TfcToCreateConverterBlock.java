package net.claustra01.poweredtfc.block;

import com.simibubi.create.foundation.block.IBE;
import net.claustra01.poweredtfc.PoweredTFCBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TfcToCreateConverterBlock extends AbstractMechanicalConverterBlock
        implements IBE<TfcToCreateConverterBlockEntity> {
    public TfcToCreateConverterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<TfcToCreateConverterBlockEntity> getBlockEntityClass() {
        return TfcToCreateConverterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TfcToCreateConverterBlockEntity> getBlockEntityType() {
        return PoweredTFCBlocks.TFC_TO_CREATE_CONVERTER_BLOCK_ENTITY.get();
    }
}
