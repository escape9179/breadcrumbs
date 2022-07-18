package logan.breadcrumbs

import logan.api.bstats.Metrics
import logan.api.command.CommandDispatcher
import logan.api.util.UpdateChecker
import logan.api.util.toBlockLocation
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

class BreadcrumbsPlugin : JavaPlugin() {

    override fun onEnable() {
        instance = this

        checkForUpdates()

        val pluginId = 15747
        Metrics(this, pluginId)

        createPluginFiles()
        registerEvents()
        registerCommands()
        startBreadcrumbDurationTimer()

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
            else logger.info("${ChatColor.YELLOW}There is a new update available! (v$it -> v${this.description.version})")
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

    private fun startBreadcrumbDurationTimer() {
        Bukkit.getScheduler().runTaskTimer(this, {
            playersWithBreadcrumbs.forEach { (playerId, breadcrumbs) ->
                val player = Bukkit.getPlayer(playerId) ?: return@forEach
                if (player.location.toBlockLocation() == breadcrumbs.last().location.toBlockLocation()) {
                    breadcrumbs.last().duration = PlayerConfig.getDuration(playerId)
                    return@forEach
                }
                breadcrumbs.add(BreadcrumbParticle(playerId, player.location, PlayerConfig.getColor(playerId), PlayerConfig.getDuration(playerId)))
            }
        }, Config.getPlaceFrequency(), Config.getPlaceFrequency())
    }

    companion object {
        lateinit var instance: BreadcrumbsPlugin
    }
}