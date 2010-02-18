package org.drools.core.util;

import java.util.NoSuchElementException;

import org.drools.runtime.ObjectFilter;
import org.drools.common.InternalFactHandle;
import org.drools.core.util.ObjectHashMap.ObjectEntry;

public class JavaIteratorAdapter
    implements
    java.util.Iterator {
    public static int    OBJECT = 0;
    public static int    FACT_HANDLE = 1;

    private Iterator     iterator;
    private ObjectEntry  nextEntry;
    //    private Object  nextObject;
    //    private InternalFactHandle  nextHandle;
    private ObjectFilter filter;
    private int          type;

    public JavaIteratorAdapter(Iterator iterator,
                               int type) {
        this( iterator,
              type,
              null );
    }

    public JavaIteratorAdapter(Iterator iterator,
                               int type,
                               ObjectFilter filter) {
        this.iterator = iterator;
        this.filter = filter;
        this.type = type;
        setNext();
    }

    public boolean hasNext() {
        return (this.nextEntry != null);
    }

    public Object next() {
        ObjectEntry current = this.nextEntry;

        if ( current != null ) {
            setNext();
        } else {
            throw new NoSuchElementException( "No more elements to return" );
        }

        if ( this.type == OBJECT ) {
            return ((InternalFactHandle) current.getKey()).getObject();
        } else {
            return current.getKey();
        }
    }

    private void setNext() {
        ObjectEntry entry = null;

        while ( entry == null ) {
            entry = (ObjectEntry) this.iterator.next();
            if ( entry == null ) {
                break;
            }
            if ( this.filter != null ) {
                Object object = ((InternalFactHandle) entry.getKey()).getObject();
                if ( this.filter.accept( object ) == false ) {
                    entry = null;
                }
            }
        }

        this.nextEntry = entry;
        //        this.nextHandle = handle;
        //        this.nextObject = object;
    }

    public void remove() {
        throw new UnsupportedOperationException( "remove() is not support" );
    }
}
