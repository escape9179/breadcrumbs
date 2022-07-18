package logan.breadcrumbs

import logan.api.command.BasicCommand
import logan.api.command.SenderTarget
import logan.api.util.sendMessage
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
        playersWithBreadcrumbs.values.forEach {
            it.filter {
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
            playersWithBreadcrumbs.remove(sender.uniqueId)?.forEach(BreadcrumbParticle::cancelTasks)
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
            Class.forName("org.bukkit.Color").getField(args[0].uppercase()).get(null) as Color
        } catch (e: NoSuchFieldException) {
            try {
                if (args.size == 1) {
                    Color.fromRGB(Integer.decode(args[0].replace("#", "0x")))
                } else Color.fromRGB(args[0].toInt(), args[1].toInt(), args[2].toInt())
            } catch (e: IllegalArgumentException) {
                sender.sendMessage(Config.getPrefix() + " " + String.format(Config.getUnknownColorMessage(), args[0]), true)
                return true
            }
        }
        PlayerConfig.setColor(sender.uniqueId, color)
        playersWithBreadcrumbs[sender.uniqueId]?.forEach { it.color = color }
        sender.sendMessage(Config.getPrefix() + " " + String.format(Config.getSetColorMessage(), color.red, color.green, color.blue), true)
        return true
    }
}