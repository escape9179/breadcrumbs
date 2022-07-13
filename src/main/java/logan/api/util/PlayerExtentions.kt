package logan.api.util

import org.bukkit.entity.Player

fun Player.equals(other: Player?): Boolean {
    return this.uniqueId == other?.uniqueId
}