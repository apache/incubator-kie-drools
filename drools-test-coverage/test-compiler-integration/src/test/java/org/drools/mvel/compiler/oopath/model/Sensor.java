package org.drools.mvel.compiler.oopath.model;

import org.drools.core.phreak.AbstractReactiveObject;

public class Sensor extends AbstractReactiveObject {
    private double value = 0;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        notifyModification();
    }
}
