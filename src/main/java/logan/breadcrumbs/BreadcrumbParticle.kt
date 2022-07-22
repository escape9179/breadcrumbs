package logan.breadcrumbs

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.scheduler.BukkitTask
import java.util.*

class BreadcrumbParticle(val playerId: UUID, val location: Location, var color: Color, var duration: Long) {
    private lateinit var spawnerTask: BukkitTask

    init {
        activate()
    }

    fun activate() {
        spawnerTask = Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin.instance, Runnable {
            val player = Bukkit.getPlayer(playerId) ?: return@Runnable
            player.spawnParticle(
                Particle.REDSTONE, location, Config.getCount(),
                Config.getSpread(), 0.0, Config.getSpread(),
                Particle.DustOptions(color, Config.getSize())
            )
        }, Config.getEmissionFrequency(), Config.getEmissionFrequency())
    }

    fun isActive() = !spawnerTask.isCancelled

    fun deactivate() {
        spawnerTask.cancel()
    }

    fun isWithinViewDistance() = location.distance(playerId.bukkitPlayer.location) <= Config.getViewDistance()

    override fun equals(other: Any?): Boolean {
        if (other !is BreadcrumbParticle) return false
        return playerId == other.playerId && location == other.location
    }
}