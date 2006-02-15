package org.drools.lang.descr;

public class ConsequenceDescr extends PatternDescr {
    private String text;

    public ConsequenceDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
