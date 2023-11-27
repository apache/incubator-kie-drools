package org.drools.drlonyaml.integration.tests;

public class Message {

    private final String text;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getSize() {
        return text.length();
    }
}
