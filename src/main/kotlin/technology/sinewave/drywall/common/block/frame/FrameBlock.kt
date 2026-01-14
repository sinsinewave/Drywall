package technology.sinewave.drywall.common.block.frame

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
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
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import technology.sinewave.drywall.common.panel.Panels
import technology.sinewave.drywall.common.util.ShapeUtils

class FrameBlock(properties: Properties) : Block(properties), EntityBlock {
    companion object {
        val NORTH: BooleanProperty = BlockStateProperties.NORTH
        val EAST : BooleanProperty = BlockStateProperties.EAST
        val SOUTH: BooleanProperty = BlockStateProperties.SOUTH
        val WEST : BooleanProperty = BlockStateProperties.WEST


        private val sideShapes = arrayOf(
            doubleArrayOf(04.0, 00.0, 00.0, 12.0, 03.0, 04.0),
            doubleArrayOf(04.0, 13.0, 00.0, 12.0, 16.0, 04.0),
        )
        private val straightShapes = arrayOf(
            doubleArrayOf(04.0, 00.0, 06.0, 12.0, 16.0, 10.0),
            doubleArrayOf(04.0, 00.0, 00.0, 12.0, 03.0, 16.0),
            doubleArrayOf(04.0, 13.0, 00.0, 12.0, 16.0, 16.0),
        )

        val postShape: VoxelShape = box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)
        val sideShapeNorth: VoxelShape = ShapeUtils.horizontalShape(Direction.NORTH, *sideShapes)
        val sideShapeEast : VoxelShape = ShapeUtils.horizontalShape(Direction.EAST,  *sideShapes)
        val sideShapeSouth: VoxelShape = ShapeUtils.horizontalShape(Direction.SOUTH, *sideShapes)
        val sideShapeWest : VoxelShape = ShapeUtils.horizontalShape(Direction.WEST,  *sideShapes)
        val straightShapeZ: VoxelShape = ShapeUtils.horizontalShape(Direction.NORTH, *straightShapes)
        val straightShapeX: VoxelShape = ShapeUtils.horizontalShape(Direction.EAST,  *straightShapes)
    }

    init {
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST,  false)
            .setValue(SOUTH, false)
            .setValue(WEST,  false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder
            .add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val pos   = context.clickedPos
        val level = context.level
        return this.defaultBlockState()
            .setValue(NORTH, checkSide(Direction.NORTH, pos, level))
            .setValue(EAST,  checkSide(Direction.EAST,  pos, level))
            .setValue(SOUTH, checkSide(Direction.SOUTH, pos, level))
            .setValue(WEST,  checkSide(Direction.WEST,  pos, level))
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val north = state.getValue(NORTH)
        val east  = state.getValue(EAST)
        val south = state.getValue(SOUTH)
        val west  = state.getValue(WEST)
        // First check for straight piece
        return if (north && south && !south && !west) { straightShapeZ }
        else if (!north && !south && east && west) { straightShapeX }
        else if (north || east || south || west) {
            // Then check for individual sides
            val sides = arrayListOf<VoxelShape>()
            if (north) { sides.add(sideShapeNorth) }
            if (east)  { sides.add(sideShapeEast) }
            if (south) { sides.add(sideShapeSouth) }
            if (west)  { sides.add(sideShapeWest) }
            Shapes.or(postShape, *sides.toTypedArray())
        }
        else { postShape }
    }

    // Separate from just querying sides in case we have for example height variations in the frame model depending on configuration, like walls
    fun getOpenSides(state: BlockState): Map<Direction, Boolean> {
        return mapOf(
            Direction.NORTH to state.getValue(NORTH),
            Direction.EAST  to state.getValue(EAST),
            Direction.SOUTH to state.getValue(SOUTH),
            Direction.WEST  to state.getValue(WEST),
            Direction.UP    to true,
            Direction.DOWN  to false
        )
    }

    private fun checkSide(side: Direction, pos: BlockPos, level: LevelAccessor): Boolean {
        val neighbour = level.getBlockState(pos.relative(side))
        return neighbour.block is FrameBlock || neighbour.isFaceSturdy(level, pos.relative(side), side.opposite)
    }

    override fun updateShape(
        state       : BlockState,
        direction   : Direction,
        neighbour   : BlockState,
        level       : LevelAccessor,
        pos         : BlockPos,
        neighbourPos: BlockPos
    ): BlockState {
        return state
            .setValue(NORTH, checkSide(Direction.NORTH, pos, level))
            .setValue(EAST,  checkSide(Direction.EAST,  pos, level))
            .setValue(SOUTH, checkSide(Direction.SOUTH, pos, level))
            .setValue(WEST,  checkSide(Direction.WEST,  pos, level))
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
        if (p == null) { be.panels[hitResult.direction] = Panels.DEBUG_PANEL.get() }
        else { be.panels[hitResult.direction] = null }
        be.setChanged()

        return InteractionResult.SUCCESS_NO_ITEM_USED
    }
}