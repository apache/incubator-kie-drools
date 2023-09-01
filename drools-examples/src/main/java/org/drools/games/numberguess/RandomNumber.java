package org.drools.games.numberguess;

import java.util.Random;

public class RandomNumber {
    private int randomNumber;

    public RandomNumber(int v) {
        this.randomNumber = new Random().nextInt( v );
    }

    public int getValue() {
        return this.randomNumber;
    }

    @Override
    public String toString() {
        return "RandomNumber{" +
               "randomNumber=" + randomNumber +
               '}';
    }
}
