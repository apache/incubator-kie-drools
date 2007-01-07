package org.mvel.util;

public final class ShadowingExecutionStack implements Stack {
    private StackElement element;
    private StackElement shadowElement;

    public ShadowingExecutionStack() {
    }


    private int size = 0;

    public boolean isEmpty() {
        return element == null;
    }

    public void push(Object o) {
        size++;
        element = new StackElement(element, o);
        shadowElement = new StackElement(shadowElement, o);
    }


    public Object pushAndPeek(Object o) {
        size++;
        element = new StackElement(element, o);
        shadowElement = new StackElement(shadowElement, o);
        return o;
    }


    public void discard() {

    }

    public void push(Object obj1, Object obj2) {

    }

    public void push(Object obj1, Object obj2, Object obj3) {

    }

    public Object peek() {
        if (size == 0) return null;
        else return element.value;
    }

    public Object pop() {
        if (size == 0) return null;
        size--;
        Object el = element.value;
        element = element.next;
        return el;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        element = null;
    }

    public void showStack() {
        Object el = element;
        do {
            System.out.println("->" + ((StackElement) el).value);
        }
        while ((el = element.next) != null);
    }
}
