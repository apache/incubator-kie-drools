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

import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.MultiMap;

public class ExactMatcherSearch<T> {

    private ExactMatcher matcher;
    private MultiMap<Value, T, List<T>> map;

    public ExactMatcherSearch(final ExactMatcher matcher,
                              final MultiMap<Value, T, List<T>> map) {
        this.matcher = matcher;
        this.map = map;
    }

    public MultiMap<Value, T, List<T>> search() {

        if (matcher.isNegate()) {

            if (map.containsKey(matcher.getValue())) {

                return MultiMap.merge(map.subMap(map.firstKey(), true,
                                                 matcher.getValue(), false),
                                      map.subMap(matcher.getValue(), false,
                                                 map.lastKey(), true));
            } else {
                return map;
            }
        } else {
            return map.subMap(matcher.getValue(), true,
                              matcher.getValue(), true);
        }
    }
}
