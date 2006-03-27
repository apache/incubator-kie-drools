package org.drools.util.asm;

public class BaseBean {

    private String text = "hola";
    private int number = 42;
    
    public String getText() {
        return text;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    
}
