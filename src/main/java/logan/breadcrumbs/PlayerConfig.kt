package logan.breadcrumbs

import logan.api.util.secondsToTicks
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object PlayerConfig {
    private val configuration = YamlConfiguration.loadConfiguration(File(PLAYER_CONFIG_PATH))

    init {
        Bukkit.getScheduler().runTaskTimer(BreadcrumbsPlugin.instance, {
            configuration.save(File(PLAYER_CONFIG_PATH))
        }, secondsToTicks(10), secondsToTicks(10))
    }

    fun getColor(playerId: UUID) = configuration.getColor("$playerId.color", Config.getColor())

    fun setColor(playerId: UUID, color: Color) = configuration.set("$playerId.color", color)
}