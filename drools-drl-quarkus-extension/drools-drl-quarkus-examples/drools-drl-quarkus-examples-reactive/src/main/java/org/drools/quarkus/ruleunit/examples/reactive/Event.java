package org.drools.quarkus.ruleunit.examples.reactive;

public class Event {

    private String type;
    private int value;

    public Event() {
    }

    public Event(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Event [type=" + type + ", value=" + value + "]";
    }

}
