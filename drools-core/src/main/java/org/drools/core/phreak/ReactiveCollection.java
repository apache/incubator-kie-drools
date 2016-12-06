package org.drools.core.phreak;

import java.util.Collection;
import java.util.Iterator;

import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;
import org.drools.core.spi.Tuple;

public abstract class ReactiveCollection<T, W extends Collection<T>> extends AbstractReactiveObject implements Collection<T> {

    protected final W wrapped;

    public ReactiveCollection(W wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        // TODO wrap the iterator to avoid calling remove while iterating?
        return wrapped.iterator();
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a);
    }
    
    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }
    
    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean result = false;
        for ( T elem : c ) {
            result |= add(elem);
        }
        return result;
    }
    
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for ( Object elem : c ) {
            result |= remove(elem);
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean result = false;
        for ( T elem : wrapped ) {
            if ( !c.contains(elem) ) {
                result |= remove(elem);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        for ( T elem : wrapped ) {
            wrapped.remove(elem);
        }
    }
    
    @Override
    public boolean remove(Object o) {
        boolean result = wrapped.remove(o);
        if (result) {
            if (o instanceof ReactiveObject) {
                for (Tuple lts : getLeftTuples()) {
                    ((ReactiveObject) o).removeLeftTuple(lts);
                }
            }
            ReactiveObjectUtil.notifyModification(o, getLeftTuples(), ModificationType.REMOVE);
        }
        return result;
    }

}
