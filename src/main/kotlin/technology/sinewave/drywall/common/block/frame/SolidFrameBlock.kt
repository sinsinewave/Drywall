package technology.sinewave.drywall.common.block.frame

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty

class SolidFrameBlock(properties: Properties) : FrameBlock(properties) {
    companion object {
        val PANEL_SIDES = mapOf<Direction, BooleanProperty>(
            *(Direction.entries.map { Pair(it, BooleanProperty.create("panel_"+it.serializedName.lowercase())) }).toTypedArray()
        )

        val PANEL_NORTH: BooleanProperty = BooleanProperty.create("panel_north")
        val PANEL_EAST : BooleanProperty = BooleanProperty.create("panel_east")
        val PANEL_SOUTH: BooleanProperty = BooleanProperty.create("panel_south")
        val PANEL_WEST : BooleanProperty = BooleanProperty.create("panel_west")
        val PANEL_UP   : BooleanProperty = BooleanProperty.create("panel_up")
        val PANEL_DOWN : BooleanProperty = BooleanProperty.create("panel_down")
    }

    init {
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(PANEL_NORTH, false)
            .setValue(PANEL_EAST,  false)
            .setValue(PANEL_SOUTH, false)
            .setValue(PANEL_WEST,  false)
            .setValue(PANEL_UP,    false)
            .setValue(PANEL_DOWN,  false)
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
}