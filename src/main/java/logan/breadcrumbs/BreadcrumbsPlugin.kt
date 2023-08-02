package logan.breadcrumbs

import logan.api.bstats.Metrics
import logan.api.command.CommandDispatcher
import logan.api.util.UpdateChecker
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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
        startBreadcrumbDurationMonitor()
        startBreadcrumbVisibilityUpdater()

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
        CommandDispatcher.registerCommand(AddCommand())
        CommandDispatcher.registerCommand(RemoveCommand())
    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(PlayerJoinListener(), this)
    }

    /**
     * Starts a timer to periodically place breadcrumbs for players.
     *
     * This method uses a Bukkit scheduler to run a task at regular intervals.
     * The task iterates through the `playersWithBreadcrumbs` map, checks if
     * any active breadcrumbs are close to the player, and resets their duration.
     * If no active breadcrumbs are close to the player, a new breadcrumb is placed
     * and added to the player's breadcrumb list.
     *
     * @see Config.getPlaceFrequency
     * @see BreadcrumbParticle.isActive
     * @see Player.isCloseToBreadcrumb
     * @see placeBreadcrumbForPlayer
     */
    private fun startBreadcrumbPlaceTimer() {
        Bukkit.getScheduler().runTaskTimer(this, {
            playersWithBreadcrumbs.forEach outer@{ (playerId, breadcrumbList) ->
                val player = playerId.bukkitPlayer ?: return@outer
                breadcrumbList.filter(BreadcrumbParticle::isActive).forEach inner@{ breadcrumb ->
                    if (player.isCloseToBreadcrumb(breadcrumb)) {
                        breadcrumb.resetDuration()
                        return@outer
                    }
                }
                val breadcrumb = placeBreadcrumbForPlayer(player)
                breadcrumbList.add(breadcrumb)
            }
        }, Config.getPlaceFrequency(), Config.getPlaceFrequency())
    }

    private fun placeBreadcrumbForPlayer(player: Player): BreadcrumbParticle {
        return BreadcrumbParticle(
            player.uniqueId,
            player.location,
            PlayerConfig.getColor(player.uniqueId),
            PlayerConfig.getDuration(player.uniqueId)
        )
    }

    /**
     * Starts a timer to monitor the duration of active breadcrumbs for all players.
     * The duration for each breadcrumb will be decremented by 1 at each tick of the timer,
     * and any breadcrumbs with a duration less than or equal to 0 will be deactivated and removed.
     */
    private fun startBreadcrumbDurationMonitor() {
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

    /**
     * Starts a timer that updates the visibility of breadcrumbs for all players.
     * This method is called internally by the plugin and is not meant to be accessed directly.
     */
    private fun startBreadcrumbVisibilityUpdater() {
        Bukkit.getScheduler().runTaskTimer(this, {
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