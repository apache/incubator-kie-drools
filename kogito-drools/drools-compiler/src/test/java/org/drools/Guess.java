package org.drools;

import java.io.Serializable;

public class Guess implements Serializable {

    private Integer value;

    public void setValue(final Integer guess) {
        this.value = guess;
    }

    public Integer getValue() {
        return this.value;
    }

}
