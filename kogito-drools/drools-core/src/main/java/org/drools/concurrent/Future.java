package org.drools.concurrent;

import java.io.Externalizable;

public interface Future extends Externalizable  {
    boolean isDone();

    Object getObject();

    boolean exceptionThrown();
    Exception getException();
}
