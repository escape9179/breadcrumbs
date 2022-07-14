package logan.api.util

import org.bukkit.Location
import org.bukkit.entity.Player

fun Player.equals(other: Player?): Boolean {
    return this.uniqueId == other?.uniqueId
}

fun Player.blockLocation() = location.toBlockLocation()

fun Player.distanceFrom(location: Location) = this.location.distance(location)