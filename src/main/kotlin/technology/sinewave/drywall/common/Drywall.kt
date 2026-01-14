package technology.sinewave.drywall.common

import net.minecraft.client.Minecraft
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import technology.sinewave.drywall.common.block.Blocks
import technology.sinewave.drywall.common.item.Items
import technology.sinewave.drywall.common.panel.Panels
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(Drywall.ID)
object Drywall {
    const val ID = "drywall"

    // Common logger
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        Blocks.REGISTRY.register(MOD_BUS)
        Blocks.BE_REGISTRY.register(MOD_BUS)
        Items.REGISTRY.register(MOD_BUS)
        Items.TAB_REGISTRY.register(MOD_BUS)
        Panels.REGISTRY.register(MOD_BUS)

        runForDist(clientTarget = {
            MOD_BUS.addListener(::onClientSetup)
            Minecraft.getInstance()
        }, serverTarget = {
            MOD_BUS.addListener(::onServerSetup)
        })
    }

    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) { }
    private fun onClientSetup(event: FMLClientSetupEvent) { }
}