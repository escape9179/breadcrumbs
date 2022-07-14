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
import java.io.IOException
import java.nio.file.Files

const val maxParticleCount = 1000
const val particleViewDistance = 32

const val dataFolderPath = "plugins/Breadcrumbs"
const val configPath = "$dataFolderPath/config.yml"

class BreadcrumbsPlugin : JavaPlugin() {

    private val playerParticleMap = mutableMapOf<Player, MutableList<Location>>()

    override fun onEnable() {
        val pluginId = 15747
        Metrics(this, pluginId)

        dataFolder.mkdirs()

        try {
            Files.copy(getResource("/config.yml")!!, dataFolder.toPath())
        } catch (e: IOException) {

        }

        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            playerParticleMap.forEach { (player, locations) ->
                if (!player.isOnline) return@forEach
                if (locations.isNotEmpty()) {
                    if (!locations.any { it.toBlockLocation() == player.location.toBlockLocation() }) {
                        locations.add(player.location)
                    }
                    if (locations.size >= maxParticleCount)
                        locations.removeFirst()
                    locations.forEach { location ->
                        if (player.location.distance(location) <= particleViewDistance) {
                            player.spawnParticle(
                                Particle.REDSTONE,
                                location,
                                Config.getCount(),
                                Particle.DustOptions(Config.getColor(), Config.getSize())
                            )
                        }
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