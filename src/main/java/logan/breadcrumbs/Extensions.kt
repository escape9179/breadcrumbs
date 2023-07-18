package logan.breadcrumbs

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

val UUID.bukkitPlayer: Player
    get() = Bukkit.getPlayer(this)

fun Player.nearbyBreadcrumbs(breadcrumbs: List<BreadcrumbParticle>, distance: Int): List<BreadcrumbParticle> {
    return breadcrumbs.filter { it.location.distance(this.location) <= distance }
}