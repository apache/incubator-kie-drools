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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScalablePool<T> {

    private final java.util.Queue<T> pool = new ConcurrentLinkedQueue<>();
    private final List<T> resources = Collections.synchronizedList( new ArrayList<>() );

    private final Supplier<? extends T> supplier;
    private final Consumer<? super T> resetter;
    private final Consumer<? super T> disposer;

    public ScalablePool( int initialSize, Supplier<? extends T> supplier, Consumer<? super T> resetter, Consumer<? super T> disposer ) {
        this.supplier = supplier;
        this.resetter = resetter;
        this.disposer = disposer;

        for (int i = 0; i < initialSize; i++) {
            T t = this.supplier.get();
            pool.offer( t );
            resources.add( t );
        }
    }

    public T get() {
        T t = pool.poll();
        if (t != null) {
            return t;
        }

        t = this.supplier.get();
        resources.add( t );
        return t;
    }

    public void release(T t) {
        resetter.accept( t );
        pool.offer( t );
    }

    public void shutdown() {
        for (T t : resources) {
            disposer.accept( t );
        }
        pool.clear();
        resources.clear();
    }
}
