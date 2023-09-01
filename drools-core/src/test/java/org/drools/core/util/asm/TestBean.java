package org.drools.core.util.asm;

public class TestBean {

    private String   something;
    private int      number;
    private boolean  blah;
    private Object[] objArray;

    public boolean isBlah() {
        return this.blah;
    }

    public void setBlah(final boolean blah) {
        this.blah = blah;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public String getSomething() {
        return this.something;
    }

    public void setSomething(final String something) {
        this.something = something;
    }

    public String fooBar() {
        return "fooBar";
    }

    public long getLongField() {
        return 424242;
    }

    public Long getOtherLongField() {
        return new Long( 42424242 );
    }

    public Object[] getObjArray() {
        return this.objArray;
    }

    public void setObjArray(final Object[] objArray) {
        this.objArray = objArray;
    }

}
