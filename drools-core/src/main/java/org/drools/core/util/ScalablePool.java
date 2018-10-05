/*
 * Copyright 2018 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScalablePool<T> {

    private static final Logger log = LoggerFactory.getLogger(ScalablePool.class);

    private final T[] fixedSizePool;
    private volatile int cursor;

    private java.util.Queue<T> dynamicPool;

    private final Supplier<? extends T> supplier;
    private final Consumer<? super T> resetter;
    private final Consumer<? super T> disposer;

    public ScalablePool( int initialSize, Supplier<? extends T> supplier, Consumer<? super T> resetter, Consumer<? super T> disposer ) {
        this.supplier = supplier;
        this.resetter = resetter;
        this.disposer = disposer;

        fixedSizePool = (T[]) new Object[initialSize];
        for (int i = 0; i < initialSize; i++) {
            fixedSizePool[i] = this.supplier.get();
        }
        cursor = initialSize;
    }

    public T get() {
        synchronized (fixedSizePool) {
            if ( cursor > 0 ) {
                return fixedSizePool[--cursor];
            }
        }
        if (dynamicPool == null) {
            dynamicPool = new java.util.LinkedList<>();
        } else {
            synchronized (dynamicPool) {
                if ( !dynamicPool.isEmpty() ) {
                    return dynamicPool.poll();
                }
            }
        }
        return supplier.get();
    }

    public void release(T t) {
        resetter.accept( t );
        synchronized (fixedSizePool) {
            if ( cursor < fixedSizePool.length ) {
                fixedSizePool[cursor++] = t;
                return;
            }
        }
        synchronized (dynamicPool) {
            dynamicPool.offer( t );
        }
    }

    public void clear() {
        for (int i = 0; i < fixedSizePool.length; i++) {
            disposer.accept( fixedSizePool[i] );
            fixedSizePool[i] = null;
        }
        if (dynamicPool != null) {
            for (T t : dynamicPool) {
                disposer.accept( t );
            }
            dynamicPool.clear();
        }
    }

}
