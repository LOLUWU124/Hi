package com.example.powers;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum PowerType {
    METAL_JAW("Metal Jaw", "Eat any item and gain mastery from rarity."),
    WIND_STEP("Wind Step", "Move quickly and master speed by sprinting."),
    STONE_SKIN("Stone Skin", "Become tougher as you endure damage."),
    FLAME_HEART("Flame Heart", "Burn with power and ignite enemies."),
    TIDE_CALLER("Tide Caller", "Grow stronger while swimming."),
    SHADOW_VEIL("Shadow Veil", "Master stealth through sneaking."),
    THUNDER_PULSE("Thunder Pulse", "Build charge by striking foes."),
    NATURE_TOUCH("Nature Touch", "Harvest plants and logs for mastery."),
    FROST_BLOOM("Frost Bloom", "Grow power by traveling frozen lands."),
    SUNFORGE("Sunforge", "Mine by daylight to fill your mastery."),
    MOONLIT_HUNTER("Moonlit Hunter", "Fight at night to improve this power."),
    ARCANE_ECHO("Arcane Echo", "Convert experience into mastery."),
    BEAST_BOND("Beast Bond", "Interact with animals to bond deeper."),
    VOID_GLIDE("Void Glide", "Survive falls and glides to learn control."),
    CRYSTAL_MIND("Crystal Mind", "Shape resources into refined knowledge."),
    GRAVITY_WELL("Gravity Well", "Control knockback and slam enemies."),
    STORM_EYE("Storm Eye", "Harness storms and electric surges."),
    BLOODRUSH("Bloodrush", "Gain momentum while fighting under pressure."),
    AETHER_SHIFT("Aether Shift", "Phase through danger with mobility."),
    IRON_STOMACH("Iron Stomach", "Convert food and scraps into endurance.");

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
