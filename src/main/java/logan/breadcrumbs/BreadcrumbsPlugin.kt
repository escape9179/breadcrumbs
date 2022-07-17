package logan.breadcrumbs

import logan.api.bstats.Metrics
import logan.api.command.CommandDispatcher
import logan.api.util.toBlockLocation
import org.bukkit.Bukkit
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
val playersWithBreadcrumbs = mutableMapOf<UUID, MutableList<BreadcrumbParticle>>()

class BreadcrumbsPlugin : JavaPlugin() {

    override fun onEnable() {
        instance = this

        val pluginId = 15747
        Metrics(this, pluginId)

        dataFolder.mkdirs()
        try {
            Files.copy(javaClass.getResourceAsStream("/config.yml")!!, Paths.get(CONFIG_PATH))
        } catch (e: IOException) {

        }

        CommandDispatcher.registerCommand(BreadcrumbsCommand())
        CommandDispatcher.registerCommand(ToggleCommand())
        CommandDispatcher.registerCommand(ReloadCommand())
        CommandDispatcher.registerCommand(ColorCommand())

        Bukkit.getScheduler().runTaskTimer(this, {
            playersWithBreadcrumbs.forEach { (playerId, breadcrumbs) ->
                val player = Bukkit.getPlayer(playerId) ?: return@forEach
                if (player.location.toBlockLocation() == breadcrumbs.last().location.toBlockLocation()) {
                    breadcrumbs.last().duration = Config.getDuration()
                    return@forEach
                }
                breadcrumbs.add(BreadcrumbParticle(playerId, player.location, PlayerConfig.getColor(playerId), Config.getDuration()))
            }
        }, Config.getPlaceFrequency(), Config.getPlaceFrequency())

        logger.info("$name enabled.")
    }

    override fun onDisable() {
        logger.info("$name disabled.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return CommandDispatcher.onCommand(sender, command, label, args)
    }

    companion object {
        lateinit var instance: BreadcrumbsPlugin
    }
}