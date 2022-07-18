package logan.breadcrumbs

import logan.api.util.UpdateChecker
import org.bukkit.ChatColor
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isOp) {
            val pluginInstance = BreadcrumbsPlugin.instance
            val pluginVersion = BreadcrumbsPlugin.instance.description.version
            UpdateChecker(pluginInstance, 103340).getVersion {
                if (pluginVersion.equals(it)) event.player.sendMessage("${ChatColor.GREEN}You're up to date!")
                else event.player.sendMessage("${ChatColor.YELLOW}There is a new update available! (v$it -> v$pluginVersion)")
            }
        }
    }
}