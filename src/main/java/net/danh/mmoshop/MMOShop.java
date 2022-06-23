package net.danh.mmoshop;

import net.danh.dcore.Utils.File;
import net.danh.mmoshop.Commands.CMD;
import net.danh.mmoshop.Events.Chat;
import net.danh.mmoshop.Events.Inventory;
import net.danh.mmoshop.File.Files;
import net.danh.mmoshop.File.Shop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MMOShop extends JavaPlugin {

    private static MMOShop instance;
    private static Economy econ;

    public static Economy getEconomy() {
        return econ;
    }

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

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new CMD(this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new Inventory(), this);
        Files.create();
        File.updateFile(MMOShop.getInstance(), Files.getLanguage(), "language.yml");
        loadShop();
    }

    @Override
    public void onDisable() {
        Files.save();
    }
}
