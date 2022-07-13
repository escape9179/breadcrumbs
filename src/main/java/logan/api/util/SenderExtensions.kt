package logan.api.util

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun CommandSender.sendMessage(message: String, translateColorCodes: Boolean) {
    sendMessage(
        if (translateColorCodes) ChatColor.translateAlternateColorCodes('&', message)
        else message
    )
}

fun CommandSender.hasNoPermission(node: String): Boolean {
    return !this.hasPermission(node)
}