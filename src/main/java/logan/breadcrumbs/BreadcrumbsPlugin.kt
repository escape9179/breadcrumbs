package logan.breadcrumbs

import logan.api.bstats.Metrics
import logan.api.command.CommandDispatcher
import logan.api.util.UpdateChecker
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

const val DATA_FOLDER_PATH = "plugins/Breadcrumbs"
const val CONFIG_PATH = "$DATA_FOLDER_PATH/config.yml"
const val PLAYER_CONFIG_PATH = "$DATA_FOLDER_PATH/player-data.yml"
const val PREFIX = "&e[Breadcrumbs]&r"
val playersWithBreadcrumbs = mutableMapOf<UUID, MutableList<BreadcrumbParticle>>()
var updated = false
lateinit var newVersion: String

class BreadcrumbsPlugin : JavaPlugin() {

    override fun onEnable() {
        instance = this

        checkForUpdates()

        val pluginId = 15747
        Metrics(this, pluginId)

        createPluginFiles()
        registerEvents()
        registerCommands()
        startBreadcrumbPlaceTimer()
        startBreadcrumbDurationTimer()
        startBreadcrumbUpdateTimer()

        logger.info("$name enabled.")
    }

    override fun onDisable() {
        logger.info("$name disabled.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return CommandDispatcher.onCommand(sender, command, label, args)
    }

    private fun createPluginFiles() {
        dataFolder.mkdirs()
        try {
            Files.copy(javaClass.getResourceAsStream("/config.yml")!!, Paths.get(CONFIG_PATH))
        } catch (e: IOException) {

        }
    }

    private fun checkForUpdates() {
        UpdateChecker(this, 103340).getVersion {
            if (this.description.version.equals(it)) logger.info("${ChatColor.GREEN}You're up to date!")
                .also { updated = true }
            else {
                logger.info("${ChatColor.YELLOW}There is a new update available! (v${this.description.version} -> v$it)")
                newVersion = it
                updated = false
            }
        }
    }

    private fun registerCommands() {
        CommandDispatcher.registerCommand(BreadcrumbsCommand())
        CommandDispatcher.registerCommand(ToggleCommand())
        CommandDispatcher.registerCommand(ReloadCommand())
        CommandDispatcher.registerCommand(ColorCommand())
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
    }

    private fun startBreadcrumbPlaceTimer() {
        Bukkit.getScheduler().runTaskTimer(this, {
            playersWithBreadcrumbs.forEach outer@{ (playerId, breadcrumbList) ->
                breadcrumbList.filter(BreadcrumbParticle::isActive).forEach inner@{ breadcrumb ->
                    val player = playerId.bukkitPlayer
                    if (player.isCloseToBreadcrumb(breadcrumb)) {
                        breadcrumb.resetDuration()
                        return@outer
                    }
                }
                val breadcrumb = placeBreadcrumbForPlayer(playerId)
                breadcrumbList.add(breadcrumb)
            }
        }, Config.getPlaceFrequency(), Config.getPlaceFrequency())
    }

    private fun placeBreadcrumbForPlayer(playerId: UUID): BreadcrumbParticle {
        return BreadcrumbParticle(
            playerId,
            playerId.bukkitPlayer.location,
            PlayerConfig.getColor(playerId),
            PlayerConfig.getDuration(playerId)
        )
    }

    private fun startBreadcrumbDurationTimer() {
        Bukkit.getScheduler().runTaskTimer(this, {
            playersWithBreadcrumbs.forEach { (_, breadcrumbList) ->
                breadcrumbList.removeIf { breadcrumb ->
                    if (breadcrumb.duration <= 0) {
                        breadcrumb.deactivate()
                        true
                    } else {
                        breadcrumb.duration--
                        false
                    }
                }
            }
        }, 20, 20)
    }

    private fun startBreadcrumbUpdateTimer() {
        Bukkit.getScheduler().runTaskTimer(this, {
            for (entry in playersWithBreadcrumbs.entries) {
                for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                    for (nearbyBreadcrumb in onlinePlayer.nearbyBreadcrumbs(entry.value, Config.getViewDistance())) {
                        nearbyBreadcrumb.addViewerOfNotAlreadyViewing(onlinePlayer.uniqueId)
                    }
                }
            }

            updateBreadcrumbsVisibility()
        }, 20, 20)
    }

    private fun updateBreadcrumbsVisibility() {
        playersWithBreadcrumbs.forEach { (_, breadcrumbList) ->
            breadcrumbList.forEach { breadcrumb ->
                for (player in server.onlinePlayers) {
                    if (breadcrumb.isWithinViewDistanceOf(player)) {
                        if (!breadcrumb.isActive()) {
                            breadcrumb.activate()
                            return
                        }
                    } else {
                        if (breadcrumb.isActive())
                            breadcrumb.deactivate()
                    }
                }
            }
        }
    }

    companion object {
        lateinit var instance: BreadcrumbsPlugin
    }
}