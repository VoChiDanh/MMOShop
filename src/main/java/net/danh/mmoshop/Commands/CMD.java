package net.danh.mmoshop.Commands;

import net.danh.dcore.Commands.CMDBase;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.MMOShop;
import net.danh.mmoshop.Manager.Shops;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.danh.dcore.List.Contain.inList;
import static net.danh.dcore.Utils.Player.sendConsoleMessage;
import static net.danh.dcore.Utils.Player.sendPlayerMessage;
import static net.danh.mmoshop.File.Files.*;

public class CMD extends CMDBase {

    public CMD(JavaPlugin core) {
        super(core, "mmoshop");
    }

    @Override
    public void playerexecute(Player p, String[] args) {
        if (args.length == 1) {
            if (p.hasPermission("mmoshop.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Files.reload();
                    sendPlayerMessage(p, "&aReloaded");
                }
            }
            if (args[0].equalsIgnoreCase("help")) {
                sendPlayerMessage(p, getLanguage().getStringList("HELP.DEFAULT"));
                if (p.hasPermission("mmoshop.admin")) {
                    sendPlayerMessage(p, getLanguage().getStringList("HELP.ADMIN"));
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("shop")) {
                if (inList(getConfig().getStringList("SHOP"), args[1])) {
                    if (p.hasPermission("mmoshop.shop." + args[1]) || p.hasPermission("mmoshop.shop.*")) {
                        Shop shop = new Shop(args[1]);
                        if (shop.getConfig().getKeys(false).size() == 0) {
                            sendPlayerMessage(p, "&cShop " + args[1] + " is empty, let's see example.yml in plugin folder to config new shop!");
                            return;
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Shops.openShop(p, new Shop(args[1]));
                            }
                        }.runTask(MMOShop.getInstance());
                    }
                }
            }
        }
    }

    @Override
    public void consoleexecute(ConsoleCommandSender c, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                reload();
            }
            if (args[0].equalsIgnoreCase("help")) {
                sendConsoleMessage(c, getLanguage().getStringList("HELP.DEFAULT"));
                sendConsoleMessage(c, getLanguage().getStringList("HELP.ADMIN"));
            }
        }
        if (args.length == 3) {
            Player p = Bukkit.getPlayer(args[2]);
            if (p == null) {
                return;
            }
            if (args[0].equalsIgnoreCase("shop")) {
                if (inList(getConfig().getStringList("SHOP"), args[1])) {
                    Shop shop = new Shop(args[1]);
                    if (shop.getConfig().getKeys(false).size() == 0) {
                        sendPlayerMessage(p, "&cShop " + args[1] + " is empty, let's see example.yml in plugin folder to config new shop!");
                        return;
                    }
                    Shops.openShop(p, new Shop(args[1]));
                }
            }
        }
    }

    @Override
    public List<String> TabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("mmoshop.admin")) {
                commands.add("help");
                commands.add("reload");
            }
            commands.add("shop");
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("shop")) {
                for (String shop_name : Files.getConfig().getStringList("SHOP")) {
                    if (sender.hasPermission("mmoshop.shop." + shop_name) || sender.hasPermission("mmoshop.shop.*")) {
                        StringUtil.copyPartialMatches(args[1], Collections.singleton(shop_name), completions);
                    }
                }
            }
        }
        Collections.sort(completions);
        return completions;
    }
}
