package technology.sinewave.drywall.common.item

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import technology.sinewave.drywall.common.Drywall
import technology.sinewave.drywall.common.block.Blocks

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object Items {
    val REGISTRY    : DeferredRegister.Items            = DeferredRegister.createItems(Drywall.ID)
    val TAB_REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Drywall.ID)

    val FRAME_BLOCK_ITEM: DeferredItem<BlockItem> = REGISTRY.registerSimpleBlockItem(Blocks.FRAME_BLOCK)



    init {
        // Register creative tab
        TAB_REGISTRY.register("drywall_group") { ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.${Drywall.ID}"))
                .icon { ItemStack(FRAME_BLOCK_ITEM.get()) }
                .displayItems { params, it ->
                    it.accept { FRAME_BLOCK_ITEM.get() }
                }
            .build()
        }
    }

    // Only used to deal with IDEA constantly fucking optimising away that one import
    init { DeferredHolder<Any, Any>::getValue }
}