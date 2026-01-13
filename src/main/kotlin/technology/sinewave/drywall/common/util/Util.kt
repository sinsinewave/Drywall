package technology.sinewave.drywall.common.util

import net.minecraft.resources.ResourceLocation
import technology.sinewave.drywall.common.Drywall

// Miscellaneous utility functions that don't really seem at home anywhere else
fun modLoc(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(Drywall.ID, path)