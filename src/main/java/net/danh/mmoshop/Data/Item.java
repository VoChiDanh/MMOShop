package net.danh.mmoshop.Data;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.danh.mmoshop.MMOShop;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;

import static net.danh.dcore.Utils.Player.sendPlayerMessage;
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

    public static void sellItem(Player p, String type, String id, Integer price, Integer amount) {
        ItemStack item = item(type, id);
        if (item == null) {
            return;
        }
        item.setAmount(amount);
        int a = getPlayerAmount(p, item);
        if (a >= amount) {
            removeItems(p, item, amount);
            EconomyResponse e = MMOShop.getEconomy().depositPlayer(p, price * amount);
            if (e.transactionSuccess()) {
                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("SELL_ITEMS")).replaceAll("%item%", Objects.requireNonNull(item.getItemMeta()).getDisplayName()).replaceAll("%price%", String.format("%,d", price * amount)).replaceAll("%amount%", String.format("%,d", amount)));
            }
        } else {
            sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("NOT_ENOUGH_ITEM")).replaceAll("%item%", Objects.requireNonNull(item.getItemMeta()).getDisplayName()));
        }
    }

    private static boolean checkItems(String type, String id) {
        MMOItem mmoitem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(type), id);
        if (mmoitem == null) {
            return false;
        }
        return mmoitem.newBuilder().build() != null;
    }

    private static ItemStack item(String type, String id) {
        if (checkItems(type, id)) {
            MMOItem mmoitem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(type), id);
            return Objects.requireNonNull(mmoitem).newBuilder().build();
        }
        return null;
    }

    public static void buyItem(Player p, String type, String id, Integer price, Integer amount) {
        ItemStack item = item(type, id);
        if (item == null) {
            return;
        }
        item.setAmount(amount);
        if (MMOShop.getEconomy().getBalance(p) >= price * amount) {
            EconomyResponse e = MMOShop.getEconomy().withdrawPlayer(p, price * amount);
            p.getInventory().addItem(item);
            if (e.transactionSuccess()) {
                sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("BUY_ITEMS")).replaceAll("%item%", Objects.requireNonNull(item.getItemMeta()).getDisplayName()).replaceAll("%price%", String.format("%,d", price * amount)).replaceAll("%amount%", String.format("%,d", amount)));
            }
        } else {
            sendPlayerMessage(p, Objects.requireNonNull(getLanguage().getString("NOT_ENOUGH_MONEY")).replaceAll("%money%", String.format("%,d", price * amount)));
        }
    }

}
