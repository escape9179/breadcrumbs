package logan.breadcrumbs

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isOp) {
            if (updated) event.player.sendMessage("[${BreadcrumbsPlugin.instance.description.prefix}] ${ChatColor.GREEN}You're up to date!")
            else event.player.sendMessage("[${BreadcrumbsPlugin.instance.description.prefix}] ${ChatColor.YELLOW}There is a new update available! (v${BreadcrumbsPlugin.instance.description.version} -> v$newVersion)")
        }
    }
}