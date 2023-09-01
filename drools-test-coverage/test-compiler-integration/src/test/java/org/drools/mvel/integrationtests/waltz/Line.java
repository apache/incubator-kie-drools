package org.drools.mvel.integrationtests.waltz;


public class Line {
    private int p1;

    private int p2;

    public Line() {

    }

    public Line(final int p1,
                final int p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public int getP1() {
        return this.p1;
    }

    public void setP1(final int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return this.p2;
    }

    public void setP2(final int p2) {
        this.p2 = p2;
    }

    public String toString() {
        return "{Line p1=" + this.p1 + ", p2=" + this.p2 + "}";
    }
}
