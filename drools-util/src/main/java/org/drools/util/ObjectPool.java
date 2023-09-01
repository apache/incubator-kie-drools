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
package org.drools.util;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ObjectPool<T> extends AutoCloseable {

    T borrow();
    void offer(T t);

    static <T> ObjectPool<T> newLockFreePool(Supplier<T> factory) {
        return new LockFreeObjectPool<>(factory);
    }

    static <T> ObjectPool<T> newLockFreePool(Supplier<T> factory, Consumer<T> destroyer) {
        return new LockFreeObjectPool<>(factory, destroyer);
    }

    class LockFreeObjectPool<T> implements ObjectPool<T> {
        private final Supplier<T> factory;
        private final Consumer<T> destroyer;

        private final Queue<T> pool = new LinkedTransferQueue<>();

        public LockFreeObjectPool(Supplier<T> factory) {
            this(factory, null);
        }

        public LockFreeObjectPool(Supplier<T> factory, Consumer<T> destroyer) {
            this.factory = factory;
            this.destroyer = destroyer;
        }

        @Override
        public T borrow() {
            T t = pool.poll();
            return t != null ? t : factory.get();
        }

        @Override
        public void offer(T t) {
            pool.offer(t);
        }

        @Override
        public void close() throws Exception {
            if (destroyer != null) {
                pool.forEach(destroyer);
            }
        }
    }
}
