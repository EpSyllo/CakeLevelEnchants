package dev.cakestudio.cakelevelenchants.config;

import java.util.Map;

public record EnchantmentConfig(boolean enabled, Map<String, EnchantSetting> enchantments) {
}