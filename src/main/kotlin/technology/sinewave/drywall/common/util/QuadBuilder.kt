package technology.sinewave.drywall.common.util

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction
import org.joml.Vector3f

// Why do I feel like I got a bit.. Extra(TM) with this
class QuadBuilder {
    private var vertexIndex = 0
    private val vertices    = IntArray(DefaultVertexFormat.BLOCK.vertexSize)
    private lateinit var sprite: TextureAtlasSprite
    private lateinit var normal: Vector3f

    // Convenience for pixel alignment
    fun vertex(
        x: Int, y: Int, z: Int,
        u: Int, v: Int
    ) {
        vertex(x/16f, y/16f, z/16f, u.toFloat(), v.toFloat())
    }

    fun vertex(
        x: Float, y: Float, z: Float,
        u: Float, v: Float
    ) {
        if (vertexIndex >= 4) { throw IllegalStateException("Too many vertices in BakedQuad") }

        var ptr = vertexIndex * DefaultVertexFormat.BLOCK.vertexSize / 4

        // Coordinates
        vertices[ptr++] = x.toBits()
        vertices[ptr++] = y.toBits()
        vertices[ptr++] = z.toBits()

        // Colour, which we override to 0xFFFFFFFF
        vertices[ptr++] = (255) or (255 shl 8) or (255 shl 16) or (255 shl 24)

        // UV map
        vertices[ptr++] = sprite.getU(u / 16).toBits()
        vertices[ptr++] = sprite.getV(v / 16).toBits()
        vertices[ptr++] = 0

        // Normals
        vertices[ptr] = vertices[ptr] or ((normal.x * 127).toInt() and 255)
        vertices[ptr] = vertices[ptr] or ((normal.y * 127).toInt() and 255) shl 8
        vertices[ptr] = vertices[ptr] or ((normal.y * 127).toInt() and 255) shl 16
        vertexIndex += 1
    }

    companion object {
        fun quad(
            facing: Direction,
            sprite: TextureAtlasSprite,
            shade : Boolean = false,
            init  : QuadBuilder.() -> Unit
        ): BakedQuad {
            val builder = QuadBuilder()
            builder.sprite = sprite
            builder.normal = Vector3f(
                facing.normal.x.toFloat(),
                facing.normal.y.toFloat(),
                facing.normal.z.toFloat()
            )
            builder.init()

            return BakedQuad(builder.vertices, -1, facing, sprite, shade)
        }
    }
}