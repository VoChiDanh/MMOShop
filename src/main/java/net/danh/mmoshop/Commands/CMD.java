package net.danh.mmoshop.Commands;

import net.danh.litecore.CMD.CMDBase;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.MMOShop;
import net.danh.mmoshop.Manager.Shops;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.danh.litecore.Utils.Chat.sendCommandSenderMessage;
import static net.danh.mmoshop.File.Files.getConfig;
import static net.danh.mmoshop.File.Files.getLanguage;

public class CMD extends CMDBase {

    public CMD(JavaPlugin core) {
        super(core, "mmoshop");
    }

    @Override
    public void execute(CommandSender c, String[] args) {
        if (args.length == 1) {
            if (c.hasPermission("mmoshop.admin")) {
                if (args[0].equalsIgnoreCase("reload")) {
                    Files.reload();
                    sendCommandSenderMessage(c, "&aReloaded");
                }
            }
            if (args[0].equalsIgnoreCase("help")) {
                sendCommandSenderMessage(c, getLanguage().getStringList("HELP.DEFAULT"));
                if (c.hasPermission("mmoshop.admin")) {
                    sendCommandSenderMessage(c, getLanguage().getStringList("HELP.ADMIN"));
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("shop")) {
                if (getConfig().getStringList("SHOP").contains(args[1])) {
                    if (c.hasPermission("mmoshop.shop." + args[1]) || c.hasPermission("mmoshop.shop.*")) {
                        Shop shop = new Shop(args[1]);
                        if (shop.getConfig().getKeys(false).size() == 0) {
                            sendCommandSenderMessage(c, "&cShop " + args[1] + " is empty, let's see example.yml in plugin folder to config new shop!");
                            return;
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (c instanceof Player) {
                                    Shops.openShop((Player) c, new Shop(args[1]));
                                }
                            }
                        }.runTask(MMOShop.getInstance());
                    }
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
