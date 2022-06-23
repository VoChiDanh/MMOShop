package net.danh.mmoshop.Manager;

import net.danh.dcore.Utils.Chat;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.MMOShop;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class Debug {

    public static Set<Player> sell = new HashSet<>();
    public static Set<Player> buy = new HashSet<>();

    public static HashMap<Player, String> name = new HashMap<>();
    public static HashMap<Player, String> item_type = new HashMap<>();
    public static HashMap<Player, String> item_id = new HashMap<>();

    public static void debug(String msg) {
        if (Files.getConfig().getBoolean("DEBUG")) {
            MMOShop.getInstance().getLogger().log(Level.INFO, Chat.colorize(msg));
        }
    }
}
