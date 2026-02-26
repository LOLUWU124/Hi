package com.example.powersmod;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.network.ServerPlayerEntity;

public class PowerManager {
    private final Map<UUID, EnumMap<PowerType, PowerState>> data = new HashMap<>();

    private EnumMap<PowerType, PowerState> mapFor(UUID id) {
        return data.computeIfAbsent(id, k -> new EnumMap<>(PowerType.class));
    }

    public boolean add(ServerPlayerEntity player, PowerType type) {
        var map = mapFor(player.getUuid());
        if (map.containsKey(type)) return false;
        map.put(type, new PowerState());
        return true;
    }

    public boolean remove(ServerPlayerEntity player, PowerType type) {
        return mapFor(player.getUuid()).remove(type) != null;
    }

    public boolean awaken(ServerPlayerEntity player, PowerType type) {
        var state = mapFor(player.getUuid()).get(type);
        return state != null && state.tryAwaken();
    }

    public void addMastery(ServerPlayerEntity player, PowerType type, double amount) {
        var state = mapFor(player.getUuid()).get(type);
        if (state != null) state.addMastery(amount * 0.55);
    }

    public String summary(ServerPlayerEntity player) {
        var map = mapFor(player.getUuid());
        if (map.isEmpty()) return "No powers.";
        StringBuilder sb = new StringBuilder("Powers: ");
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(" | ");
            first = false;
            sb.append(e.getKey().key()).append(" ").append(String.format("%.1f%%", e.getValue().mastery()));
            if (e.getValue().awakened()) sb.append(" (Awakened)");
        }
        return sb.toString();
    }
}
