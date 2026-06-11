package technology.sinewave.drywall.common.block.frame

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import technology.sinewave.drywall.common.Registries
import technology.sinewave.drywall.common.block.Blocks
import technology.sinewave.drywall.common.panel.Panel

class FrameBlockEntity(
    pos  : BlockPos,
    state: BlockState
) : BlockEntity(
    Blocks.FRAME_BE.get(),
    pos,
    state
) {
    val panels = HashMap<Direction, Panel?>().apply {
        Direction.entries.forEach { this.put(it, null) }
    }

    infix fun hasPanelOn(dir: Direction): Boolean {
        return panels[dir] != null
    }

    fun checkAndSetBlockVariant() {
        if (!hasLevel()) { return }

        val newState = if (panels.values.all { it == null }) {
            Blocks.EMPTY_FRAME_BLOCK.get().defaultBlockState()
        }
        else {
            Blocks.SOLID_FRAME_BLOCK.get().defaultBlockState()
        }

        // This is probably atrocious practice but it does work
        // Making it less atrocious is Future Sine's problem
        this.blockState = newState
        level!!.setBlock(blockPos, newState, 1 or 2)
        level!!.setBlockEntity(this)
        setChanged()
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        val sides = tag.getCompound("Sides")

        for (entry in panels.entries) {
            val sideName = sides.getString(entry.key.serializedName)
            if (sideName.isEmpty()) { continue }

            entry.setValue(Registries.PANELS.get(ResourceLocation.parse(sideName)))
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        val sides = CompoundTag()
        for (entry in panels.entries) {
            val sideName = if (entry.value != null) {
                Registries.PANELS.getKey(entry.value!!).toString()
            }
            else { continue }

            sides.putString(entry.key.serializedName, sideName)
        }

        tag.put("Sides", sides)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag, registries)
        return tag
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)
}