package org.mvel.util;

public class StackElement {
    public StackElement(StackElement next, Object value) {
        this.next = next;
        this.value = value;
    }

    public StackElement next;
    public Object value;
}
