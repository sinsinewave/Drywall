package technology.sinewave.drywall.client.block.frame

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import technology.sinewave.drywall.common.block.frame.FrameBlockEntity
import technology.sinewave.drywall.common.util.modLoc

@OnlyIn(Dist.CLIENT)
class FrameBlockEntityRenderer(val context: BlockEntityRendererProvider.Context): BlockEntityRenderer<FrameBlockEntity> {
    override fun render(
        blockEntity: FrameBlockEntity,
        delta      : Float,
        poseStack  : PoseStack,
        buffers    : MultiBufferSource,
        light      : Int,
        overlay    : Int
    ) {
        // TODO: Run for all panels
        val panel = Minecraft.getInstance().modelManager.getModel(ModelResourceLocation.standalone(modLoc("block/panel/debug")))

        /*poseStack.pushPose()
        context.blockRenderDispatcher.modelRenderer.renderModel(
            poseStack.last(),
            buffers.getBuffer(RenderType.solid()),
            null,
            panel,
            1.0f, 1.0f, 1.0f,
            light,
            overlay
        )
        poseStack.popPose()*/
    }
}