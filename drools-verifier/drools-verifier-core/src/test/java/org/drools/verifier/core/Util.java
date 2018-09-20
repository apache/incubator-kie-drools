/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.verifier.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.MultiMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Util {

    public static <T> void assertMapContent(final MultiMap<Value, T, List<T>> objectMultiMap,
                                            final Comparable... keyValues) {

        final Set<Value> keys = objectMultiMap.keySet();
        assertEquals(new HashSet<>(Arrays.asList(keyValues)).size(), keys.size());

        for (final Comparable keyValue : keyValues) {
            assertTrue(objectMultiMap.containsKey(getValue(keyValue)));
        }
    }

    public static Value getValue(final Comparable keyValue) {
        if (keyValue instanceof Key) {
            return ((Key) keyValue).getSingleValue();
        } else {
            return new Value(keyValue);
        }
    }
}
