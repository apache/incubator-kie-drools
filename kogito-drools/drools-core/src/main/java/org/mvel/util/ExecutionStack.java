package org.mvel.util;

import static org.mvel.util.ParseTools.debug;

public class ExecutionStack implements Stack {
    private StackElement element;
    private int size = 0;

    public boolean isEmpty() {
        return element == null;
    }

    public void push(Object o) {
        size++;
        element = new StackElement(element, o);
    }

    public Object pushAndPeek(Object o) {
        size++;
        element = new StackElement(element, o);
        return o;
    }


    public void push(Object obj1, Object obj2) {
        size += 2;
        element = new StackElement(new StackElement(element, obj1), obj2);
    }

    public void push(Object obj1, Object obj2, Object obj3) {
        size += 3;
        element = new StackElement(new StackElement(new StackElement(element, obj1), obj2), obj3);
    }

    public Object peek() {
        if (size == 0) return null;
        else return element.value;
    }

    public Object pop() {
        if (size-- == 0) return null;
        Object el = element.value;
        element = element.next;
        return el;
    }

    public void discard() {
        if (size-- != 0)
            element = element.next;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
        element = null;
    }


    public void showStack() {
        StackElement el = element;
        do {
            System.out.println("->" + el.value);
        }
        while ((el = el.next) != null);
    }
}
