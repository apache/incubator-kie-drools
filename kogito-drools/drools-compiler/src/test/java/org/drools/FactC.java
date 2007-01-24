package org.drools;

public class FactC {
    String f1;
    Integer f2;
    Float f3;
    public FactC(String a, Integer b, Float c) {
        f1 = a;
        f2 = b;
        f3 = c;
    }
    public FactC() {

    }
    public String getF1() {
        return f1;
    }
    public void setF1(String s) {
        f1 = s;
    }
    public Integer getF2() {
        return f2;
    }
    public void setF2(Integer i) {
        f2 = i;
    }
    public Float getF3() {
        return f3;
    }
    public void setF3(Float f) {
        f3 = f;
    }

}
