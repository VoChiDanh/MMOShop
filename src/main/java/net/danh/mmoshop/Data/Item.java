package net.danh.mmoshop.Data;

import me.clip.placeholderapi.PlaceholderAPI;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.danh.mmoshop.File.Shop;
import net.danh.mmoshop.MMOShop;
import net.danh.mmoshop.Manager.Debug;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import static net.danh.litecore.Utils.Chat.sendPlayerMessage;
import static net.danh.mmoshop.File.Files.getLanguage;

public class Item {

    public static int getPlayerAmount(HumanEntity player, ItemStack item) {
        final PlayerInventory inv = player.getInventory();
        final ItemStack[] items = inv.getContents();
        int c = 0;
        for (final ItemStack is : items) {
            if (is != null) {
                if (is.isSimilar(item)) {
                    c += is.getAmount();
                }
            }
        }
        return c;
    }

    public static void removeItems(Player player, ItemStack item, long amount) {
        item = item.clone();
        final PlayerInventory inv = player.getInventory();
        final ItemStack[] items = inv.getContents();
        int c = 0;
        for (int i = 0; i < items.length; ++i) {
            final ItemStack is = items[i];
            if (is != null) {
                if (is.isSimilar(item)) {
                    if (c + is.getAmount() > amount) {
                        final long canDelete = amount - c;
                        is.setAmount((int) (is.getAmount() - canDelete));
                        items[i] = is;
                        break;
                    }
                    c += is.getAmount();
                    items[i] = null;
                }
            }
        }
        inv.setContents(items);
        player.updateInventory();
    }

    public static void sellItem(Player p, String type, String id, Double price, String symbol, List<String> Command, Integer amount, Shop shop) {
        ItemStack item = item(type, id);
        if (item != null) {
            item.setAmount(amount);
        }
        int a = getPlayerAmount(p, item);
        if (a >= amount) {
            if (item != null) {
                removeItems(p, item, amount);
            }
            ExecuteCommand(p, Command, Double.parseDouble(new DecimalFormat("#.###").format(price * amount).replace(",", ".")), amount);
            if (item != null) {
                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("SELL_ITEMS"))
                        .replace("%symbol%", Matcher.quoteReplacement(symbol))
                        .replace("%item%", Objects.requireNonNull(item.getItemMeta()).getDisplayName())
                        .replace("%price%", String.valueOf(new DecimalFormat("#.###").format(price * amount).replace(",", ".")))
                        .replace("%amount%", String.valueOf(amount)));
            } else {
                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("SELL_ITEMS"))
                        .replace("%symbol%", Matcher.quoteReplacement(symbol))
                        .replace("%item%", shop.getConfig().getString("ITEMS." + Debug.item.get(p) + ".DISPLAY", "&bCustom display"))
                        .replace("%price%", String.valueOf(new DecimalFormat("#.###").format(price * amount).replace(",", ".")))
                        .replace("%amount%", String.valueOf(amount)));
            }
        } else {
            if (item != null) {
                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("NOT_ENOUGH_ITEM"))
                        .replace("%symbol%", Matcher.quoteReplacement(symbol))
                        .replace("%item%", Objects.requireNonNull(item.getItemMeta()).getDisplayName())
                        .replace("%amount%", String.valueOf(amount)));
            } else {

                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("NOT_ENOUGH_ITEM"))
                        .replace("%symbol%", Matcher.quoteReplacement(symbol))
                        .replace("%item%", shop.getConfig().getString("ITEMS." + Debug.item.get(p) + ".DISPLAY", "&bCustom display"))
                        .replace("%amount%", String.valueOf(amount)));
            }
        }
    }

    private static boolean checkItems(String type, String id) {
        MMOItem mmoitem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(type), id);
        if (mmoitem == null) {
            return false;
        }
        return mmoitem.newBuilder().build() != null;
    }

    public static ItemStack item(String type, String id) {
        if (checkItems(type, id)) {
            MMOItem mmoitem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(type), id);
            return Objects.requireNonNull(mmoitem).newBuilder().build();
        }
        return null;
    }

    public static Double Cost(Player p, String placeholders) {
        return BigDecimal.valueOf(Long.parseLong(PlaceholderAPI.setPlaceholders(p, placeholders))).doubleValue();
    }

    public static void ExecuteCommand(Player p, List<String> commands, Double cost, int amount) {
        for (String cmd : commands) {
            if (cmd.startsWith("[CMD] ")) {
                String command = PlaceholderAPI.setPlaceholders(p, cmd.replace("[CMD] ", "")
                        .replace("%cost%", String.valueOf(cost))
                        .replace("%amount%", String.valueOf(amount)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        MMOShop.getInstance().getServer().dispatchCommand(MMOShop.getInstance().getServer().getConsoleSender(), command);
                    }
                }.runTask(MMOShop.getInstance());
            }
        }
    }

    public static void buyItem(Player p, String type, String id, Double price, String symbol, List<String> commands, String Placeholder, Integer amount, Shop shop) {
        ItemStack item = item(type, id);
        if (item != null) {
            item.setAmount(amount);
        }
        if (Cost(p, Placeholder) >= (price * amount)) {
            ExecuteCommand(p, commands, (price * amount), amount);
            if (item != null) {
                p.getInventory().addItem(item);
            }
            if (item != null) {
                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("BUY_ITEMS"))
                        .replace("%symbol%", Matcher.quoteReplacement(symbol))
                        .replace("%item%", Objects.requireNonNull(item.getItemMeta()).getDisplayName())
                        .replace("%price%", String.valueOf(price * amount))
                        .replace("%amount%", String.valueOf(amount)));
            } else sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("BUY_ITEMS"))
                    .replace("%symbol%", Matcher.quoteReplacement(symbol))
                    .replace("%item%", shop.getConfig().getString("ITEMS." + Debug.item.get(p) + ".DISPLAY", "&bCustom display"))
                    .replace("%price%", String.valueOf(price * amount))
                    .replace("%amount%", String.valueOf(amount)));
        } else {
            sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("NOT_ENOUGH_MONEY"))
                    .replace("%symbol%", Matcher.quoteReplacement(symbol))
                    .replace("%amount%", String.valueOf(amount)));
        }
    }

}
