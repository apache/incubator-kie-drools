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

        public IteratorAdapter(FastIterator fastIterator) {
            this.fastIterator = fastIterator;
        }

        public Object next() {
            current = fastIterator.next(current);
            return current;
        }
    }
}
