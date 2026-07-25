package net.claustra01.poweredtfc;

import com.simibubi.create.AllCreativeModeTabs;
import net.claustra01.poweredtfc.block.CreateToTfcConverterBlock;
import net.claustra01.poweredtfc.block.CreateToTfcConverterBlockEntity;
import net.claustra01.poweredtfc.block.TfcToCreateConverterBlock;
import net.claustra01.poweredtfc.block.TfcToCreateConverterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PoweredTFCBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PoweredTFC.MODID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PoweredTFC.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PoweredTFC.MODID);

    private static final BlockBehaviour.Properties CONVERTER_PROPERTIES = BlockBehaviour.Properties.of()
            .strength(3.5f)
            .sound(SoundType.WOOD)
            .noOcclusion();

    public static final DeferredBlock<CreateToTfcConverterBlock> CREATE_TO_TFC_CONVERTER =
            BLOCKS.registerBlock("create_to_tfc_converter", CreateToTfcConverterBlock::new, CONVERTER_PROPERTIES);
    public static final DeferredBlock<TfcToCreateConverterBlock> TFC_TO_CREATE_CONVERTER =
            BLOCKS.registerBlock("tfc_to_create_converter", TfcToCreateConverterBlock::new, CONVERTER_PROPERTIES);

    public static final DeferredItem<BlockItem> CREATE_TO_TFC_CONVERTER_ITEM =
            ITEMS.registerSimpleBlockItem(CREATE_TO_TFC_CONVERTER);
    public static final DeferredItem<BlockItem> TFC_TO_CREATE_CONVERTER_ITEM =
            ITEMS.registerSimpleBlockItem(TFC_TO_CREATE_CONVERTER);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreateToTfcConverterBlockEntity>>
            CREATE_TO_TFC_CONVERTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                    "create_to_tfc_converter",
                    () -> BlockEntityType.Builder.of(
                            CreateToTfcConverterBlockEntity::new,
                            CREATE_TO_TFC_CONVERTER.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TfcToCreateConverterBlockEntity>>
            TFC_TO_CREATE_CONVERTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
                    "tfc_to_create_converter",
                    () -> BlockEntityType.Builder.of(
                            TfcToCreateConverterBlockEntity::new,
                            TFC_TO_CREATE_CONVERTER.get()
                    ).build(null)
            );

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(PoweredTFCBlocks::addToCreativeTab);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AllCreativeModeTabs.BASE_CREATIVE_TAB.getKey())) {
            event.accept(CREATE_TO_TFC_CONVERTER_ITEM.get());
            event.accept(TFC_TO_CREATE_CONVERTER_ITEM.get());
        }
    }

    private PoweredTFCBlocks() {}
}
