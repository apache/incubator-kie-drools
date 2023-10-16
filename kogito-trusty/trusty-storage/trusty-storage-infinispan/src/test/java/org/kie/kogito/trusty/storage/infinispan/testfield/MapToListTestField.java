/*
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
package org.kie.kogito.trusty.storage.infinispan.testfield;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MapToListTestField<M, K, V, L> extends ListTestField<M, L> {

    public MapToListTestField(
            String fieldName,
            Map<K, V> fieldValue,
            Function<M, Map<K, V>> getter,
            BiConsumer<M, Map<K, V>> setter,
            Class<L> elementClass,
            Function<Map<K, V>, List<L>> mapToList,
            Function<List<L>, Map<K, V>> listToMap) {
        super(fieldName, mapToList.apply(fieldValue), obj -> mapToList.apply(getter.apply(obj)), (obj, value) -> setter.accept(obj, listToMap.apply(value)), elementClass);
    }
}
