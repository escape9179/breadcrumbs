package logan.breadcrumbs

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.util.sendMessage
import logan.api.util.toBukkitColor
import org.bukkit.Color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BreadcrumbsCommand : BasicCommand<CommandSender>(
    "breadcrumbs",
    "breadcrumbs.use", -
    0..0,
    arrayOf("bc"),
    SenderTarget.PLAYER
) {
    override fun run(sender: CommandSender, args: Array<out String>, data: Any?): Boolean {
        sender.sendMessage(
            """
            &eBreadcrumbs v1.1
            &e/breadcrumbs reload &f- Reload config.
            &e/breadcrumbs toggle &f- Toggles breadcrumbs on or off.
            &e/breadcrumbs color <(r g b) | #hex | 0xhex> &f- Set the color of your breadcrumbs.
        """.trimIndent(), true
        )
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
        playersWithBreadcrumbs.values.forEach { breadcrumbList ->
            breadcrumbList.forEach { it.color = PlayerConfig.getColor(it.playerId)}
            breadcrumbList.filter {
                it.duration >= Config.getDefaultDuration()
            }.forEach { it.duration = Config.getDefaultDuration() }
        }
        sender.sendMessage(Config.getPrefix() + " " + Config.getReloadMessage(), true)
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
            playersWithBreadcrumbs.remove(sender.uniqueId)?.forEach(BreadcrumbParticle::deactivate)
            sender.sendMessage(PREFIX + " " + Config.getToggleOffMessage(), true)
        } else {
            Config.getDurations().filter { sender.hasPermission("breadcrumbs.duration.${it.first}") }
                .run {
                    if (isEmpty()) PlayerConfig.setDuration(sender.uniqueId, Config.getDefaultDuration())
                    else PlayerConfig.setDuration(sender.uniqueId, last().second)
                }
            playersWithBreadcrumbs[sender.uniqueId] = mutableListOf(
                BreadcrumbParticle(
                    sender.uniqueId, sender.location,
                    PlayerConfig.getColor(sender.uniqueId), PlayerConfig.getDuration(sender.uniqueId)
                )
            )

            sender.sendMessage(PREFIX + " " + Config.getToggleOnMessage(), true)
        }
        return true
    }
}

class ColorCommand : BasicCommand<Player>(
    "color",
    "breadcrumbs.color",
    1..3,
    target = SenderTarget.PLAYER,
    parentCommand = "breadcrumbs"
) {
    override fun run(sender: Player, args: Array<out String>, data: Any?): Boolean {
        val color = try {
            if (args[0].equals("reset", true)) null
            else Class.forName("org.bukkit.Color").getField(args[0].uppercase()).get(null) as Color
        } catch (e: NoSuchFieldException) {
            try {
                args[0].replace("#", "0x").toBukkitColor()
            } catch (e: IllegalArgumentException) {
                sender.sendMessage(Config.getPrefix() + " " + String.format(Config.getUnknownColorMessage(), args[0]), true)
                return true
            }
        }
        PlayerConfig.setColor(sender.uniqueId, color)
        val newColor = PlayerConfig.getColor(sender.uniqueId)
        playersWithBreadcrumbs[sender.uniqueId]?.forEach { it.color = newColor }
        sender.sendMessage(Config.getPrefix() + " " + String.format(Config.getSetColorMessage(), newColor.red, newColor.green, newColor.blue), true)
        return true
    }
}