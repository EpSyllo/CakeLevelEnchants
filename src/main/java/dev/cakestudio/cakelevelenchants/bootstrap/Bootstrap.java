package dev.cakestudio.cakelevelenchants.bootstrap;

import dev.cakestudio.cakelevelenchants.config.EnchantSetting;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.event.RegistryEvents;
import lombok.NonNull;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Bootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NonNull BootstrapContext c) {
        Path d = c.getDataDirectory();
        Path f = d.resolve("config.yml");

        if (Files.notExists(f)) {
            try {
                Files.createDirectories(d);
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) {
                        Files.copy(in, f);
                    }
                }
            } catch (IOException e) {
                c.getLogger().error("Не удалось инициализировать config.yml", e);
                return;
            }
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f.toFile());

        if (!cfg.getBoolean("enchantments.enabled", true)) {
            return;
        }

        ConfigurationSection sec = cfg.getConfigurationSection("enchantments.list");
        if (sec == null) {
            sec = cfg.getConfigurationSection("enchantments.levels");
        }

        if (sec == null) {
            c.getLogger().warn("Секция зачарований отсутствует в config.yml");
            return;
        }

        Map<Key, EnchantSetting> map = new HashMap<>();

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

            if (lvl <= 0) continue;

            String s = k.toLowerCase(Locale.ROOT);
            Key key = s.contains(":") ? Key.key(s) : Key.key("minecraft", s);

            map.put(key, new EnchantSetting(lvl, w, cost));
        }

        if (map.isEmpty()) {
            return;
        }

        LifecycleEventManager<BootstrapContext> m = c.getLifecycleManager();

        m.registerEventHandler(RegistryEvents.ENCHANTMENT.entryAdd().newHandler(e -> {
            EnchantSetting s = map.get(e.key().key());
            if (s != null) {
                c.getLogger().info("Применены настройки для {}: {}", e.key().key(), s);
                if (s.maxLevel() > 0) {
                    e.builder().maxLevel(s.maxLevel());
                }
                if (s.weight() >= 1) {
                    e.builder().weight(s.weight());
                }
                if (s.anvilCost() >= 0) {
                    e.builder().anvilCost(s.anvilCost());
                }
            }
        }));
    }
}