package logan.breadcrumbs

import org.bukkit.Color
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileReader

object Config {
    var configuration = YamlConfiguration.loadConfiguration(File(dataFolderPath))

    fun getColor() = configuration.getString("breadcrumbs.color")!!
        .split(",")
        .map { it.toInt() }
        .run { Color.fromRGB(this[0], this[1], this[2]) }

    fun getSize() = configuration.getDouble("breadcrumbs.size").toFloat()

    fun getCount() = configuration.getInt("breadcrumbs.count")

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(File(configPath))
    }
}