package technology.sinewave.drywall.common.panel

import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.shapes.VoxelShape

class Panel(
    val shape: VoxelShape = Block.box(
        00.0, 00.0, 00.0,
        16.0, 16.0, 04.0
    )
)