package org.drools.core.util;

import java.io.Externalizable;

public class Memento<T> {

    private T old;
    private T current;
    private boolean firstRecord = true;

    public Memento(T object) {
        if (!(object instanceof DeepCloneable || object instanceof Externalizable)) {
            throw new RuntimeException("Memento object must be either DeepCloneable or Externalizable");
        }
        current = object;
    }

    public boolean initFirstRecord() {
        if (!firstRecord) {
            return false;
        }
        firstRecord = false;
        return true;
    }

    public T get() {
        return current;
    }

    public void record() {
        old = doClone();
    }

    public void undo() {
        if (old == null) {
            throw new RuntimeException("Nothing to be undone");
        }
        current = old;
        old = null;
    }

    protected T doClone() {
        if (current instanceof DeepCloneable) {
            return ((DeepCloneable<T>)current).deepClone();
        }
        return (T)ClassUtils.deepClone((Externalizable)current);
    }
}
