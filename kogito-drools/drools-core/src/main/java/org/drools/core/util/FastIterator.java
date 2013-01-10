package org.drools.core.util;

public interface FastIterator {
    Entry next(Entry object);
    
    boolean isFullIterator();

    public static FastIterator EMPTY = new FastIterator() {
        public Entry next(Entry object) {
            return null;
        }
        public boolean isFullIterator() {
            return false;
        }
    };

    public static class IteratorAdapter implements Iterator {
        private final FastIterator fastIterator;
        private Entry current = null;
        private boolean firstConsumed = false;

        public IteratorAdapter(FastIterator fastIterator, Entry first) {
            this.fastIterator = fastIterator;
            current = first;
        }

        public Object next() {
            if (!firstConsumed) {
                firstConsumed = true;
                return current;
            }
            current = fastIterator.next(current);
            return current;
        }
    }
}
