package logan.breadcrumbs

import logan.api.util.distanceFrom
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitTask
import java.util.*

class BreadcrumbParticle(val playerId: UUID, val location: Location, var color: Color, var duration: Int) {
    lateinit var durationTask: BukkitTask
    lateinit var spawnerTask: BukkitTask

    init {
        durationTask = Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin.instance, Runnable {
            if (duration <= 0) {
                playersWithBreadcrumbs[playerId]!!.remove(this)
                durationTask.cancel()
                spawnerTask.cancel()
            } else duration--
        }, 20, 20)

        spawnerTask = Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin.instance, Runnable {
            val player = Bukkit.getPlayer(playerId) ?: return@Runnable
            if (player.distanceFrom(location) >= Config.getViewDistance()
            ) return@Runnable
            player.spawnParticle(
                Particle.REDSTONE, location, Config.getCount(),
                Particle.DustOptions(color, Config.getSize())
            )
        }, Config.getEmissionFrequency(), Config.getEmissionFrequency())
    }

    fun cancelTasks() {
        durationTask.cancel()
        spawnerTask.cancel()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BreadcrumbParticle) return false
        return playerId == other.playerId && location == other.location
    }
}