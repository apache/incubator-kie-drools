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
package org.drools.verifier.core.maps;

import java.util.ArrayList;
import java.util.List;

public class MultiMapFactory {

    public static <Key extends Comparable, Value, ListType extends List<Value>> MultiMap<Key, Value, ListType> make(final boolean updatable,
                                                                                                                    final NewSubMapProvider<Value, ListType> newSubMapProvider) {
        if (updatable) {
            return new ChangeHandledMultiMap<>(new RawMultiMap<>(newSubMapProvider));
        } else {
            return new RawMultiMap<>(newSubMapProvider);
        }
    }

    public static <Key extends Comparable, Value> MultiMap<Key, Value, List<Value>> make(final boolean updatable) {
        return make(updatable,
                    new NewSubMapProvider<Value, List<Value>>() {
                        @Override
                        public List<Value> getNewSubMap() {
                            return new ArrayList<>();
                        }
                    });
    }

    public static MultiMap make() {
        return make(false);
    }
}
