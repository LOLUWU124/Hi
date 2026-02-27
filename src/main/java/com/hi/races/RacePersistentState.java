package com.hi.races;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RacePersistentState extends PersistentState {
    private static final String KEY = "hi_races";

    private final Map<UUID, String> raceByPlayer = new HashMap<>();

    public static RacePersistentState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
                RacePersistentState::read,
                RacePersistentState::new,
                KEY
        );
    }

    public void setRace(UUID player, Race race) {
        raceByPlayer.put(player, race.id());
        markDirty();
    }

    public Race getRace(UUID player) {
        return Race.fromString(raceByPlayer.getOrDefault(player, Race.NONE.id())).orElse(Race.NONE);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        NbtList players = new NbtList();
        raceByPlayer.forEach((uuid, race) -> {
            NbtCompound entry = new NbtCompound();
            entry.putUuid("uuid", uuid);
            entry.putString("race", race);
            players.add(entry);
        });
        nbt.put("players", players);
        return nbt;
    }

    private static RacePersistentState read(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        RacePersistentState state = new RacePersistentState();
        NbtList players = nbt.getList("players", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < players.size(); i++) {
            NbtCompound entry = players.getCompound(i);
            if (entry.containsUuid("uuid") && entry.contains("race", NbtElement.STRING_TYPE)) {
                state.raceByPlayer.put(entry.getUuid("uuid"), entry.getString("race"));
            }
        }
        return state;
    }
}
