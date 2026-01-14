package technology.sinewave.drywall.client.block.frame

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import technology.sinewave.drywall.common.Registries
import technology.sinewave.drywall.common.block.frame.FrameBlockEntity

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
        for (panelEntry in blockEntity.panels) {
            // Skip rendering empty sides
            panelEntry.value?.let {
                // TODO: Some sort of null safety for this
                val name  = Registries.PANELS.getKey(it)!!

                val model = Minecraft.getInstance().modelManager.getModel(
                    ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(
                        name.namespace,
                        "block/panel/"+name.path
                    ))
                )

                poseStack.pushPose()
                val dir = panelEntry.key

                when (dir) {
                    Direction.EAST,
                    Direction.WEST -> { poseStack.rotateAround(
                        Axis.YP.rotationDegrees(dir.toYRot()),
                        0.5f, 0.5f, 0.5f
                    )}
                    Direction.NORTH,
                    Direction.SOUTH -> { poseStack.rotateAround(
                        Axis.YP.rotationDegrees(dir.toYRot()+180),
                        0.5f, 0.5f, 0.5f
                    )}
                    Direction.UP -> { poseStack.rotateAround(
                        Axis.XP.rotationDegrees(90f),
                        0.5f, 0.5f, 0.5f
                    )}
                    Direction.DOWN -> { poseStack.rotateAround(
                        Axis.XP.rotationDegrees(-90f),
                        0.5f, 0.5f, 0.5f
                    )}
                }
                context.blockRenderDispatcher.modelRenderer.renderModel(
                    poseStack.last(),
                    buffers.getBuffer(RenderType.solid()),
                    null,
                    model,
                    1.0f, 1.0f, 1.0f,
                    light,
                    overlay
                )
                poseStack.popPose()
            }
        }
    }
}