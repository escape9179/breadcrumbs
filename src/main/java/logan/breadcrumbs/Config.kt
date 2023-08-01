package logan.breadcrumbs

import logan.api.util.toBukkitColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object Config {

    private var configuration = YamlConfiguration.loadConfiguration(File(CONFIG_PATH))

    fun getPrefix() = configuration.getString("prefix")

    fun getReloadMessage() = configuration.getString("message.reload", "Reloaded config.")

    fun getToggleOnMessage() = configuration.getString("message.toggle-on", "Breadcrumbs on.")

    fun getToggleOffMessage() = configuration.getString("message.toggle-off", "Breadcrumbs off.")

    fun getSetColorMessage() = configuration.getString("message.set-color", "Set breadcrumb color to %d, %d, %d.")

    fun getUnknownColorMessage() = configuration.getString("message.unknown-color", "Unknown color: %s.")

    fun getColor() = configuration.getString("breadcrumbs.color")!!.toBukkitColor()

    fun getSize() = configuration.getDouble("breadcrumbs.size", 1.0).toFloat()

    fun getCount() = configuration.getInt("breadcrumbs.count", 3)

    fun getSpread() = configuration.getDouble("breadcrumbs.spread", 0.10)

    fun getDefaultDuration() = configuration.getLong("breadcrumbs.duration.default", 1800)

    fun getDurations(): List<Pair<String, Long>> {
        return configuration.getConfigurationSection("breadcrumbs.duration")
            .run {
                getKeys(false).map { it to this.getLong(it) }
            }
    }

    fun getEmissionFrequency() = configuration.getLong("breadcrumbs.emission-frequency")

    fun getPlaceFrequency() = configuration.getLong("breadcrumbs.place-frequency")

    fun getSpawnDistance() = configuration.getDouble("breadcrumbs.spawn-distance", 5.2)

    fun getViewDistance() = configuration.getInt("breadcrumbs.view-distance")

    fun getMultiplayer() = configuration.getBoolean("breadcrumbs.multiplayer", false)

    fun reload() {
        configuration = YamlConfiguration.loadConfiguration(File(CONFIG_PATH))
    }
}