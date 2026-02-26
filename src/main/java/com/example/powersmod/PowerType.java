package com.example.powersmod;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum PowerType {
    METAL_JAW,
    SKY_SENTINEL,
    TITAN_GUARD,
    BLAZE_RUNNER,
    STORM_LANCER,
    PHASE_STEPPER,
    OCEAN_WARDEN,
    FROST_KNIGHT,
    SHADOW_SPECTER,
    SOLAR_MONK,
    LUNAR_ASSASSIN,
    ARC_PUNCHER,
    EARTHSHAKER,
    IRON_HEART,
    TOXIC_VEIL,
    THORN_GUARDIAN,
    CRYSTAL_ARCHER,
    NETHER_REAVER,
    AETHER_WING,
    BULWARK,
    WILD_CALLER,
    LIGHT_BRINGER,
    VOID_WALKER,
    THUNDERCLAP,
    GEARMIND,
    STARFORGED,
    PHOENIX_SOUL,
    GRAVITY_KNIGHT,
    BEACON_SPIRIT,
    RIFT_BLADE;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<PowerType> fromInput(String input) {
        if (input == null || input.isBlank()) return Optional.empty();
        String normalized = input.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return Arrays.stream(values()).filter(v -> v.name().equals(normalized)).findFirst();
    }
}
