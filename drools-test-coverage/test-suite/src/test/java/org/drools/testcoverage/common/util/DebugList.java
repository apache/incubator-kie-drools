package org.drools.testcoverage.common.util;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DebugList<T> extends ArrayList<T> {
    public Consumer<DebugList<T>> onItemAdded;

    @Override
    public synchronized boolean add( T t ) {
        System.out.println( Thread.currentThread() + " adding " + t );
        boolean result = super.add( t );
        if (onItemAdded != null) {
            onItemAdded.accept( this );
        }
        return result;
    }
}
