package org.drools.concurrent;

public interface InternalFuture extends Future {
    void setObject(Object object);
    Command getCommand();
}
