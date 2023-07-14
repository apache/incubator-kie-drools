/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ObjectPool<T> extends AutoCloseable {

    T borrow();
    void offer(T t);

    static <T> ObjectPool<T> newSynchronizedPool(Supplier<T> factory) {
        return new SynchronizedObjectPool<>(factory);
    }

    static <T> ObjectPool<T> newSynchronizedPool(Supplier<T> factory, Consumer<T> destroyer) {
        return new SynchronizedObjectPool<>(factory, destroyer);
    }

    class SynchronizedObjectPool<T> implements ObjectPool<T> {
        private final Supplier<T> factory;
        private final Consumer<T> destroyer;

        private final Queue<T> pool = new ArrayDeque<>();

        public SynchronizedObjectPool(Supplier<T> factory) {
            this(factory, null);
        }

        public SynchronizedObjectPool(Supplier<T> factory, Consumer<T> destroyer) {
            this.factory = factory;
            this.destroyer = destroyer;
        }

        @Override
        public synchronized T borrow() {
            return pool.isEmpty() ? factory.get() : pool.poll();
        }

        @Override
        public synchronized void offer(T t) {
            pool.offer(t);
        }

        @Override
        public synchronized void close() throws Exception {
            if (destroyer != null) {
                pool.forEach(destroyer);
            }
        }
    }
}
