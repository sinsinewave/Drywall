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
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.client.model.QuadTransformers
import net.neoforged.neoforge.client.model.lighting.QuadLighter
import net.neoforged.neoforge.client.model.lighting.SmoothQuadLighter
import org.joml.Matrix4f
import technology.sinewave.drywall.common.Registries
import technology.sinewave.drywall.common.block.frame.FrameBlockEntity
import technology.sinewave.drywall.common.util.QuadBuilder

@OnlyIn(Dist.CLIENT)
class FrameBlockEntityRenderer(): BlockEntityRenderer<FrameBlockEntity> {
    override fun render(
        blockEntity: FrameBlockEntity,
        delta      : Float,
        matrices   : PoseStack,
        buffers    : MultiBufferSource,
        light      : Int,
        overlay    : Int
    ) {
        val vertices = buffers.getBuffer(RenderType.solid())
        val lighter = SmoothQuadLighter(BlockColors.createDefault())
        lighter.setup(blockEntity.level!!, blockEntity.blockPos, blockEntity.blockState)


        for (panelEntry in blockEntity.panels) {
            // Skip rendering empty sides
            panelEntry.value?.let {
                // TODO: Some sort of null safety for this perhaps
                val name = Registries.PANELS.getKey(it)!!

                val faceSprite = Minecraft.getInstance().modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(ResourceLocation.fromNamespaceAndPath(
                    name.namespace,
                    "block/panel/${name.path}"
                ))
                val edgeSprite = Minecraft.getInstance().modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(ResourceLocation.fromNamespaceAndPath(
                name.namespace,
                    "block/panel/${name.path}_edge"
                ))

                val quads = ArrayList<BakedQuad>()

                // All get a full outer face
                quads.add(QuadBuilder.quad(Direction.NORTH, faceSprite) {
                    vertex(0,  16,  0,  16, 0)
                    vertex(16, 16,  0,  0,  0)
                    vertex(16, 0,   0,  0,  16)
                    vertex(0,  0,   0,  16, 16)
                })

                // Inner face is more complex
                // Arguably we _could_ just let it clip
                // But this isn't a Bethesda product and also it's not that bad
                val offsets = intArrayOf(0, 0, 0, 0) // L/U/R/D
                for ((idx, neighbour) in neighbourDirections[panelEntry.key]!!.withIndex()) {
                    if (blockEntity hasPanelOn neighbour) { offsets[idx] = 4 }
                }
                // Inner face with shrinking offsets applied
                quads.add(QuadBuilder.quad(Direction.SOUTH, faceSprite) {
                    vertex(16-offsets[2], 16-offsets[1], 4,  16-offsets[2],  0+offsets[1])
                    vertex( 0+offsets[0], 16-offsets[1], 4,   0+offsets[0],  0+offsets[1])
                    vertex( 0+offsets[0],  0+offsets[3], 4,   0+offsets[0], 16-offsets[3])
                    vertex(16-offsets[2],  0+offsets[3], 4,  16-offsets[2], 16-offsets[3])
                })

                // Edging 😳
                // We can reuse the offsets from before for checking if the edge even needs rendering
                // Left
                if (offsets[0] == 0) { quads.add(QuadBuilder.quad(Direction.WEST, edgeSprite) {
                    vertex(0, 16-offsets[1], 4,  4,  0+offsets[1])
                    vertex(0, 16,            0,  0,  0)
                    vertex(0,  0,            0,  0, 16)
                    vertex(0,  0+offsets[3], 4,  4, 16-offsets[3])
                })}

                // Up
                if (offsets[1] == 0) { quads.add(QuadBuilder.quad(Direction.UP, edgeSprite) {
                    vertex(16-offsets[2], 16, 4,  4,  0+offsets[2])
                    vertex(16,            16, 0,  0,  0)
                    vertex( 0,            16, 0,  0, 16)
                    vertex( 0+offsets[0], 16, 4,  4, 16-offsets[0])
                })}

                // Right
                if (offsets[2] == 0) { quads.add(QuadBuilder.quad(Direction.WEST, edgeSprite) {
                    vertex(16,  0+offsets[3], 4,  4,  0+offsets[3])
                    vertex(16,  0,            0,  0,  0)
                    vertex(16, 16,            0,  0, 16)
                    vertex(16, 16-offsets[1], 4,  4, 16-offsets[1])
                })}

                // Down
                if (offsets[3] == 0) { quads.add(QuadBuilder.quad(Direction.DOWN, edgeSprite) {
                    vertex( 0+offsets[0], 0, 4,  4,  0+offsets[0])
                    vertex( 0,            0, 0,  0,  0)
                    vertex(16,            0, 0,  0, 16)
                    vertex(16-offsets[2], 0, 4,  4, 16-offsets[2])
                })}


                for (q in quads) {
                    QuadTransformers.applying(Transformation(when(panelEntry.key) {
                        Direction.DOWN  -> Matrix4f().rotateAroundCentre(-90f, Axis.XP)
                        Direction.UP    -> Matrix4f().rotateAroundCentre(90f, Axis.XP)
                        Direction.NORTH -> Matrix4f().rotateAroundCentre(0f)
                        Direction.SOUTH -> Matrix4f().rotateAroundCentre(180f)
                        Direction.WEST  -> Matrix4f().rotateAroundCentre(90f)
                        Direction.EAST  -> Matrix4f().rotateAroundCentre(-90f)
                    })).process(q).output(vertices, matrices, lighter, overlay)
                }
            }
        }
    }

    // Convenience shorthand for rendering a quad
    private fun BakedQuad.output(
        vertices: VertexConsumer,
        matrices: PoseStack,
        lighter : QuadLighter,
        overlay : Int
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

    // Neighbours of a panel direction in a fixed order/unwrap
    // Order is left, up, right, down; up and down are assumed to have been rotated from north on X+
    // Makes vertex fuckery a lot easier
    private val neighbourDirections = hashMapOf(
        Direction.DOWN  to arrayOf(Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH),
        Direction.UP    to arrayOf(Direction.WEST, Direction.SOUTH, Direction.EAST, Direction.NORTH)
    ).apply { Direction.entries.forEach { dir ->
        if (dir.axis.isHorizontal) { this.put(dir, arrayOf(dir.counterClockWise, Direction.UP, dir.clockWise, Direction.DOWN)) }
    }}

    private fun Matrix4f.rotateAroundCentre(deg: Float, axis: Axis = Axis.YP): Matrix4f {
        return this.rotateAround(axis.rotationDegrees(deg), 0.5f, 0.5f, 0.5f)
    }
}
