package logan.breadcrumbs

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.util.sendMessage
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BreadcrumbsCommand : BasicCommand<CommandSender>(
    "breadcrumbs",
    "breadcrumbs.use",-
    0..0,
    arrayOf("bc"),
    SenderTarget.PLAYER
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        sender.sendMessage("""
            &eBreadcrumbs v1.0
            &e/breadcrumbs reload - Reload config.
            &e/breadcrumbs toggle - Toggles breadcrumbs on or off.
        """.trimIndent(), true)
        return true
    }
}

class ReloadCommand : BasicCommand<CommandSender>(
    "reload",
    "breadcrumbs.reload",
    0..0,
    arrayOf("r"),
    SenderTarget.BOTH,
    "breadcrumbs"
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        Config.reload()
        playersWithBreadcrumbs.values.forEach {
            it.filter {
                it.duration >= Config.getDuration()
            }.forEach { it.duration = Config.getDuration() }
        }
        sender.sendMessage("&eReloaded config.", true)
        return true
    }
}

class ToggleCommand : BasicCommand<Player>(
    "toggle",
    "breadcrumbs.use",
    0..0,
    arrayOf("t"),
    SenderTarget.PLAYER,
    "breadcrumbs"
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        if (playersWithBreadcrumbs.contains(sender.uniqueId)) {
            playersWithBreadcrumbs.remove(sender.uniqueId)?.forEach(BreadcrumbParticle::cancelTasks)
            sender.sendMessage("&eBreadcrumbs &coff.", true)
        } else {
            playersWithBreadcrumbs[sender.uniqueId] = mutableListOf(BreadcrumbParticle(sender.uniqueId, sender.location, Config.getColor(), Config.getDuration()))
            sender.sendMessage("&eBreadcrumbs &aon.", true)
        }
        return true
    }
}