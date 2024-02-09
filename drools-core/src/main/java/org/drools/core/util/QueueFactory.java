/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.util;


import java.util.Comparator;

import org.drools.core.util.Queue.QueueEntry;

import static org.drools.util.Config.getConfig;

public class QueueFactory {

    private static final String BINARY_HEAP_QUEUE = "binaryheap";
    private static final String TREE_SET_QUEUE = "treeset";

    private static final String DEFAULT_QUEUE = TREE_SET_QUEUE;

    public enum QueueType {
        BINARY_HEAP, TREE_SET;

        static QueueType get(String s) {
            if (s.equalsIgnoreCase(TREE_SET_QUEUE)) {
                return TREE_SET;
            }
            if (s.equalsIgnoreCase(BINARY_HEAP_QUEUE)) {
                return BINARY_HEAP;
            }
            throw new UnsupportedOperationException("Unknown queue type: " + s);
        }

        Factory createFactory() {
            return (this == TREE_SET) ?
                    new TreeSetQueueFactory() : new BinaryHeapQueueFactory();
        }
    }


    private static final QueueType QUEUE_TYPE;

    static {
        QUEUE_TYPE = QueueType.get(getConfig("org.drools.queuetype", DEFAULT_QUEUE));
    }

    public static <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator) {
        return QueueFactoryHolder.INSTANCE.createQueue(comparator);
    }

    interface Factory {
        <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator);
    }

    static class QueueFactoryHolder {
        private static final Factory INSTANCE = QUEUE_TYPE.createFactory();
    }

    static class BinaryHeapQueueFactory implements QueueFactory.Factory {

        @Override
        public <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator) {
            return new BinaryHeapQueue<>(comparator);
        }
    }

    static class TreeSetQueueFactory implements QueueFactory.Factory {

        @Override
        public <T extends QueueEntry> Queue<T> createQueue(Comparator<T> comparator) {
            return new TreeSetQueue<>(comparator);
        }
    }
}
