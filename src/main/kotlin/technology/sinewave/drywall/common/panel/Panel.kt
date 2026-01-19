package technology.sinewave.drywall.common.panel

import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import technology.sinewave.drywall.common.util.ShapeUtils

// TODO: Some kind of PanelProperties with a nice builder and shit
class Panel() {
    // This is used to dynamically include panels in the frame block's shape
    fun getShape(side: Direction): VoxelShape {
        return ShapeUtils.facingShape(side, *arrayOf(
            doubleArrayOf(00.0, 00.0, 00.0, 16.0, 16.0, 04.0)
        ))
    }
}