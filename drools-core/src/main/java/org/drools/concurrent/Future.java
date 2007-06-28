package org.drools.concurrent;

import java.io.Serializable;

public interface Future extends Serializable  {
    boolean isDone();
    
    Object getObject();
    
    boolean exceptionThrown();
    Exception getException();
}
