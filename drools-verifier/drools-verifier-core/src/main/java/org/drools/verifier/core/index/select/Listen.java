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
package org.drools.verifier.core.index.select;

import java.util.ArrayList;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapChangeHandler;
import org.drools.verifier.core.util.PortablePreconditions;

public class Listen<T>
        extends Select<T> {

    private final ArrayList<AllListener<T>> allListeners = new ArrayList<>();
    private final ArrayList<FirstListener<T>> firstListeners = new ArrayList<>();
    private final ArrayList<LastListener<T>> lastListeners = new ArrayList<>();

    private Entry<T> first;
    private Entry<T> last;
    private MultiMap<Value, T, List<T>> all;

    public Listen(final MultiMap<Value, T, List<T>> map,
                  final Matcher matcher) {
        super(map,
              matcher);

        PortablePreconditions.checkNotNull("map",
                                           map);

        map.addChangeListener(new MultiMapChangeHandler<Value, T>() {
            @Override
            public void onChange(final ChangeSet<Value, T> changeSet) {

                if (hasNoListeners()) {
                    return;
                }

                final ChangeHelper<T> changeHelper = new ChangeHelper<>(changeSet,
                                                                         matcher);

                if (!firstListeners.isEmpty()) {
                    if (first == null || changeHelper.firstChanged(first)) {
                        first = firstEntry();
                        notifyFirstListeners();
                    }
                }

                if (!lastListeners.isEmpty()) {
                    if (last == null || changeHelper.lastChanged(last)) {
                        last = lastEntry();
                        notifyLastListeners();
                    }
                }

                if (!allListeners.isEmpty()) {
                    all = asMap();
                    notifyAllListeners();
                }
            }
        });
    }

    /**
     * Well not *all*, just the AllListeners.
     */
    private void notifyAllListeners() {
        for (final AllListener<T> allListener : allListeners) {
            allListener.onAllChanged(all.allValues());
        }
    }

    private void notifyFirstListeners() {
        for (final FirstListener<T> firstListener : firstListeners) {
            firstListener.onFirstChanged(first.getValue());
        }
    }

    private void notifyLastListeners() {
        for (final LastListener<T> lastListener : lastListeners) {
            lastListener.onLastChanged(last.getValue());
        }
    }

    private boolean hasNoListeners() {
        return allListeners.isEmpty() && firstListeners.isEmpty() && lastListeners.isEmpty();
    }

    public void first(final FirstListener<T> firstListener) {
        if (first == null) {
            this.first = firstEntry();
        }

        firstListeners.add(firstListener);
    }

    public void last(final LastListener<T> lastListener) {
        if (last == null) {
            this.last = lastEntry();
        }
        lastListeners.add(lastListener);
    }

    public void all(final AllListener<T> allListener) {
        if (all == null) {
            this.all = asMap();
        }
        this.allListeners.add(allListener);
    }
}
