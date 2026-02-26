package com.example.powers;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum PowerType {
    METAL_JAW("Metal Jaw", "Eat any item, even non-food, like a metal crunch hero."),
    SKY_SENTINEL("Sky Sentinel", "Aerial protector with controlled flight and sky mobility."),
    TITAN_GUARD("Titan Guard", "Tank hero that hardens while taking hits."),
    BLAZE_RUNNER("Blaze Runner", "Fire-speed hero who burns through fights."),
    STORM_LANCER("Storm Lancer", "Lightning striker powered by storms and impact."),
    PHASE_STEPPER("Phase Stepper", "Blink-style hero with evasive movement."),
    OCEAN_WARDEN("Ocean Warden", "Water guardian with swim dominance."),
    FROST_KNIGHT("Frost Knight", "Ice-armored fighter with cold resilience."),
    SHADOW_SPECTER("Shadow Specter", "Stealth hero from the dark."),
    SOLAR_MONK("Solar Monk", "Daylight martial master with focused speed."),
    LUNAR_ASSASSIN("Lunar Assassin", "Night duelist with precise burst damage."),
    ARC_PUNCHER("Arc Puncher", "Electrified brawler with charged strikes."),
    EARTHSHAKER("Earthshaker", "Ground-impact bruiser who weaponizes falls."),
    IRON_HEART("Iron Heart", "Endurance hero with steady life sustain."),
    TOXIC_VEIL("Toxic Veil", "Poison-resistant anti-debuff specialist."),
    THORN_GUARDIAN("Thorn Guardian", "Reflective defender punishing attackers."),
    CRYSTAL_ARCHER("Crystal Archer", "Precision hero empowered by crystals."),
    NETHER_REAVER("Nether Reaver", "Infernal raider thriving in heat and nether blocks."),
    AETHER_WING("Aether Wing", "Glide specialist with air control."),
    BULWARK("Bulwark", "Shield-style defender reducing incoming force."),
    WILD_CALLER("Wild Caller", "Nature ally empowered by animals and plants."),
    LIGHT_BRINGER("Light Bringer", "Radiant hero with beacon-like vision and support."),
    VOID_WALKER("Void Walker", "Portal-touched mobility hero."),
    THUNDERCLAP("Thunderclap", "Explosive melee hero with shock damage."),
    GEARMIND("Gearmind", "Rhythm/intellect hero gaining power from repetition."),
    STARFORGED("Starforged", "Cosmic momentum hero using aerial impact."),
    PHOENIX_SOUL("Phoenix Soul", "Rebirth-style fire hero for clutch combat."),
    GRAVITY_KNIGHT("Gravity Knight", "Force manipulator that controls knockback."),
    BEACON_SPIRIT("Beacon Spirit", "Aura hero giving sustained utility buffs."),
    RIFT_BLADE("Rift Blade", "Dimensional fighter with blink-pressure combat.");

    private final String displayName;
    private final String description;

    PowerType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String displayName() {
        return displayName;
    }

    public String description() {
        return description;
    }

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<PowerType> fromInput(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        String normalized = input.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return Arrays.stream(values()).filter(power -> power.name().equals(normalized)).findFirst();
    }
}
