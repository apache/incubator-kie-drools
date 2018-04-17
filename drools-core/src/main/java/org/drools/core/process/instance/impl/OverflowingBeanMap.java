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
 *
 */

package org.drools.core.process.instance.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A utility class that exposes getters and setters of a bean
 * as key/value pairss in a Map, allowing for extra "overflowing"
 * parameters to be stored in a separate map.
 */
class OverflowingBeanMap<T> extends BeanMap<T> {

    Map<String, Object> overflow = new HashMap<>();

    OverflowingBeanMap(T bean) {
        super(bean);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> entries = new HashSet<>(super.entrySet());
        entries.addAll(overflow.entrySet());
        return entries;
    }

    @Override
    public boolean containsKey(Object key) {
        return super.propertyExists((String) key)
                || overflow.containsKey(key);
    }

    @Override
    public Object put(String key, Object value) {
        return propertyExists(key) ?
                super.put(key, value) :
                overflow.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return propertyExists((String) key) ?
                super.get(key) :
                overflow.get(key);
    }
}

