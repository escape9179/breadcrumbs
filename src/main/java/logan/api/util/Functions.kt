package logan.api.util

import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun secondsToTicks(seconds: Long) = 20 * seconds

fun CommandSender.sendMessage(message: String, translateColorCodes: Boolean) {
    sendMessage(
        if (translateColorCodes) ChatColor.translateAlternateColorCodes('&', message)
        else message
    )
}

fun CommandSender.sendColoredFormattedMessage(message: String, vararg params: Any) {
    sendMessage(String.format(message, params), true)
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

fun String.toBukkitColor(): Color {
    return split(" ")
        .run {
            if (size == 1) {
                val formattedHexString = this[0].replace("#", "0x")
                Color.fromRGB(Integer.decode(formattedHexString))
            }
            else Color.fromRGB(this[0].toInt(), this[1].toInt(), this[2].toInt())
        }
}

fun String.isHexColor(): Boolean {
    val regex = "(#|0x)(\\d|[A-Fa-f]){6}".toRegex()
    return this.matches(regex)
}

fun String.isRgbColor(): Boolean {
    val regex = "\\d{1,3} \\d{1,3} \\d{1,3}".toRegex()
    return this.matches(regex)
}