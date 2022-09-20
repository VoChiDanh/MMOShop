package net.danh.mmoshop.Manager;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.danh.dcore.Utils.Chat;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.MMOShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static net.danh.dcore.Utils.Items.Lore;
import static net.danh.dcore.Utils.Items.makeItem;

public class Shops {
    public static void loadShop() {
        Files.getConfig().getStringList("SHOP").forEach(name -> {
            Shop shop = new Shop(name);
            shop.save();
            shop.load();
        });
        MMOShop.getInstance().getLogger().log(Level.INFO, "Loaded " + Files.getConfig().getStringList("SHOP").size() + " shop(s)");
    }

    public static void openShop(Player p, Shop shop) {
        Debug.playerShopHashMap.put(p, shop);
        FileConfiguration get = shop.getConfig();
        String name = Chat.colorize(Objects.requireNonNull(get.getString("NAME")));
        int size = get.getInt("SIZE") * 9;
        Inventory inv = Bukkit.createInventory(p, size, name);
        for (String item_name : Objects.requireNonNull(get.getConfigurationSection("ITEMS")).getKeys(false)) {
            if (get.contains("ITEMS." + item_name + ".MATERIAL")) {
                ItemStack item = makeItem(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".MATERIAL")))), Short.parseShort("0"), 1, get.getBoolean("ITEMS." + item_name + ".GLOW"), get.getBoolean("ITEMS." + item_name + ".HIDE_FLAG"), false, Objects.requireNonNull(get.getString("ITEMS." + item_name + ".NAME")), get.getStringList("ITEMS." + item_name + ".LORE"));
                if (get.contains("ITEMS." + item_name + ".SLOT")) {
                    int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                    inv.setItem(slot, item);
                }
                if (get.contains("ITEMS." + item_name + ".SLOTS")) {
                    for (Integer slots : get.getIntegerList("ITEMS." + item_name + ".SLOTS")) {
                        inv.setItem(slots, item);
                    }
                }
            }
            if (get.contains("ITEMS." + item_name + ".MMO_TYPE") && get.contains("ITEMS." + item_name + ".MMO_ID")) {
                MMOItem mmoitem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(get.getString("ITEMS." + item_name + ".MMO_TYPE")), get.getString("ITEMS." + item_name + ".MMO_ID"));
                if (mmoitem == null) {
                    return;
                }
                ItemStack item = mmoitem.newBuilder().build();
                if (item == null) {
                    return;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    return;
                }
                List<String> lore = meta.getLore();
                List<String> lore_item = Lore(Files.getConfig().getStringList("LORE"));
                if (get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST") > 0d && get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST") > 0d) {
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST"))).replaceAll("%buy%", String.valueOf(get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST")))).collect(Collectors.toList());
                }
                if (get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST") <= 0d && get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST") > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%sell%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%buy%", String.valueOf(get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST")))).collect(Collectors.toList());
                }
                if (get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST") <= 0d && get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST") > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%buy%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST")))).collect(Collectors.toList());
                }
                if (lore != null) {
                    lore.addAll(lore_item);
                    meta.setLore(Lore(lore));
                } else {
                    meta.setLore(Lore(lore_item));
                }
                item.setItemMeta(meta);
                int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                inv.setItem(slot, item);
            }
        }
        p.openInventory(inv);
    }

    public static Inventory getShop(Player p, Shop shop) {
        FileConfiguration get = shop.getConfig();
        String name = Chat.colorize(Objects.requireNonNull(get.getString("NAME")));
        int size = get.getInt("SIZE") * 9;
        Inventory inv = Bukkit.createInventory(p, size, name);
        for (String item_name : Objects.requireNonNull(get.getConfigurationSection("ITEMS")).getKeys(false)) {
            if (get.contains("ITEMS." + item_name + ".MATERIAL")) {
                ItemStack item = makeItem(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".MATERIAL")))), Short.parseShort("0"), 1, get.getBoolean("ITEMS." + item_name + ".GLOW"), get.getBoolean("ITEMS." + item_name + ".HIDE_FLAG"), false, Objects.requireNonNull(get.getString("ITEMS." + item_name + ".NAME")), get.getStringList("ITEMS." + item_name + ".LORE"));
                if (get.contains("ITEMS." + item_name + ".SLOT")) {
                    int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                    inv.setItem(slot, item);
                }
                if (get.contains("ITEMS." + item_name + ".SLOTS")) {
                    for (Integer slots : get.getIntegerList("ITEMS." + item_name + ".SLOTS")) {
                        inv.setItem(slots, item);
                    }
                }
            }
            if (get.contains("ITEMS." + item_name + ".MMO_TYPE") && get.contains("ITEMS." + item_name + ".MMO_ID")) {
                MMOItem mmoitem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(get.getString("ITEMS." + item_name + ".MMO_TYPE")), get.getString("ITEMS." + item_name + ".MMO_ID"));
                if (mmoitem == null) {
                    return null;
                }
                ItemStack item = mmoitem.newBuilder().build();
                if (item == null) {
                    return null;
                }
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    return null;
                }
                List<String> lore = meta.getLore();
                List<String> lore_item = Lore(Files.getConfig().getStringList("LORE"));
                if (get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST") > 0d && get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST") > 0d) {
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST"))).replaceAll("%buy%", String.valueOf(get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST")))).collect(Collectors.toList());
                }
                if (get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST") <= 0d && get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST") > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%sell%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%buy%", String.valueOf(get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST")))).collect(Collectors.toList());
                }
                if (get.getDouble("ITEMS." + item_name + ".BUY_PRICE.COST") <= 0d && get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST") > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%buy%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(get.getDouble("ITEMS." + item_name + ".SELL_PRICE.COST")))).collect(Collectors.toList());
                }
                if (lore != null) {
                    lore.addAll(lore_item);
                    meta.setLore(Lore(lore));
                } else {
                    meta.setLore(Lore(lore_item));
                }
                item.setItemMeta(meta);
                int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                inv.setItem(slot, item);
            }
        }
        return inv;
    }
}
