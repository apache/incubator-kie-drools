package org.drools;

import java.util.Random;

public class RandomNumber {
    private int randomNumber;

    public void begin() {
        this.randomNumber = new Random().nextInt( 100 );
    }

    public void setValue(final int value) {
        this.randomNumber = value;
    }

    public int getValue() {
        return this.randomNumber;
    }

}
