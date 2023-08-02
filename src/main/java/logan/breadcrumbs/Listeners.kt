package logan.breadcrumbs

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.player.isOp) {
            if (updated) event.player.sendMessage("$PREFIX ${ChatColor.GREEN}You're up to date!")
            else event.player.sendMessage("$PREFIX ${ChatColor.YELLOW}There is a new update available! (v${BreadcrumbsPlugin.instance.description.version} -> v$newVersion)")
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val player = event.player
        playersWithBreadcrumbs[player.uniqueId]?.forEach {
            it.deactivate()
        }
    }
}