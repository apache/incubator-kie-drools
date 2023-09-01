package org.drools.core.util.asm;

public class BaseBean {

    private final String text   = "hola";
    private int          number = 42;

    public String getText() {
        return this.text;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

}
