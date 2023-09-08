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
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.index.matchers.FromMatcher;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.matchers.ToMatcher;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.drools.verifier.core.util.PortablePreconditions;

public class Select<T> {

    private final MultiMap<Value, T, List<T>> map;
    private final Matcher matcher;

    public Select(final MultiMap<Value, T, List<T>> map,
                  final Matcher matcher) {
        this.map = PortablePreconditions.checkNotNull("map",
                                                      map);
        this.matcher = PortablePreconditions.checkNotNull("matcher",
                                                          matcher);
    }

    public T first() {
        final Entry<T> entry = firstEntry();
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    protected Entry<T> firstEntry() {
        final MultiMap<Value, T, List<T>> subMap = asMap();
        if (subMap == null) {
            return null;
        } else {
            try {
                final Value key = subMap.firstKey();
                final List<T> list = getT(subMap,
                                          key);
                if (list == null) {
                    return null;
                }
                return new Entry(key,
                                 list.iterator().next());
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public T last() {
        final Entry<T> entry = lastEntry();
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    protected Entry<T> lastEntry() {
        final MultiMap<Value, T, List<T>> subMap = asMap();
        if (subMap == null) {
            return null;
        } else {
            try {
                final Value key = subMap.lastKey();
                final List<T> list = getT(subMap,
                                          key);
                if (list == null) {
                    return null;
                }
                return new Entry<>(key,
                                    list.get(list.size() - 1));
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    private List<T> getT(final MultiMap<Value, T, List<T>> subMap,
                         final Value key) {
        if (subMap == null || subMap.isEmpty()) {
            return null;
        } else {
            return subMap.get(key);
        }
    }

    public Collection<T> all() {
        final MultiMap<Value, T, List<T>> subMap = asMap();
        if (subMap == null) {
            return new ArrayList<>();
        } else {
            return subMap.allValues();
        }
    }

    public MultiMap<Value, T, List<T>> asMap() {
        if (map == null) {
            return null;
        } else if (map.isEmpty()) {
            return map;
        } else if (matcher instanceof FromMatcher) {

            final FromMatcher fromMatcher = (FromMatcher) matcher;

            final Value lastKey = map.lastKey();

            if (lastKey == null) {
                return null;
            } else if (fromMatcher.getFrom().compareTo(lastKey) > 0) {
                return null;
            }

            return map.subMap(fromMatcher.getFrom(),
                              fromMatcher.includeValue(),
                              lastKey,
                              true);
        } else if (matcher instanceof ToMatcher) {

            final ToMatcher toMatcher = (ToMatcher) this.matcher;
            return map.subMap(map.firstKey(),
                              true,
                              toMatcher.getTo(),
                              false);
        } else if (matcher instanceof ExactMatcher) {

            return new ExactMatcherSearch<T>((ExactMatcher) this.matcher,
                                             this.map).search();
        } else {
            final MultiMap<Value, T, List<T>> result = MultiMapFactory.make();
            MultiMap.merge(result,
                           map);
            return result;
        }
    }

    public boolean exists() {
        final MultiMap<Value, T, List<T>> subMap = asMap();
        if (subMap == null) {
            return false;
        } else if (subMap.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    protected class Entry<T> {

        private final Value key;
        private final T value;

        public Entry(final Value key,
                     final T value) {
            this.key = key;
            this.value = value;
        }

        public Value getKey() {
            return key;
        }

        public T getValue() {
            return value;
        }
    }
}
