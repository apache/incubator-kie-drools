package org.drools.games.numberguess;

public class GameRules {
    private int maxRange;
    private int allowedGuesses;

    public GameRules(int maxRange,
                     int allowedGuesses) {
        this.maxRange = maxRange;
        this.allowedGuesses = allowedGuesses;
    }

    public int getAllowedGuesses() {
        return allowedGuesses;
    }

    public int getMaxRange() {
        return maxRange;
    }

    @Override
    public String toString() {
        return "GameRules{" +
               "maxRange=" + maxRange +
               ", allowedGuesses=" + allowedGuesses +
               '}';
    }
}
