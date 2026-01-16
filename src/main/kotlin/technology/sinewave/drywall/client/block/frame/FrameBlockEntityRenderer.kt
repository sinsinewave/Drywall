package technology.sinewave.drywall.client.block.frame

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.mojang.math.Transformation
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockColors
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.client.model.QuadTransformers
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.lighting.SmoothQuadLighter
import org.joml.Matrix4f
import technology.sinewave.drywall.common.Registries
import technology.sinewave.drywall.common.block.frame.FrameBlockEntity

@OnlyIn(Dist.CLIENT)
class FrameBlockEntityRenderer(val context: BlockEntityRendererProvider.Context): BlockEntityRenderer<FrameBlockEntity> {
    override fun render(
        blockEntity: FrameBlockEntity,
        delta      : Float,
        matrices   : PoseStack,
        buffers    : MultiBufferSource,
        light      : Int,
        overlay    : Int
    ) {
        val vertices = buffers.getBuffer(RenderType.solid())
        val quadLighter = SmoothQuadLighter(BlockColors.createDefault())
        quadLighter.setup(blockEntity.level!!, blockEntity.blockPos, blockEntity.blockState)

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

                matrices.pushPose()

                // Create QuadTransformer for rotations
                val transformer = QuadTransformers.applying(Transformation(when (panelEntry.key) {
                    Direction.EAST  -> Matrix4f().rotateAroundCentre(-90f)
                    Direction.WEST  -> Matrix4f().rotateAroundCentre(90f)
                    Direction.NORTH -> Matrix4f()
                    Direction.SOUTH -> Matrix4f().rotateAroundCentre(180f)
                    Direction.UP    -> Matrix4f().rotateAroundCentre(90f, Axis.XP)
                    Direction.DOWN  -> Matrix4f().rotateAroundCentre(-90f, Axis.XP)
                }))

                for (quad in model.getQuads(blockEntity.blockState, null, RandomSource.create(42), ModelData.EMPTY, RenderType.solid())) {
                    val transformedQuad = transformer.process(quad)
                    quadLighter.computeLightingForQuad(transformedQuad)
                    vertices.putBulkData(
                        matrices.last(),
                        transformedQuad,
                        quadLighter.computedBrightness,
                        1f, 1f, 1f, 1f,
                        quadLighter.computedLightmap,
                        overlay,
                        false
                    )
                }

                matrices.popPose()
            }
        }
    }

    private fun Matrix4f.rotateAroundCentre(deg: Float, axis: Axis = Axis.YP): Matrix4f {
        return this.rotateAround(axis.rotationDegrees(deg), 0.5f, 0.5f, 0.5f)
    }
}
