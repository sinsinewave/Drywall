package technology.sinewave.drywall.common.panel

import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import technology.sinewave.drywall.common.Drywall
import technology.sinewave.drywall.common.Registries

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object Panels {
    val REGISTRY: DeferredRegister<Panel> = DeferredRegister.create(Registries.Keys.PANELS, Drywall.ID)

    val DEBUG_PANEL: DeferredHolder<Panel, Panel> = REGISTRY.register("debug") { -> Panel() }

    // Only used to deal with IDEA constantly fucking optimising away that one import
    init { DeferredHolder<Any, Any>::getValue }
}