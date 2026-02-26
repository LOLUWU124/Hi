package com.example.powers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PowerManager {
    private final PowerPlugin plugin;
    private final Map<UUID, EnumMap<PowerType, PowerState>> playerPowers = new HashMap<>();

    public PowerManager(PowerPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        playerPowers.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection root = config.getConfigurationSection("players");
        if (root == null) {
            return;
        }

        for (String uuidText : root.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidText);
            } catch (IllegalArgumentException e) {
                continue;
            }
            ConfigurationSection powersSection = root.getConfigurationSection(uuidText + ".powers");
            if (powersSection == null) {
                continue;
            }
            EnumMap<PowerType, PowerState> map = new EnumMap<>(PowerType.class);
            for (String key : powersSection.getKeys(false)) {
                PowerType.fromInput(key).ifPresent(type -> {
                    PowerState state = new PowerState();
                    state.setMastery(powersSection.getDouble(key + ".mastery", 0));
                    state.setAwakened(powersSection.getBoolean(key + ".awakened", false));
                    map.put(type, state);
                });
            }
            if (!map.isEmpty()) {
                playerPowers.put(uuid, map);
            }
        }
    }

    public void save() {
        plugin.getConfig().set("players", null);
        for (Map.Entry<UUID, EnumMap<PowerType, PowerState>> entry : playerPowers.entrySet()) {
            String base = "players." + entry.getKey() + ".powers";
            for (Map.Entry<PowerType, PowerState> powerEntry : entry.getValue().entrySet()) {
                String path = base + "." + powerEntry.getKey().key();
                plugin.getConfig().set(path + ".mastery", powerEntry.getValue().getMastery());
                plugin.getConfig().set(path + ".awakened", powerEntry.getValue().isAwakened());
            }
        }
        plugin.saveConfig();
    }

    private EnumMap<PowerType, PowerState> mapFor(UUID uuid) {
        return playerPowers.computeIfAbsent(uuid, id -> new EnumMap<>(PowerType.class));
    }

    public boolean hasPower(UUID uuid, PowerType type) {
        return mapFor(uuid).containsKey(type);
    }

    public boolean addPower(Player player, PowerType type) {
        EnumMap<PowerType, PowerState> powers = mapFor(player.getUniqueId());
        if (powers.containsKey(type)) {
            return false;
        }
        powers.put(type, new PowerState());
        return true;
    }

    public boolean removePower(Player player, PowerType type) {
        EnumMap<PowerType, PowerState> powers = mapFor(player.getUniqueId());
        return powers.remove(type) != null;
    }

    public boolean awaken(Player player, PowerType type) {
        PowerState state = mapFor(player.getUniqueId()).get(type);
        if (state == null || !state.canAwaken() || state.isAwakened()) {
            return false;
        }
        state.setAwakened(true);
        return true;
    }

    public void addMastery(Player player, PowerType type, double amount) {
        PowerState state = mapFor(player.getUniqueId()).get(type);
        if (state == null) {
            return;
        }
        double before = state.getMastery();
        state.setMastery(before + amount);
        if (before < 100 && state.getMastery() >= 100) {
            player.sendMessage(ChatColor.GOLD + "Your " + type.displayName() + " mastery reached 100%! Use /power awaken " + type.key() + " " + player.getName());
        }
    }

    public PowerState getState(Player player, PowerType type) {
        return mapFor(player.getUniqueId()).get(type);
    }

    public String summary(Player player) {
        EnumMap<PowerType, PowerState> powers = mapFor(player.getUniqueId());
        if (powers.isEmpty()) {
            return ChatColor.GRAY + "No powers.";
        }
        StringBuilder sb = new StringBuilder(ChatColor.AQUA + "Powers: ");
        boolean first = true;
        for (Map.Entry<PowerType, PowerState> entry : powers.entrySet()) {
            if (!first) {
                sb.append(ChatColor.GRAY).append(" | ");
            }
            first = false;
            sb.append(ChatColor.YELLOW)
              .append(entry.getKey().displayName())
              .append(ChatColor.WHITE)
              .append(" ")
              .append(String.format("%.1f%%", entry.getValue().getMastery()));
            if (entry.getValue().isAwakened()) {
                sb.append(ChatColor.LIGHT_PURPLE).append(" (Awakened)");
            }
        }
        return sb.toString();
    }

    public void startPassiveTask(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyAwakenedPassives(player);
            }
        }, 20L, 40L);
    }

    private void applyAwakenedPassives(Player player) {
        applyIfAwakened(player, PowerType.WIND_STEP, PotionEffectType.SPEED, 1);
        applyIfAwakened(player, PowerType.STONE_SKIN, PotionEffectType.RESISTANCE, 1);
        applyIfAwakened(player, PowerType.TIDE_CALLER, PotionEffectType.DOLPHINS_GRACE, 1);
        applyIfAwakened(player, PowerType.SHADOW_VEIL, PotionEffectType.INVISIBILITY, 0);
        applyIfAwakened(player, PowerType.MOONLIT_HUNTER, PotionEffectType.NIGHT_VISION, 0);
        applyIfAwakened(player, PowerType.VOID_GLIDE, PotionEffectType.SLOW_FALLING, 0);
        applyIfAwakened(player, PowerType.FLAME_HEART, PotionEffectType.FIRE_RESISTANCE, 0);
        applyIfAwakened(player, PowerType.SUNFORGE, PotionEffectType.HASTE, 1);
        applyIfAwakened(player, PowerType.ARCANE_ECHO, PotionEffectType.LUCK, 0);
        applyIfAwakened(player, PowerType.BEAST_BOND, PotionEffectType.HERO_OF_THE_VILLAGE, 0);
        applyIfAwakened(player, PowerType.GRAVITY_WELL, PotionEffectType.JUMP_BOOST, 1);
        applyIfAwakened(player, PowerType.STORM_EYE, PotionEffectType.WATER_BREATHING, 0);
        applyIfAwakened(player, PowerType.BLOODRUSH, PotionEffectType.STRENGTH, 0);
        applyIfAwakened(player, PowerType.AETHER_SHIFT, PotionEffectType.SPEED, 0);
        applyIfAwakened(player, PowerType.IRON_STOMACH, PotionEffectType.SATURATION, 0);
    }

    private void applyIfAwakened(Player player, PowerType type, PotionEffectType effectType, int amplifier) {
        PowerState state = getState(player, type);
        if (state != null && state.isAwakened()) {
            player.addPotionEffect(new PotionEffect(effectType, 80, amplifier, true, false, true));
        }
    }
}
