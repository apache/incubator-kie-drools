/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to avoid chaining conversion in code
 */
@SuppressWarnings("unchecked")
public final class MapWalker {

    private Object theMap;
    private boolean safeNull;

    /**
     * Walker with safe null
     * @param theMap that we're going to walk into
     * @param safeNull whether to return a null value at the end of the walk, otherwise if the key isn't expected a NPE or an {@link IllegalArgumentException} might raise.
     */
    public MapWalker(Object theMap, boolean safeNull) {
        this.theMap = theMap;
        this.safeNull = safeNull;
    }

    public MapWalker(Object theMap) {
        this(theMap, false);
    }

    /**
     * Return the object as a Map.
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> asMap() {
        if (theMap instanceof Map) {
            return (Map<K, V>) theMap;
        }
        throw new IllegalArgumentException(String.format("The object %s is not a map. Impossible to get", theMap));
    }

    /**
     * Return the object as a list
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> List<Map<K, V>> asList() {
        if (theMap instanceof List) {
            return (List<Map<K, V>>) theMap;
        }
        throw new IllegalArgumentException(String.format("The object %s is not a list. Impossible to get", theMap));
    }

    /**
     * The key value is another map 
     */
    public MapWalker mapToMap(String key) {
        if (theMap instanceof Map) {
            this.theMap = ((Map<String, ?>) theMap).get(key);
            if (safeNull && this.theMap == null) {
                this.theMap = new HashMap<>();
            }
            return this;
        }
        throw new IllegalArgumentException(String.format("The object %s is not a map. Impossible to walk to the key '%s'", theMap, key));
    }

    /**
     * The key value is a list map.
     * 
     * @param key
     * @return
     */
    public MapWalker mapToListMap(final String key) {
        if (theMap instanceof Map) {
            theMap = (List<Map<?, ?>>) ((Map<?, ?>) theMap).get(key);
            if (safeNull && this.theMap == null) {
                this.theMap = new ArrayList<Map<?, ?>>();
            }
            return this;
        }
        throw new IllegalArgumentException(String.format("The object %s is not a list. Impossible to walk to the key %s", theMap, key));
    }

    /**
     * In a list, we take the index that is a map
     * @param index
     * @return
     */
    public MapWalker listToMap(final int index) {
        if (theMap instanceof List) {
            final List<Map<?, ?>> theList = ((List<Map<?, ?>>) theMap);
            if (theList != null && theList.size() > index) {
                theMap = (Map<?, ?>) theList.get(index);
            } else {
                this.theMap = new HashMap<>();
            }

            return this;
        }
        throw new IllegalArgumentException(String.format("The object %s is not a list. Impossible to walk to the index '%d'", theMap, index));
    }

}
