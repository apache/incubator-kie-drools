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
package org.drools.verifier.core.index.query;

import java.util.HashMap;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.MultiMap;

public class MapBy<KeyType, ValueType> {

    private MultiMap<Value, ValueType, List<ValueType>> multiMap;

    private HashMap<KeyType, Value> valueMap = new HashMap<>();

    public MapBy(final MultiMap<Value, ValueType, List<ValueType>> multiMap) {
        this.multiMap = multiMap;
        for (final Value value : multiMap.keySet()) {
            valueMap.put((KeyType) value.getComparable(),
                         value);
        }
    }

    public boolean containsKey(final KeyType key) {
        return valueMap.containsKey(key);
    }

    public List<ValueType> get(final KeyType key) {
        return multiMap.get(valueMap.get(key));
    }
}
