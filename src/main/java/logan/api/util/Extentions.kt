package logan.api.util

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.sendMessage(message: String, translateColorCodes: Boolean) {
    sendMessage(
        if (translateColorCodes) ChatColor.translateAlternateColorCodes('&', message)
        else message
    )
}

fun CommandSender.hasNoPermission(node: String): Boolean {
    return !this.hasPermission(node)
}

fun Player.equals(other: Player?): Boolean {
    return this.uniqueId == other?.uniqueId
}

fun Player.blockLocation() = location.toBlockLocation()

fun Player.distanceFrom(location: Location) = this.location.distance(location)

fun Location.toBlockLocation() = Location(world, blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())