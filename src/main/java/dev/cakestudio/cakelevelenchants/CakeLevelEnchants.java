package dev.cakestudio.cakelevelenchants;

import dev.cakestudio.cakelevelenchants.config.ConfigManager;
import dev.cakestudio.cakelevelenchants.listener.AnvilListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class CakeLevelEnchants extends JavaPlugin {

    @Getter
    private ConfigManager cm;

    @Override
    public void onEnable() {
        cm = new ConfigManager(this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}