package org.optaplanner.core.impl.domain.variable.listener.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * An ordered {@link Set} which is implemented as a {@link ArrayList} for a small {@link Set#size()}
 * and a {@link LinkedHashSet} for a big {@link Set#size()}.
 * <p>
 * This speeds up {@link #add(Object)} performance (in some cases by 20%) if most instances have a small size
 * because no {@link Object#hashCode()} need to be calculated.
 *
 * @param <E>
 */
final class SmallScalingOrderedSet<E> implements Set<E> {

    protected static final int LIST_SIZE_THRESHOLD = 16;

    private boolean belowThreshold;
    private List<E> list;
    private Set<E> set;

    public SmallScalingOrderedSet() {
        belowThreshold = true;
        list = new ArrayList<>(LIST_SIZE_THRESHOLD);
        set = null;
    }

    @Override
    public int size() {
        return belowThreshold ? list.size() : set.size();
    }

    @Override
    public boolean isEmpty() {
        return belowThreshold ? list.isEmpty() : set.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return belowThreshold ? list.contains(o) : set.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return belowThreshold ? list.containsAll(c) : set.containsAll(c);
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<E> childIterator = belowThreshold ? list.iterator() : set.iterator();
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return childIterator.hasNext();
            }

            @Override
            public E next() {
                return childIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return belowThreshold ? list.toArray() : set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return belowThreshold ? list.toArray(a) : set.toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (belowThreshold) {
            int newSize = list.size() + 1;
            if (newSize > LIST_SIZE_THRESHOLD) {
                set = new LinkedHashSet<>(list);
                list = null;
                belowThreshold = false;
                return set.add(e);
            } else {
                if (list.contains(e)) {
                    return false;
                }
                return list.add(e);
            }
        } else {
            return set.add(e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (belowThreshold) {
            int newSize = list.size() + c.size();
            if (newSize > LIST_SIZE_THRESHOLD) {
                set = new LinkedHashSet<>(newSize);
                set.addAll(list);
                list = null;
                belowThreshold = false;
                return set.addAll(c);
            } else {
                boolean changed = false;
                for (E e : c) {
                    if (!list.contains(e)) {
                        changed = true;
                        list.add(e);
                    }
                }
                return changed;
            }
        } else {
            return set.addAll(c);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (belowThreshold) {
            return list.remove(o);
        } else {
            int newSize = set.size() - 1;
            if (newSize <= LIST_SIZE_THRESHOLD) {
                set.remove(o);
                list = new ArrayList<>(set);
                set = null;
                belowThreshold = true;
                return true;
            } else {
                return set.remove(o);
            }
        }
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll() not yet implemented");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll() not yet implemented");
    }

    @Override
    public void clear() {
        if (belowThreshold) {
            list.clear();
        } else {
            list = new ArrayList<>(LIST_SIZE_THRESHOLD);
            set = null;
            belowThreshold = true;
        }
    }

}
