package org.drools.mvel.compiler.util.debug;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DebugList<T> extends ArrayList<T> {
    public Consumer<DebugList<T>> onItemAdded;

    @Override
    public synchronized boolean add(final T t ) {
        final boolean result = super.add(t );
        if (onItemAdded != null) {
            onItemAdded.accept( this );
        }
        return result;
    }
}
