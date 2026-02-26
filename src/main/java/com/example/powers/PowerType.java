package com.example.powers;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum PowerType {
    METAL_JAW("Metal Jaw", "Base: consume any item for mastery. Awakening: gain stronger nourishment and regen bursts."),
    WIND_STEP("Wind Step", "Base: gain mastery by sprinting. Awakening: keep swift movement with controlled aerial hops."),
    STONE_SKIN("Stone Skin", "Base: gain mastery while tanking hits. Awakening: consistent damage reduction and sturdy defense."),
    FLAME_HEART("Flame Heart", "Base: gain mastery through combat. Awakening: ignite enemies and resist flames."),
    TIDE_CALLER("Tide Caller", "Base: gain mastery by swimming. Awakening: superior underwater control and breathing."),
    SHADOW_VEIL("Shadow Veil", "Base: mastery from sneaking and stealth. Awakening: maintain invisibility and evasive play."),
    THUNDER_PULSE("Thunder Pulse", "Base: mastery from melee pressure. Awakening: occasional lightning-empowered strikes."),
    NATURE_TOUCH("Nature Touch", "Base: mastery from harvesting natural blocks. Awakening: faster growth utility and regeneration moments."),
    FROST_BLOOM("Frost Bloom", "Base: mastery while moving on frozen terrain. Awakening: cold resistance and controlled slowing aura."),
    SUNFORGE("Sunforge", "Base: mastery from daylight mining. Awakening: efficient gathering and daytime momentum."),
    MOONLIT_HUNTER("Moonlit Hunter", "Base: mastery from night combat. Awakening: sharper senses and stronger nocturnal combat."),
    ARCANE_ECHO("Arcane Echo", "Base: convert XP gains into mastery. Awakening: better utility luck and magical sustain."),
    BEAST_BOND("Beast Bond", "Base: mastery through interacting with animals. Awakening: supportive regeneration and village affinity."),
    VOID_GLIDE("Void Glide", "Base: mastery from surviving falls and gliding. Awakening: reliable fall control and drift safety."),
    CRYSTAL_MIND("Crystal Mind", "Base: mastery from crystal-rich blocks. Awakening: clarity buffs and burst haste windows."),
    GRAVITY_WELL("Gravity Well", "Base: mastery from pressure combat. Awakening: moderated knockback control and jump control."),
    STORM_EYE("Storm Eye", "Base: mastery during storms and battle. Awakening: weather-adapted breathing and charged bursts."),
    BLOODRUSH("Bloodrush", "Base: mastery from aggressive combat flow. Awakening: measured low-health damage boost."),
    AETHER_SHIFT("Aether Shift", "Base: mastery while gliding and surviving falls. Awakening: movement precision and mobility windows."),
    IRON_STOMACH("Iron Stomach", "Base: mastery from consuming items and meals. Awakening: steady saturation and absorption control.");

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
