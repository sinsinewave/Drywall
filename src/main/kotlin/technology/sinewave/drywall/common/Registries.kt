package technology.sinewave.drywall.common

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegistryBuilder
import technology.sinewave.drywall.common.panel.Panel
import technology.sinewave.drywall.common.util.modLoc

@EventBusSubscriber(modid = Drywall.ID)
object Registries {
    object Keys {
        val PANELS: ResourceKey<Registry<Panel>> = ResourceKey.createRegistryKey(modLoc("panels"))
    }
    val PANELS: Registry<Panel> = RegistryBuilder(Keys.PANELS).sync(false).create()

    @SubscribeEvent
    fun registerRegistries(event: NewRegistryEvent) {
        event.register(PANELS)
    }
}