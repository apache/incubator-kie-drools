/*
 * Copyright 2005 JBoss Inc
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

package org.kie.kogito.rules.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.kie.kogito.rules.DataStream;

public class ListDataStream<T> implements DataStream<T> {
    private final ArrayList<T> values = new ArrayList<>();
    private final List<Consumer<T>> subscribers = new ArrayList<>();

    public ListDataStream( T... ts ) {
        append( ts );
    }

    @Override
    public void append( T... ts ) {
        for (T t : ts) {
            values.add( t );
            subscribers.forEach(c -> c.accept(t));
        }
    }

    @Override
    public void subscribe(Consumer<T> consumer) {
        subscribers.add(consumer);
        values.forEach(consumer);
    }

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }
}
