package org.drools;

public class FactA {
    String  field1;
    Integer field2;
    Float   field3;

    public FactA() {
    }

    public FactA(final String f1,
                 final Integer f2,
                 final Float f3) {
        this.field1 = f1;
        this.field2 = f2;
        this.field3 = f3;
    }

    public String getField1() {
        return this.field1;
    }

    public void setField1(final String s) {
        this.field1 = s;
    }

    public Integer getField2() {
        return this.field2;
    }

    public void setField2(final Integer i) {
        this.field2 = i;
    }

    public Float getField3() {
        return this.field3;
    }

    public void setField3(final Float f) {
        this.field3 = f;
    }

}
