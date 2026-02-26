package com.example.powersmod;

public class PowerState {
    private double mastery;
    private boolean awakened;

    public double mastery() { return mastery; }
    public boolean awakened() { return awakened; }

    public void addMastery(double amount) {
        mastery = Math.max(0, Math.min(100, mastery + amount));
    }

    public boolean tryAwaken() {
        if (awakened || mastery < 100) return false;
        awakened = true;
        return true;
    }
}
