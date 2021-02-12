package perryplaysmc.dynamicgui.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 02/2021-Now
 **/

public class DynamicLogger {

    public static void error(String... messages) {
        for(String message : messages)
            log("&e&lERROR: &c" + message);
    }

    public static void info(String... messages) {
        for(String message : messages)
            log("&b&lINFO: &a" + message);
    }

    public static void warn(String... messages) {
        for(String message : messages)
            log("&c&lWARNING: &e" + message);
    }

    public static void log(String... messages) {
        for(String message : messages)
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
