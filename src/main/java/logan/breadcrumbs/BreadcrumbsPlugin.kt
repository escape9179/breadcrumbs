package logan.breadcrumbs

import logan.api.bstats.Metrics
import logan.api.command.CommandDispatcher
import logan.api.util.blockLocation
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

const val dataFolderPath = "plugins/Breadcrumbs"
const val configPath = "$dataFolderPath/config.yml"
val breadcrumbsColorDark = TextColor.color(255, 182, 67)
val breadcrumbsColorLight = TextColor.color(255, 218, 54)
val playersWithBreadcrumbs = mutableMapOf<Player, MutableList<BreadcrumbParticle>>()

object BreadcrumbsPlugin : JavaPlugin() {

    override fun onEnable() {
        val pluginId = 15747
        Metrics(this, pluginId)

        dataFolder.mkdirs()

        try {
            Files.copy(javaClass.getResourceAsStream("/config.yml")!!, Paths.get(configPath))
        } catch (e: IOException) {

        }

        CommandDispatcher.registerCommand(BreadcrumbsCommand())
        CommandDispatcher.registerCommand(ToggleCommand())
        CommandDispatcher.registerCommand(ReloadCommand())

        Bukkit.getScheduler().runTaskTimer(this, Runnable {
            playersWithBreadcrumbs.forEach { (player, breadcrumbs) ->
                if (player.blockLocation() == breadcrumbs.last().location.toBlockLocation())
                    return@forEach
                breadcrumbs.add(BreadcrumbParticle(player, player.location, Config.getColor(), Config.getDuration()))
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
}