package org.drools;

public class FactC {
    String  f1;
    Integer f2;
    Float   f3;

    public FactC(final String a,
                 final Integer b,
                 final Float c) {
        this.f1 = a;
        this.f2 = b;
        this.f3 = c;
    }

    public FactC() {

    }

    public FactC( String f1 ) {
        this.f1 = f1;
    }

    public String getF1() {
        return this.f1;
    }

    public void setF1(final String s) {
        this.f1 = s;
    }

    public Integer getF2() {
        return this.f2;
    }

    public void setF2(final Integer i) {
        this.f2 = i;
    }

    public Float getF3() {
        return this.f3;
    }

    public void setF3(final Float f) {
        this.f3 = f;
    }

}
