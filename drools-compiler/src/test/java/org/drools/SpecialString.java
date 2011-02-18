package org.drools;

public class SpecialString {
    private String text;

    public SpecialString(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return (getText() + "[" + super.toString() + "]");
    }
}
