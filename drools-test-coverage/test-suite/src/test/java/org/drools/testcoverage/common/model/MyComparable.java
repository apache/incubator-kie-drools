package org.drools.testcoverage.common.model;

public class MyComparable implements Comparable<MyComparable> {

    private String strValue;
    private Integer intValue;

    public static final MyComparable ABC = new MyComparable("ABC", 1);
    public static final MyComparable DEF = new MyComparable("DEF", 1);
    public static final MyComparable GHI = new MyComparable("GHI", 1);
    public static final MyComparable JKL = new MyComparable("JKL", 1);
    public static final MyComparable MNO = new MyComparable("MNO", 1);
    public static final MyComparable PQR = new MyComparable("PQR", 1);

    public MyComparable() {
    }

    public MyComparable(String strValue, Integer intValue) {
        this.strValue = strValue;
        this.intValue = intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    @Override
    public int compareTo(MyComparable o) {
        MyComparable other = o;
        int result = this.strValue.compareTo(other.strValue);
        if (result != 0) {
            return result;
        }
        return this.intValue.compareTo(other.intValue);
    }
}
