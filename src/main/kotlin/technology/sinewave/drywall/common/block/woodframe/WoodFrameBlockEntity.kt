package technology.sinewave.drywall.common.block.woodframe

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import technology.sinewave.drywall.common.block.Blocks

class WoodFrameBlockEntity(
    pos  : BlockPos,
    state: BlockState
) : BlockEntity(
    Blocks.FRAME_BE.get(),
    pos,
    state
) {
    // TODO: Implement panels
}