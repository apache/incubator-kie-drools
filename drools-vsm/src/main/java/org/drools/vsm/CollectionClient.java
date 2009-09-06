package org.drools.vsm;

import java.util.Collection;
import java.util.Iterator;

public class CollectionClient<T> implements Collection {
    private String parentInstanceId;
    
    public CollectionClient(String parentInstanceId) {
        this.parentInstanceId = parentInstanceId;
    }
    
    public String getParentInstanceId() {
        return this.parentInstanceId;
    }
    
    
    public boolean add(Object e) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean addAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    public void clear() {
        // TODO Auto-generated method stub
        
    }

    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean containsAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public Iterator iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean removeAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean retainAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object[] toArray(Object[] a) {
        // TODO Auto-generated method stub
        return null;
    }

}
