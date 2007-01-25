package org.drools;

public class FactA {
    String field1;
    Integer field2;
    Float field3;
    
    public FactA() {
    }
    
    public FactA(String f1, Integer f2, Float f3) {
        field1 = f1;
        field2 = f2;
        field3 = f3;
    }
    public String getField1() {
        return field1;
    }
    public void setField1(String s) {
        field1 = s;
    }
    public Integer getField2() {
        return field2;
    }
    public void setField2(Integer i) {
        field2 = i;
    }
    public Float getField3() {
        return field3;
    }
    public void setField3(Float f) {
        field3 = f;
    }

}
