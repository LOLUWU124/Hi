package com.hi.races;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum Race {
    NONE("none"),
    GILLER("giller"),
    NIGHTLING("nightling"),
    METAL_JAW("metaljaw"),
    FIREBORN("fireborn"),
    SWIFTLING("swiftling"),
    PHANTOM("phantom");

    private final String id;

    Race(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static Optional<Race> fromString(String value) {
        String normalized = normalize(value);
        return Arrays.stream(values()).filter(race -> race.id.equals(normalized)).findFirst();
    }

    public static Race autoCorrect(String value) {
        String normalized = normalize(value);
        Race best = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Race race : values()) {
            if (race == NONE) {
                continue;
            }
            int distance = levenshtein(normalized, race.id);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = race;
            }
        }

        return bestDistance <= 3 ? best : null;
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "");
    }

    private static int levenshtein(String a, String b) {
        int[] previous = new int[b.length() + 1];
        int[] current = new int[b.length() + 1];

        for (int j = 0; j <= b.length(); j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(
                        Math.min(current[j - 1] + 1, previous[j] + 1),
                        previous[j - 1] + cost
                );
            }
            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[b.length()];
    }
}
