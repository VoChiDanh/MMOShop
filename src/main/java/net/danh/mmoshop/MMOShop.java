package net.danh.mmoshop;

import net.danh.mmoshop.Commands.CMD;
import net.danh.mmoshop.Events.Chat;
import net.danh.mmoshop.Events.Inventory;
import net.danh.mmoshop.File.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static net.danh.mmoshop.Manager.Shops.loadShop;

public final class MMOShop extends JavaPlugin {

    private static MMOShop instance;

    public static MMOShop getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {
        instance = this;
        new CMD(this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new Inventory(), this);
        Files.create();
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
