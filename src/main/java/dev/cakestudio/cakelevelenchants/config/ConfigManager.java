package dev.cakestudio.cakelevelenchants.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class ConfigManager {

    private final JavaPlugin p;
    private FileConfiguration cfg;
    private EnchantmentConfig ec;
    private AnvilConfig ac;

    public ConfigManager(JavaPlugin p) {
        this.p = p;
        reload();
    }

    public void reload() {
        p.saveDefaultConfig();
        p.reloadConfig();
        cfg = p.getConfig();

        Map<String, EnchantSetting> m = new HashMap<>();
        ConfigurationSection sec = cfg.getConfigurationSection("enchantments.list");
        if (sec == null) {
            sec = cfg.getConfigurationSection("enchantments.levels");
        }

        if (sec != null) {
            for (String k : sec.getKeys(false)) {
                int lvl;
                int w = -1;
                int cost = -1;

                if (sec.isConfigurationSection(k)) {
                    ConfigurationSection sub = sec.getConfigurationSection(k);
                    lvl = sub.getInt("max-level", sub.getInt("level"));
                    w = sub.getInt("weight");
                    cost = sub.getInt("anvil-cost");
                } else {
                    lvl = sec.getInt(k);
                }

                if (lvl > 0) {
                    m.put(k.toLowerCase(Locale.ROOT), new EnchantSetting(lvl, w, cost));
                }
            }
        }

        ec = new EnchantmentConfig(
                cfg.getBoolean("enchantments.enabled"),
                Map.copyOf(m)
        );

        ConfigurationSection aSec = cfg.getConfigurationSection("anvil");
        if (aSec != null) {
            ac = new AnvilConfig(
                    aSec.getBoolean("remove-too-expensive"),
                    aSec.getInt("cost-cap")
            );
        } else {
            ac = new AnvilConfig(true, 39);
        }
    }
}