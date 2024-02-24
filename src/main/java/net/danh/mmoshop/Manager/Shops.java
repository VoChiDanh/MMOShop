package net.danh.mmoshop.Manager;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.danh.litecore.Utils.Chat;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.MMOShop;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
                ItemMeta meta = item.getItemMeta();
                if (meta == null) {
                    return;
                }
                List<String> lore = meta.getLore();
                List<String> lore_item = Chat.colorize(Files.getConfig().getStringList("LORE"));
                if (net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")) > 0d && net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0")) > 0d) {
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")))).replaceAll("%buy%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0"))))).collect(Collectors.toList());
                }
                if (net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")) <= 0d && net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0")) > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%sell%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%buy%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0"))))).collect(Collectors.toList());
                }
                if (net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0")) <= 0d && net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")) > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%buy%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0"))))).collect(Collectors.toList());
                }
                if (lore != null) {
                    lore.addAll(lore_item);
                    meta.setLore(Chat.colorize(lore));
                } else {
                    meta.setLore(Chat.colorize(lore_item));
                }
                item.setItemMeta(meta);
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
                List<String> lore_item = Chat.colorize(Files.getConfig().getStringList("LORE"));
                if (net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")) > 0d && net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0")) > 0d) {
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")))).replaceAll("%buy%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0"))))).collect(Collectors.toList());
                }
                if (net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")) <= 0d && net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0")) > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%sell%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%buy%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0"))))).collect(Collectors.toList());
                }
                if (net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".BUY_PRICE.COST", "0")) <= 0d && net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0")) > 0d) {
                    for (int i = 0; i < lore_item.size(); i++) {
                        if (lore_item.get(i).contains("%buy%")) {
                            lore_item.remove(lore_item.get(i));
                        }
                    }
                    lore_item = lore_item.stream().map(s -> s.replaceAll("%symbol%", Matcher.quoteReplacement(Objects.requireNonNull(get.getString("ITEMS." + item_name + ".SYMBOL")))).replaceAll("%sell%", String.valueOf(net.danh.mmoshop.Events.Chat.calculatorPrice(p, get.getString("ITEMS." + item_name + ".SELL_PRICE.COST", "0"))))).collect(Collectors.toList());
                }
                if (lore != null) {
                    lore.addAll(lore_item);
                    meta.setLore(Chat.colorize(lore));
                } else {
                    meta.setLore(Chat.colorize(lore_item));
                }
                item.setItemMeta(meta);
                int slot = get.getInt("ITEMS." + item_name + ".SLOT");
                inv.setItem(slot, item);
            }
        }
        p.openInventory(inv);
    }

    /**
     * @param material    Material
     * @param data        Data (For legacy version 1.12.x and below)
     * @param amount      int
     * @param glow        true/false
     * @param HideFlag    true/false
     * @param Unbreakable true/false
     * @param name        Item name
     * @param lore        Item lore
     * @return ItemStack
     */
    public static ItemStack makeItem(Material material, Short data, Integer amount, Boolean glow, Boolean HideFlag, Boolean Unbreakable, String name, List<String> lore) {
        ItemStack itemStack;
        if (data >= 0) {
            itemStack = new ItemStack(material, amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta).setDisplayName(Chat.colorize(name));
            if (lore != null) {
                itemMeta.setLore(Chat.colorize(lore));
            }
            if (glow) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (HideFlag) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            }
            if (Unbreakable) {
                itemMeta.setUnbreakable(true);
            }
            itemStack.setItemMeta(itemMeta);
        } else {
            itemStack = new ItemStack(material, amount, data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta).setDisplayName(Chat.colorize(name));
            if (lore != null) {
                itemMeta.setLore(Chat.colorize(lore));
            }
            if (glow) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (HideFlag) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            }
            if (Unbreakable) {
                itemMeta.setUnbreakable(true);
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }

    /**
     * @param material    Material
     * @param data        Data (For legacy version 1.13 below), null if you use 1.13+
     * @param amount      int
     * @param glow        true/false
     * @param HideFlag    true/false
     * @param Unbreakable true/false
     * @param name        Item name
     * @param lore        Item lore
     * @return ItemStack
     */
    public static ItemStack makeItem(Material material, Short data, Integer amount, Boolean glow, Boolean HideFlag, Boolean Unbreakable, String name, String... lore) {
        ItemStack itemStack;
        if (data == null) {
            itemStack = new ItemStack(material, amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta).setDisplayName(Chat.colorize(name));
            if (lore != null) {
                List<String> l = new ArrayList<>();
                for (String lores : lore) {
                    l.add(Chat.colorize(lores));
                }
                itemMeta.setLore(Chat.colorize(l));
            }
            if (glow) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (HideFlag) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            }
            if (Unbreakable) {
                itemMeta.setUnbreakable(true);
            }
            itemStack.setItemMeta(itemMeta);
        } else {
            itemStack = new ItemStack(material, amount, data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta).setDisplayName(Chat.colorize(name));
            if (lore != null) {
                List<String> l = new ArrayList<>();
                for (String lores : lore) {
                    l.add(Chat.colorize(lores));
                }
                itemMeta.setLore(l);
            }
            if (glow) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            if (HideFlag) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
            }
            if (Unbreakable) {
                itemMeta.setUnbreakable(true);
            }
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}