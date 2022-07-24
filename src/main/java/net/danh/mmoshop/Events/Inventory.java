package net.danh.mmoshop.Events;

import net.danh.dcore.Utils.Chat;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.Manager.Debug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Objects;

import static net.danh.dcore.Utils.Player.sendPlayerMessage;

public class Inventory implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();
            if (Debug.playerShopHashMap.containsKey(p)) {
                Shop shop = Debug.playerShopHashMap.get(p);
                FileConfiguration get = shop.getConfig();
                String sname = get.getString("NAME");
                if (e.getView().getTitle().equals(Chat.colorize(Objects.requireNonNull(sname)))) {
                    Debug.playerShopHashMap.remove(p);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if (Debug.playerShopHashMap.containsKey(p)) {
                Shop shop = Debug.playerShopHashMap.get(p);
                FileConfiguration get = shop.getConfig();
                String sname = get.getString("NAME");
                if (e.getView().getTitle().equals(Chat.colorize(Objects.requireNonNull(sname)))) {
                    e.setCancelled(true);
                    if (e.getClick() == ClickType.LEFT) {
                        int slot = e.getSlot();
                        for (String names : Objects.requireNonNull(get.getConfigurationSection("ITEMS")).getKeys(false)) {
                            if (get.contains("ITEMS." + names + ".MMO_TYPE") && get.contains("ITEMS." + names + ".MMO_ID")) {
                                if (get.getInt("ITEMS." + names + ".SLOT") == slot) {
                                    if (get.getInt("ITEMS." + names + ".SELL_PRICE.COST") <= 0) {
                                        sendPlayerMessage(p, Files.getLanguage().getString("CAN_NOT_SELL"));
                                        return;
                                    }
                                    if (get.getInt("ITEMS." + names + ".SELL_PRICE.COST") > 0) {
                                        Debug.sell.add(p);
                                        Debug.name.put(p, shop.getName());
                                        Debug.item_type.put(p, get.getString("ITEMS." + names + ".MMO_TYPE"));
                                        Debug.item_id.put(p, get.getString("ITEMS." + names + ".MMO_ID"));
                                        p.closeInventory();
                                        sendPlayerMessage(p, Files.getLanguage().getString("CHAT_AMOUNT"));
                                    }
                                }
                            }
                        }
                    }
                    if (e.getClick() == ClickType.RIGHT) {
                        int slot = e.getSlot();
                        for (String names : Objects.requireNonNull(get.getConfigurationSection("ITEMS")).getKeys(false)) {
                            if (get.contains("ITEMS." + names + ".MMO_TYPE") && get.contains("ITEMS." + names + ".MMO_ID")) {
                                if (get.getInt("ITEMS." + names + ".SLOT") == slot) {
                                    if (get.getInt("ITEMS." + names + ".BUY_PRICE.COST") <= 0) {
                                        sendPlayerMessage(p, Files.getLanguage().getString("CAN_NOT_BUY"));
                                        return;
                                    }
                                    if (get.getInt("ITEMS." + names + ".BUY_PRICE.COST") > 0) {
                                        Debug.buy.add(p);
                                        Debug.name.put(p, shop.getName());
                                        Debug.item_type.put(p, get.getString("ITEMS." + names + ".MMO_TYPE"));
                                        Debug.item_id.put(p, get.getString("ITEMS." + names + ".MMO_ID"));
                                        p.closeInventory();
                                        sendPlayerMessage(p, Files.getLanguage().getString("CHAT_AMOUNT"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}