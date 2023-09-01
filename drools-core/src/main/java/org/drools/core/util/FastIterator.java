package org.drools.core.util;

public interface FastIterator<T> {
    public class NullFastIterator<T> implements FastIterator<T> {
        public static final NullFastIterator INSTANCE = new NullFastIterator();

        @Override public T next(T object) {
            return null;
        }

        @Override public boolean isFullIterator() {
            return true;
        }
    }

    T next(T object);
    
    boolean isFullIterator();

    public class IteratorAdapter<T> implements Iterator<T> {
        private final FastIterator<T> fastIterator;
        private T current = null;
        private boolean firstConsumed = false;

        public IteratorAdapter(FastIterator<T> fastIterator, T first) {
            this.fastIterator = fastIterator;
            current = first;
        }

        public T next() {
            if (!firstConsumed) {
                firstConsumed = true;
                return current;
            }
            current = fastIterator.next(current);
            return current;
        }
    }
}
