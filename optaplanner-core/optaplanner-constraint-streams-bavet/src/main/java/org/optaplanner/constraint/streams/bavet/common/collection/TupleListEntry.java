package org.optaplanner.constraint.streams.bavet.common.collection;

/**
 * An entry of {@link TupleList}
 *
 * @param <T> The element type. Often a tuple.
 */
public final class TupleListEntry<T> {

    private TupleList<T> list;
    private final T element;
    TupleListEntry<T> previous;
    TupleListEntry<T> next;

    TupleListEntry(TupleList<T> list, T element, TupleListEntry<T> previous) {
        this.list = list;
        this.element = element;
        this.previous = previous;
        this.next = null;
    }

    public TupleListEntry<T> next() {
        return next;
    }

    public TupleListEntry<T> removeAndNext() {
        TupleListEntry<T> next = this.next;
        remove(); // Sets this.next = null
        return next;
    }

    public void remove() {
        if (list == null) {
            throw new IllegalStateException("The element (" + element + ") was already removed.");
        }
        list.remove(this);
        list = null;
    }

    public T getElement() {
        return element;
    }

    @Override
    public String toString() {
        return element.toString();
    }

}
