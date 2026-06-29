package technology.sinewave.drywall.common.block.frame

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import technology.sinewave.drywall.common.panel.Panels

open class FrameBlock(properties: Properties) : Block(properties), EntityBlock {
    companion object {
        val NORTH: BooleanProperty = BlockStateProperties.NORTH
        val EAST : BooleanProperty = BlockStateProperties.EAST
        val SOUTH: BooleanProperty = BlockStateProperties.SOUTH
        val WEST : BooleanProperty = BlockStateProperties.WEST
        val UP   : BooleanProperty = BlockStateProperties.UP
        val DOWN : BooleanProperty = BlockStateProperties.DOWN
    }

    init {
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST,  false)
            .setValue(SOUTH, false)
            .setValue(WEST,  false)
            .setValue(UP,    false)
            .setValue(DOWN,  false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder
            .add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN)
    }

    private fun checkSide(side: Direction, pos: BlockPos, level: LevelAccessor): Boolean {
        val neighbour = level.getBlockState(pos.relative(side))

        return if (neighbour.block !is FrameBlock) {
            neighbour.isFaceSturdy(level, pos.relative(side), side.opposite)
        }
        else {
            val be = level.getBlockEntity(pos.relative(side)) as FrameBlockEntity
            (be hasPanelOn side.opposite).not()
        }
    }

    fun getStateForPosition(
        level: LevelAccessor,
        pos  : BlockPos,
        state: BlockState = this.defaultBlockState()
    ): BlockState {
        return state
            .setValue(NORTH, checkSide(Direction.NORTH, pos, level))
            .setValue(EAST,  checkSide(Direction.EAST,  pos, level))
            .setValue(SOUTH, checkSide(Direction.SOUTH, pos, level))
            .setValue(WEST,  checkSide(Direction.WEST,  pos, level))
            .setValue(UP,    checkSide(Direction.UP,    pos, level))
            .setValue(DOWN,  checkSide(Direction.DOWN,  pos, level))
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val pos   = context.clickedPos
        val level = context.level
        return getStateForPosition(level, pos)
    }

    override fun updateShape(
        state       : BlockState,
        direction   : Direction,
        neighbour   : BlockState,
        level       : LevelAccessor,
        pos         : BlockPos,
        neighbourPos: BlockPos
    ): BlockState {
        level.lightEngine.checkBlock(pos)

        return getStateForPosition(level, pos, state)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return FrameBlockEntity(pos, state)
    }

    // TODO:TEMPORARY
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val be = level.getBlockEntity(pos) as FrameBlockEntity
        val p = be.panels[hitResult.direction]
        if (p == null) { be.panels[hitResult.direction] = Panels.DRYWALL_PANEL.get() }
        else { be.panels[hitResult.direction] = null }
        be.checkAndSetBlockVariant()

        level.lightEngine.checkBlock(pos)
        return InteractionResult.SUCCESS_NO_ITEM_USED
    }
}