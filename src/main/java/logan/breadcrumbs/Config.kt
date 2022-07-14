package logan.breadcrumbs

import org.bukkit.Color
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileReader

object Config {
    private var configuration = YamlConfiguration.loadConfiguration(File(configPath))

    fun getColor() = configuration.getString("breadcrumbs.color")!!
        .split(",")
        .map { it.toInt() }
        .run { Color.fromRGB(this[0], this[1], this[2]) }

    fun getSize() = configuration.getDouble("breadcrumbs.size").toFloat()

    fun getCount() = configuration.getInt("breadcrumbs.count")

    fun getDuration() = configuration.getInt("breadcrumbs.duration")

    fun getEmissionFrequency() = configuration.getLong("breadcrumbs.emission-frequency")

    fun getPlaceFrequency() = configuration.getLong("breadcrumbs.place-frequency")

    fun getViewDistance() = configuration.getInt("breadcrumbs.view-distance")

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(File(configPath))
    }
}