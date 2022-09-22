package org.optaplanner.constraint.streams.bavet.common.collection;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Different from {@link LinkedList} because nodes/indexes are allowed
 * to directly reference {@link TupleListEntry} instances
 * to avoid the lookup by index cost.
 * Also doesn't implement the {@link List} interface.
 *
 * @param <T> The element type. Often a tuple.
 */
public final class TupleList<T> {

    private int size = 0;
    private TupleListEntry<T> first = null;
    private TupleListEntry<T> last = null;

    public TupleListEntry<T> add(T tuple) {
        TupleListEntry<T> entry = new TupleListEntry<>(this, tuple, last);
        if (first == null) {
            first = entry;
        } else {
            last.next = entry;
        }
        last = entry;
        size++;
        return entry;
    }

    public void remove(TupleListEntry<T> entry) {
        if (first == entry) {
            first = entry.next;
        } else {
            entry.previous.next = entry.next;
        }
        if (last == entry) {
            last = entry.previous;
        } else {
            entry.next.previous = entry.previous;
        }
        entry.previous = null;
        entry.next = null;
        size--;
    }

    public TupleListEntry<T> first() {
        return first;
    }

    public TupleListEntry<T> last() {
        return last;
    }

    public int size() {
        return size;
    }

    public void forEach(Consumer<T> tupleConsumer) {
        TupleListEntry<T> entry = first;
        while (entry != null) {
            // Extract next before processing it, in case the entry is removed and entry.next becomes null
            TupleListEntry<T> next = entry.next;
            tupleConsumer.accept(entry.getElement());
            entry = next;
        }
    }

    @Override
    public String toString() {
        return "size = " + size;
    }

}
