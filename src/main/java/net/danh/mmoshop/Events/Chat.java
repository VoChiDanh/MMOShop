package net.danh.mmoshop.Events;

import net.danh.mmoshop.Data.Item;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.MMOShop;
import net.danh.mmoshop.Manager.Debug;
import net.danh.mmoshop.Manager.Shops;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static net.danh.dcore.Random.Number.isInteger;

public class Chat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = ChatColor.stripColor(e.getMessage());
        if (Debug.sell.contains(p)) {
            Shop shop = new Shop(Debug.name.get(p));
            if (isInteger(msg)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Item.sellItem(p, Debug.item_type.get(p), Debug.item_id.get(p), shop.getConfig().getDouble("ITEMS." + Debug.item_id.get(p) + ".SELL_PRICE.COST"), shop.getConfig().getString("ITEMS." + Debug.item_id.get(p) + ".SYMBOL"), shop.getConfig().getStringList("ITEMS." + Debug.item_id.get(p) + ".SELL_PRICE.COMMAND"), Integer.parseInt(msg));
                        Debug.sell.remove(p);
                        Debug.name.remove(p, shop.getName());
                        Debug.item_type.remove(p);
                        Debug.item_id.remove(p);
                        Shops.openShop(p, shop);
                    }
                }.runTask(MMOShop.getInstance());
            }
            if (msg.equalsIgnoreCase("exit")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Debug.sell.remove(p);
                        Debug.item_type.remove(p);
                        Debug.item_id.remove(p);
                        Debug.name.remove(p, shop.getName());
                        Shops.openShop(p, shop);
                    }
                }.runTask(MMOShop.getInstance());
            }
            e.setCancelled(true);
        }
        if (Debug.buy.contains(p)) {
            Shop shop = new Shop(Debug.name.get(p));
            if (isInteger(msg)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Item.buyItem(p, Debug.item_type.get(p), Debug.item_id.get(p), shop.getConfig().getDouble("ITEMS." + Debug.item_id.get(p) + ".BUY_PRICE.COST"), shop.getConfig().getString("ITEMS." + Debug.item_id.get(p) + ".SYMBOL"), shop.getConfig().getStringList("ITEMS." + Debug.item_id.get(p) + ".BUY_PRICE.COMMAND"), shop.getConfig().getString("ITEMS." + Debug.item_id.get(p) + ".BUY_PRICE.PLACEHOLDER"), Integer.parseInt(msg));
                        Debug.buy.remove(p);
                        Debug.name.remove(p, shop.getName());
                        Debug.item_type.remove(p);
                        Debug.item_id.remove(p);
                        Shops.openShop(p, shop);
                    }
                }.runTask(MMOShop.getInstance());
            }
            if (msg.equalsIgnoreCase("exit")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Debug.buy.remove(p);
                        Debug.item_type.remove(p);
                        Debug.item_id.remove(p);
                        Debug.name.remove(p, shop.getName());
                        Shops.openShop(p, shop);
                    }
                }.runTask(MMOShop.getInstance());
            }
            e.setCancelled(true);
        }
    }
}

