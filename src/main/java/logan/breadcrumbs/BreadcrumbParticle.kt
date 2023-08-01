package logan.breadcrumbs

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

/**
 * Represents a breadcrumb particle that is emitted by a player.
 *
 * @param playerId The UUID of the player emitting the particle.
 * @param location The location where the particle should be spawned.
 * @param color The color of the particle.
 * @param duration The duration of the particle in milliseconds.
 */
class BreadcrumbParticle(val playerId: UUID, val location: Location, var color: Color, var duration: Long) {
    private lateinit var spawnerTask: BukkitTask
    private val viewers = mutableSetOf<UUID>()

    init {
        viewers.add(playerId)
        activate()
    }

    /**
     * Activates the particle emission.
     *
     * This method starts a task that repeatedly emits particles with the configured settings.
     * The particles are emitted at the specified emission frequency and spread around the
     * specified location. The color and size of the particles are also determined by the
     * configuration.
     *
     * @throws IllegalStateException if the Bukkit scheduler is not available.
     */
    fun activate() {
        spawnerTask = Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin.instance, {
            viewers.forEach { viewerId ->
                val viewer = viewerId.bukkitPlayer ?: return@forEach
                    viewer.spawnParticle(
                        Particle.REDSTONE, location, Config.getCount(),
                        Config.getSpread(), 0.0, Config.getSpread(),
                        Particle.DustOptions(color, Config.getSize())
                    )
            }
        }, Config.getEmissionFrequency(), Config.getEmissionFrequency())
    }

    fun isActive() = !spawnerTask.isCancelled

    fun deactivate() {
        spawnerTask.cancel()
    }

    fun isWithinViewDistanceOf(player: Player): Boolean {
        return location.distance(player.location) <= Config.getViewDistance()
    }

    fun removeViewer(viewer: UUID) = viewers.remove(viewer)

    fun addViewerOfNotAlreadyViewing(viewer: UUID): Boolean {
        return viewers.add(viewer)
    }

    fun resetDuration() {
        duration = PlayerConfig.getDuration(playerId)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BreadcrumbParticle) return false
        return playerId == other.playerId && location == other.location
    }

    override fun hashCode(): Int {
        var result = playerId.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + spawnerTask.hashCode()
        result = 31 * result + viewers.hashCode()
        return result
    }
}