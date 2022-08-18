package org.optaplanner.core.impl.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Uses a given {@link Set} as storage unless it is the first and only value, in which case it uses a field.
 * This helps avoid the overhead of creating and accessing the set if we only have 1 value.
 * This implementation is not thread-safe, regardless of the underlying {@link Set}.
 * Iteration order of elements is specified by the underlying {@link Set}.
 */
public final class FieldBasedScalingSet<E> implements Set<E> {

    private final Supplier<Set<E>> setSupplier;
    private E singletonValue;
    private Set<E> set;
    private int size = 0;

    public FieldBasedScalingSet(Supplier<Set<E>> setSupplier) {
        this.setSupplier = Objects.requireNonNull(setSupplier);
    }

    @Override
    public boolean add(E value) {
        if (set == null) { // We have not yet created the set.
            if (size == 0) { // Use the field instead of the set.
                singletonValue = value;
                size = 1;
                return true;
            } else if (size == 1) { // Switch from the field to the set.
                set = setSupplier.get();
                set.add(singletonValue);
                singletonValue = null;
            } else {
                throw new IllegalStateException("Impossible state: size (" + size + ") > 1 yet no set used.");
            }
        }
        boolean added = set.add(value);
        if (added) {
            size += 1;
        }
        return added;
    }

    @Override
    public void clear() {
        if (set == null) {
            singletonValue = null;
        } else {
            set.clear();
        }
        size = 0;
    }

    @Override
    public boolean remove(Object value) {
        if (set == null) { // We're using the field.
            if (!Objects.equals(singletonValue, value)) {
                return false; // Value was not found.
            }
            singletonValue = null;
            size = 0;
            return true;
        }
        boolean removed = set.remove(value);
        if (removed) {
            size -= 1;
        }
        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        int sizeBefore = size;
        for (E o : c) {
            add(o);
        }
        return sizeBefore < size;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (size == 0) {
            return false;
        } else if (set == null) {
            if (!c.contains(singletonValue)) {
                clear();
                return true;
            }
            return false;
        } else {
            return set.retainAll(c);
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        int sizeBefore = size;
        for (Object o : c) {
            remove(o);
        }
        return sizeBefore > size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object value) {
        if (size == 0) {
            return false;
        } else if (set == null) {
            return Objects.equals(value, singletonValue);
        } else {
            return set.contains(value);
        }
    }

    /**
     * As defined by {@link Set#iterator()}.
     * May throw an exception if the iterator is used to remove an element while the set is based on a field.
     * 
     * @return never null
     */
    @Override
    public Iterator<E> iterator() {
        if (size == 0) {
            return Collections.emptyIterator();
        }
        return Objects.requireNonNullElseGet(set, () -> Collections.singleton(singletonValue)).iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) { // To avoid unnecessary iterators.
        if (size == 0) {
            return;
        }
        if (set == null) {
            action.accept(singletonValue);
        } else {
            set.forEach(action); // The iterator is only necessary now.
        }
    }

    @Override
    public Object[] toArray() {
        if (size == 0) {
            return new Object[0];
        } else if (set == null) {
            return new Object[] { singletonValue };
        }
        return set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (size == 0) {
            return Arrays.copyOf(a, 0);
        } else if (set == null) {
            T[] copy = Arrays.copyOf(a, 1);
            copy[0] = (T) singletonValue;
            return copy;
        }
        return set.toArray(a);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        } else if (set == null) {
            return "[" + singletonValue + "]";
        } else {
            return set.toString();
        }
    }

}
