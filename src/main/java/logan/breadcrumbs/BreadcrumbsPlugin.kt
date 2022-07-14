package logan.breadcrumbs

import logan.api.bstats.Metrics
import logan.api.util.sendMessage
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

const val maxParticleCount = 1000
const val particleViewDistance = 32

class BreadcrumbsPlugin : JavaPlugin() {

    private val playerParticleMap = mutableMapOf<Player, MutableList<Location>>()

    override fun onEnable() {
        val pluginId = 15747
        Metrics(this, pluginId)



        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            playerParticleMap.forEach { (player, locations) ->
                if (!player.isOnline) return@forEach
                if (locations.isNotEmpty()) {
                    var add = true

                    locations.forEach inner@ {
                        if (it.toBlockLocation() == player.location.toBlockLocation()) {
                            add = false
                            return@inner
                        }
                    }
                    if (add) locations.add(player.location)
                    if (locations.size >= maxParticleCount)
                        locations.removeFirst()
                    locations.forEach { location ->
                        if (player.location.distance(location) <= particleViewDistance)
                            player.spawnParticle(Particle.REDSTONE, location, 3, Particle.DustOptions(Color.fromRGB(255, 182, 67), 1.0f))
                    }
                }
            }
        }, 10, 10)
        logger.info("$name enabled.")
    }

    override fun onDisable() {
        logger.info("$name disabled.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("breadcrumbs", true) && sender is Player) {
            if (!playerParticleMap.contains(sender)) playerParticleMap.put(sender, mutableListOf(sender.location))
                .run { sender.sendMessage("Breadcrumbs &aon&r.", true) }
            else {
                val result = playerParticleMap.remove(sender).run { sender.sendMessage("Breadcrumbs &coff&r.", true) }
                logger.info("Remove success: $result. Map length: ${playerParticleMap.size}")
            }
        }
        return true
    }
}