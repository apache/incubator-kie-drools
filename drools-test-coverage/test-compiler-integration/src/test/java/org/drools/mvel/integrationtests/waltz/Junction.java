package org.drools.mvel.integrationtests.waltz;


public class Junction {
    public static String TEE   = "tee";

    public static String FORK  = "fork";

    public static String ARROW = "arrow";

    public static String L     = "L";

    private int          p1;

    private int          p2;

    private int          p3;

    private int          basePoint;

    private String       type;

    public Junction() {

    }

    public Junction(final int p1,
                    final int p2,
                    final int p3,
                    final int basePoint,
                    final String type) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.basePoint = basePoint;
        this.type = type;
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
        return "{Junction p1=" + this.p1 + ", p2=" + this.p2 + ", p3=" + this.p3 + ", basePoint=" + this.basePoint + ", type=" + this.type + "}";
    }

    public int getBasePoint() {
        return this.basePoint;
    }

    public void setBasePoint(final int basePoint) {
        this.basePoint = basePoint;
    }

    public int getP3() {
        return this.p3;
    }

    public void setP3(final int p3) {
        this.p3 = p3;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
