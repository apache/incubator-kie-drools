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

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MappedCollectionTestField<M, T, C> extends CollectionTestField<M, T> {

    public MappedCollectionTestField(String fieldName,
            Collection<C> fieldValue,
            Function<M, Collection<C>> getter,
            BiConsumer<M, Collection<C>> setter,
            Function<T, C> mapElementToPersistentClass,
            Function<C, T> mapElementFromPersistentClass,
            Class<T> elementClass) {
        super(fieldName,
                fieldValue.stream().map(mapElementFromPersistentClass).collect(Collectors.toList()),
                m -> getter.apply(m).stream().map(mapElementFromPersistentClass).collect(Collectors.toList()),
                (m, ts) -> setter.accept(m, ts.stream().map(mapElementToPersistentClass).collect(Collectors.toList())),
                elementClass);
    }

}
