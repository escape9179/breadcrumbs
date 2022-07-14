package logan.breadcrumbs

import logan.api.util.distanceFrom
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

class BreadcrumbParticle(val owner: Player, val location: Location, val color: Color, var duration: Int) {
    lateinit var durationTask: BukkitTask
    lateinit var spawnerTask: BukkitTask

    init {
        durationTask = Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin, Runnable {
            if (duration <= 0) {
                playersWithBreadcrumbs[owner]!!.remove(this)
                durationTask.cancel()
                spawnerTask.cancel()
            } else duration--
        }, 20, 20)

        spawnerTask = Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin, Runnable {
            if (!owner.isOnline
                || owner.distanceFrom(location) >= Config.getViewDistance()
            ) return@Runnable
            owner.spawnParticle(
                Particle.REDSTONE, location, Config.getCount(),
                Particle.DustOptions(Config.getColor(), Config.getSize())
            )
        }, Config.getEmissionFrequency(), Config.getEmissionFrequency())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BreadcrumbParticle) return false
        return owner == other.owner && location == other.location
    }
}