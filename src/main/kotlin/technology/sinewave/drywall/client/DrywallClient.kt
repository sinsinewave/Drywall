package technology.sinewave.drywall.client

import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import technology.sinewave.drywall.client.block.frame.FrameBlockEntityRenderer
import technology.sinewave.drywall.common.Drywall
import technology.sinewave.drywall.common.Drywall.LOGGER
import technology.sinewave.drywall.common.block.Blocks
import technology.sinewave.drywall.common.panel.Panels
import technology.sinewave.drywall.common.util.modLoc

@EventBusSubscriber(modid = Drywall.ID, value = [Dist.CLIENT])
object DrywallClient {
    init {
        LOGGER.info("Initialising client...")
    }

    @SubscribeEvent
    fun registerAdditionalModels(event: ModelEvent.RegisterAdditional) {
        LOGGER.info("Loading panel models...")
        Panels.REGISTRY.entries.forEach { it ->
            event.register(ModelResourceLocation.standalone(modLoc("block/panel/${it.id.path}")))
        }
    }

    @SubscribeEvent
    fun registerBlockEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(Blocks.FRAME_BE.get()) { ctx -> FrameBlockEntityRenderer() }
    }
}