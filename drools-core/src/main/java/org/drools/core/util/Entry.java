package org.drools.core.util;


public interface Entry<T extends Entry>{
    public void setNext(T next);

    public T getNext();
}
