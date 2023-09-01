package org.drools.examples.sudoku;

/**
 * Used in single step solution mode to indicatean emergeny stop
 * due to failure to solve anything.
 */
public class Stepping {
    private boolean emergency;

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }
}
