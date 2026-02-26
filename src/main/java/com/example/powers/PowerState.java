package com.example.powers;

public class PowerState {
    private double mastery;
    private boolean awakened;

    public double getMastery() {
        return mastery;
    }

    public void setMastery(double mastery) {
        this.mastery = Math.max(0, Math.min(100, mastery));
    }

    public boolean isAwakened() {
        return awakened;
    }

    public void setAwakened(boolean awakened) {
        this.awakened = awakened;
    }

    public boolean canAwaken() {
        return mastery >= 100.0;
    }
}
