package logan.breadcrumbs

import org.bukkit.Color
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Config {

    private var configuration = YamlConfiguration.loadConfiguration(File(CONFIG_PATH))

    fun getColor(): Color {
        configuration.getString("breadcrumbs.color")!!
            .split(" ")
            .map { it.toInt() }
            .run {
                return if (size > 1 ) Color.fromRGB(this[0], this[1], this[2])
                else Color.fromRGB(this[0])
            }
    }

    fun getSize() = configuration.getDouble("breadcrumbs.size").toFloat()

    fun getCount() = configuration.getInt("breadcrumbs.count")

    fun getDuration() = configuration.getInt("breadcrumbs.duration")

    fun getEmissionFrequency() = configuration.getLong("breadcrumbs.emission-frequency")

    fun getPlaceFrequency() = configuration.getLong("breadcrumbs.place-frequency")

    fun getViewDistance() = configuration.getInt("breadcrumbs.view-distance")

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(File(CONFIG_PATH))
    }
}