package org.drools.quarkus.test;

public class ProbeCounter {
    private int total = 0;

    public void setTotal(final int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void addValue() {
        total += 1;
        synchronized (this) {
            if (total == 10) {
                notify();
            }
        }
    }
}
