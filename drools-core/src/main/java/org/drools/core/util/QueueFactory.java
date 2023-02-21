package org.drools.core.util;


import org.drools.core.util.Queue.QueueEntry;

import java.util.Comparator;

public class QueueFactory {

    private static final String BINARY_HEAP_QUEUE = "binaryheap";
    private static final String TREE_SET_QUEUE = "treeset";

    private static final String DEFAULT_QUEUE = BINARY_HEAP_QUEUE;

    public enum QueueType {
        BINARY_HEAP, TREE_SET;

        static QueueType get(String s) {
            if (s.equalsIgnoreCase(TREE_SET_QUEUE)) {
                return TREE_SET;
            }
            return BINARY_HEAP;
        }

        Factory createFactory() {
            return (this == TREE_SET) ?
                    new TreeSetQueueFactory() : new BinaryHeapQueueFactory();
        }
    }


    private static QueueType QUEUE_TYPE; // did not set this as final, as some tests need to change this

    static {
        QUEUE_TYPE = QueueType.get(System.getProperty("org.drools.queuetype", DEFAULT_QUEUE));
    }

    public static QueueType getQueueType() {
        return QUEUE_TYPE;
    }

    public static void setQueueType(QueueType type) {
        QUEUE_TYPE = type;
        QueueFactoryHolder.reinit();
    }

    public static <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator) {
        return QueueFactoryHolder.INSTANCE.createQueue(comparator);
    }

    public static <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator,  int capacity) {
        return QueueFactoryHolder.INSTANCE.createQueue(comparator);
    }

    interface Factory {
        <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator);
        <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator,  int capacity);
    }

    static class QueueFactoryHolder {
        private static Factory INSTANCE = QUEUE_TYPE.createFactory(); // did not set this as final, as some tests need to change this

        private static void reinit() {
            INSTANCE = QUEUE_TYPE.createFactory();
        }
    }

    static class BinaryHeapQueueFactory implements QueueFactory.Factory {

        @Override
        public <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator) {
            return new BinaryHeapQueue<>(comparator);
        }

        @Override
        public <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator,  int capacity) {
            return new BinaryHeapQueue<>(comparator, capacity);
        }
    }

    static class TreeSetQueueFactory implements QueueFactory.Factory {

        @Override
        public <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator) {
            return new TreeSetQueue<>(comparator);
        }

        @Override
        public <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator,  int capacity) {
            return new TreeSetQueue<>(comparator);
        }
    }
}
