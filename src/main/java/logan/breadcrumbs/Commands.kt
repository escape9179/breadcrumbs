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
            ${breadcrumbsColorDark}Breadcrumbs v1.0
            ${breadcrumbsColorLight}/breadcrumbs reload - Reload config.
            ${breadcrumbsColorLight}/breadcrumbs toggle - Toggles breadcrumbs on or off.
        """.trimIndent())
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
        sender.sendMessage("${breadcrumbsColorLight}Reloaded config.")
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
        ${ChatColor.RED}Wring syntax:
        ${breadcrumbsColorLight}/breadcrumbs <toggle|t>
    """.trimIndent()
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        if (playersWithBreadcrumbs.contains(sender)) {
            playersWithBreadcrumbs.remove(sender)
            sender.sendMessage("${breadcrumbsColorLight}Breadcrumbs &coff.", true)
        } else {
            playersWithBreadcrumbs[sender] = mutableListOf(BreadcrumbParticle(sender, sender.location, Config.getColor(), Config.getDuration()))
            sender.sendMessage("${breadcrumbsColorLight}Breadcrumbs &aon.", true)
        }
        return true
    }
}