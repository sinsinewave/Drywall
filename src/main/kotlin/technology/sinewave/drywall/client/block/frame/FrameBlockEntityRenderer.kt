package technology.sinewave.drywall.client.block.frame

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import com.mojang.math.Transformation
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockColors
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.client.model.QuadTransformers
import net.neoforged.neoforge.client.model.lighting.SmoothQuadLighter
import org.joml.Matrix4f
import technology.sinewave.drywall.common.Drywall
import technology.sinewave.drywall.common.Registries
import technology.sinewave.drywall.common.block.frame.EmptyFrameBlock
import technology.sinewave.drywall.common.block.frame.FrameBlockEntity
import technology.sinewave.drywall.common.util.QuadBuilder

@OnlyIn(Dist.CLIENT)
class FrameBlockEntityRenderer(): BlockEntityRenderer<FrameBlockEntity> {
    companion object {
        // QuadLighter used for AO and lighting on panels
        // TODO: Detect if player is using legacy lighting and use FlatQuadLighter in that case
        val lighter = SmoothQuadLighter(BlockColors.createDefault())

        val sideSprite = Minecraft.getInstance().modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(ResourceLocation.fromNamespaceAndPath(
            Drywall.ID,
            "block/solid_frame_side"
        ))

        val topSprite = Minecraft.getInstance().modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(ResourceLocation.fromNamespaceAndPath(
            Drywall.ID,
            "block/solid_frame_top"
        ))

        fun getSprite(direction: Direction): TextureAtlasSprite {
            return if (direction.axis.isHorizontal) {
                sideSprite
            }
            else {
                topSprite
            }
        }
    }

    override fun render(
        blockEntity: FrameBlockEntity,
        delta      : Float,
        matrices   : PoseStack,
        buffers    : MultiBufferSource,
        light      : Int,
        overlay    : Int
    ) {
        // Empty frames don't need panel rendering
        if (blockEntity.blockState.block is EmptyFrameBlock) { return }

        val vertices = buffers.getBuffer(RenderType.cutout())
        lighter.setup(blockEntity.level!!, blockEntity.blockPos, blockEntity.blockState)

        // Select either default non-panel texture, or retrieve panel's texture
        // TODO: maybe implement 3D panels?
        for (panelEntry in blockEntity.panels) {
            var sprite = getSprite(panelEntry.key)
            if (panelEntry.value != null) {
                // If panel
                val name = Registries.PANELS.getKey(panelEntry.value!!)
                if (name != null) {
                    sprite = Minecraft.getInstance().modelManager
                        .getAtlas(InventoryMenu.BLOCK_ATLAS)
                        .getSprite(ResourceLocation.fromNamespaceAndPath(
                            name.namespace,
                            "block/panel/${name.path}"
                        )
                    )
                }
            }

            // Build panel quad
            val quad = QuadBuilder.quad(Direction.NORTH, sprite) {
                vertex(0,  16,  0,  16, 0)
                vertex(16, 16,  0,  0,  0)
                vertex(16, 0,   0,  0,  16)
                vertex(0,  0,   0,  16, 16)
            }

            // Rotate quad to correct facing and render it
            QuadTransformers.applying(Transformation(when(panelEntry.key) {
                Direction.DOWN  -> Matrix4f().rotateAroundCentre(-90f, Axis.XP)
                Direction.UP    -> Matrix4f().rotateAroundCentre(90f, Axis.XP)
                Direction.NORTH -> Matrix4f().rotateAroundCentre(0f)
                Direction.SOUTH -> Matrix4f().rotateAroundCentre(180f)
                Direction.WEST  -> Matrix4f().rotateAroundCentre(90f)
                Direction.EAST  -> Matrix4f().rotateAroundCentre(-90f)
            })).process(quad).output(vertices, matrices, overlay)
        }
    }

    // Render a BakedQuad
    private fun BakedQuad.output(
        vertices   : VertexConsumer,
        matrices   : PoseStack,
        overlay    : Int,
    ) {
        matrices.pushPose()
        lighter.computeLightingForQuad(this)

        vertices.putBulkData(
            matrices.last(),
            this,
            lighter.computedBrightness,
            1f, 1f, 1f, 1f,
            lighter.computedLightmap,
            overlay,
            false
        )
        matrices.popPose()
    }

    // Rotate a Matrix4f around block centre
    private fun Matrix4f.rotateAroundCentre(deg: Float, axis: Axis = Axis.YP): Matrix4f {
        return this.rotateAround(axis.rotationDegrees(deg), 0.5f, 0.5f, 0.5f)
    }
}
