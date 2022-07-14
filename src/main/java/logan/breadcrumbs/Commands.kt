package logan.breadcrumbs

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.util.sendMessage
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BreadcrumbsCommand : BasicCommand<CommandSender>(
    "breadcrumbs",
    0..0,
    target = SenderTarget.BOTH,
    permissionNode = "breadcrumbs.use",
    aliases = arrayOf("bc")
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
    0..0,
    parentCommand = "breadcrumbs",
    target = SenderTarget.BOTH,
    permissionNode = "breadcrumbs.reload",
    aliases = arrayOf("r")
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
    0..0,
    parentCommand = "breadcrumbs",
    target = SenderTarget.BOTH,
    permissionNode = "breadcrumbs.use",
    aliases = arrayOf("t"),
    usage = """
        Wrong syntax:
        /breadcrumbs <toggle|t>
    """.trimIndent(),
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