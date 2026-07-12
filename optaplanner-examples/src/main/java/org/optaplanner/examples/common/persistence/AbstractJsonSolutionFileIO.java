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

package org.optaplanner.examples.common.persistence;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @see AbstractKeyDeserializer
 */
public class AbstractJsonSolutionFileIO<Solution_> extends JacksonSolutionFileIO<Solution_> {

    public AbstractJsonSolutionFileIO(Class<Solution_> clazz) {
        super(clazz);
    }

    public AbstractJsonSolutionFileIO(Class<Solution_> clazz, ObjectMapper mapper) {
        super(clazz, mapper);
    }

    protected <Entity_, Id_ extends Number, Value_> void deduplicateEntities(Solution_ solution,
            Function<Solution_, Collection<Entity_>> entityCollectionFunction, Function<Entity_, Id_> entityIdFunction,
            Function<Entity_, Map<Entity_, Value_>> entityMapGetter,
            BiConsumer<Entity_, Map<Entity_, Value_>> entityMapSetter) {
        var entityCollection = entityCollectionFunction.apply(solution);
        var entitiesById = entityCollection.stream()
                .collect(Collectors.toMap(entityIdFunction, Function.identity()));
        for (Entity_ entity : entityCollection) {
            var originalMap = entityMapGetter.apply(entity);
            if (originalMap.isEmpty()) {
                continue;
            }
            var newMap = new LinkedHashMap<Entity_, Value_>(originalMap.size());
            originalMap
                    .forEach((otherEntity, value) -> newMap.put(entitiesById.get(entityIdFunction.apply(otherEntity)), value));
            entityMapSetter.accept(entity, newMap);
        }

    }

    protected <Key_, Value_, Index_> Map<Key_, Value_> deduplicateMap(Map<Key_, Value_> originalMap, Map<Index_, Key_> index,
            Function<Key_, Index_> idFunction) {
        if (originalMap == null || originalMap.isEmpty()) {
            return originalMap;
        }

        Map<Key_, Value_> newMap = new LinkedHashMap<>(originalMap.size());
        originalMap.forEach((key, value) -> newMap.put(index.get(idFunction.apply(key)), value));
        return newMap;
    }

}
