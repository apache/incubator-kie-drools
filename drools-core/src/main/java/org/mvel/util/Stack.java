package org.mvel.util;

public interface Stack {
    public boolean isEmpty();
    public Object peek();

    public void push(Object obj);
    public Object pushAndPeek(Object obj);
    public void push(Object obj1, Object obj2);
    public void push(Object obj1, Object obj2, Object obj3);

    public Object pop();
    public void discard();
    public void clear();
    public int size();

    public void showStack();
}
