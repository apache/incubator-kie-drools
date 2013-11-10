package org.drools.games.numberguess;

public class Guess {
    private int value;

    public Guess(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Guess{" +
               "value=" + value +
               '}';
    }
}
