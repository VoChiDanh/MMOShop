package net.danh.mmoshop;

import net.danh.dcore.Utils.File;
import net.danh.mmoshop.Commands.CMD;
import net.danh.mmoshop.Events.Chat;
import net.danh.mmoshop.Events.Inventory;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MMOShop extends JavaPlugin {

    private static MMOShop instance;

    public static MMOShop getInstance() {
        return instance;
    }

    public static void loadShop() {
        for (String name : Files.getConfig().getStringList("SHOP")) {
            Shop shop = new Shop(name);
            shop.save();
            shop.load();
        }
    }


    @Override
    public void onEnable() {
        instance = this;
        new CMD(this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new Inventory(), this);
        Files.create();
        File.updateFile(MMOShop.getInstance(), Files.getLanguage(), "language.yml");
        loadShop();
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
        }
        Files.save();
    }
}
