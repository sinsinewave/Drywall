package technology.sinewave.drywall.common.block

import net.minecraft.core.registries.Registries
import technology.sinewave.drywall.common.Drywall
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import technology.sinewave.drywall.common.block.frame.EmptyFrameBlock
import technology.sinewave.drywall.common.block.frame.FrameBlock
import technology.sinewave.drywall.common.block.frame.FrameBlockEntity

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

// The full type gets stupidly long when you stick the BE type in the generic, this makes it a bit less obnoxious
typealias DeferredBlockEntityType<T> = DeferredHolder<BlockEntityType<*>, BlockEntityType<T>>

object Blocks {
    val REGISTRY   : DeferredRegister.Blocks              = DeferredRegister.createBlocks(Drywall.ID)
    val BE_REGISTRY: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Drywall.ID)

    val SOLID_FRAME_BLOCK: DeferredBlock<Block> = REGISTRY.register("solid_frame") { -> FrameBlock(
            BlockBehaviour.Properties.ofFullCopy(
            Blocks.OAK_PLANKS
        )
    )}

    val EMPTY_FRAME_BLOCK: DeferredBlock<Block> = REGISTRY.register("empty_frame") { -> EmptyFrameBlock(
            BlockBehaviour.Properties.ofFullCopy(
                Blocks.OAK_PLANKS
            ).noOcclusion().isViewBlocking { _,_,_ -> false } // <- That is dumb, why can't it just take a bool
        )
    }

    val FRAME_BE: DeferredBlockEntityType<FrameBlockEntity> = BE_REGISTRY.register("frame") { ->
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") // Don't care, get nulled
        BlockEntityType.Builder.of(::FrameBlockEntity, SOLID_FRAME_BLOCK.get(), EMPTY_FRAME_BLOCK.get()).build(null)
    }

    // Only used to deal with IDEA constantly fucking optimising away that one import
    init { DeferredHolder<Any, Any>::getValue }
}
