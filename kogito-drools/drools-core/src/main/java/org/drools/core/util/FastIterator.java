package org.drools.core.util;

public interface FastIterator {
    public Entry next(Entry object);
    
    public boolean isFullIterator();
}
