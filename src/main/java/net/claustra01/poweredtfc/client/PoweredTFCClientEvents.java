package net.claustra01.poweredtfc.client;

import net.claustra01.poweredtfc.PoweredTFC;
import net.claustra01.poweredtfc.PoweredTFCBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = PoweredTFC.MODID, value = Dist.CLIENT)
public final class PoweredTFCClientEvents {
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                PoweredTFCBlocks.CREATE_TO_TFC_CONVERTER_BLOCK_ENTITY.get(),
                MechanicalConverterRenderer::new
        );
        event.registerBlockEntityRenderer(
                PoweredTFCBlocks.TFC_TO_CREATE_CONVERTER_BLOCK_ENTITY.get(),
                MechanicalConverterRenderer::new
        );
    }

    private PoweredTFCClientEvents() {}
}
