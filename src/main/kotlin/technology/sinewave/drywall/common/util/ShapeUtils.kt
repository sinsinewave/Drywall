package technology.sinewave.drywall.common.util

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

object ShapeUtils {
    fun horizontalBox(
        x1: Double, y1: Double, z1: Double,
        x2: Double, y2: Double, z2: Double,
        facing: Direction
    ): VoxelShape {
        return when(facing) {
            Direction.NORTH -> Block.box(x1, y1, z1, x2, y2, z2)
            Direction.SOUTH -> Block.box(16.0-x2, y1, 16.0-z2, 16.0-x1, y2, 16.0-z1)
            Direction.WEST  -> Block.box(z1, y1, x1, z2, y2, x2)
            Direction.EAST  -> Block.box(16.0-z2, y1, 16.0-x2, 16.0-z1, y2, 16.0-x1)
            else            -> throw RuntimeException("Invalid rotation for rotated box: ${facing.serializedName}")
        }
    }

    fun horizontalShape(facing: Direction, vararg elements: DoubleArray): VoxelShape {
        return Shapes.or(
            horizontalBox(elements[0][0], elements[0][1], elements[0][2], elements[0][3], elements[0][4], elements[0][5], facing),
            *(elements.drop(1).map { horizontalBox(it[0], it[1], it[2], it[3], it[4], it[5], facing) }.toTypedArray())
        )
    }
}