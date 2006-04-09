package org.drools.util.asm;

public class TestBean {

    private String something;
    private int number;
    private boolean blah;
    
    
    public boolean isBlah() {
        return blah;
    }
    public void setBlah(boolean blah) {
        this.blah = blah;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public String getSomething() {
        return something;
    }
    public void setSomething(String something) {
        this.something = something;
    }
    
    public String fooBar() {
        return "fooBar";
    }
    
    public long getLongField() {
        return 424242;
    }
    
    public Long getOtherLongField() {
        return new Long(42424242);
    }
    
}
