package org.optaplanner.examples.common.persistence.jackson;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @see AbstractKeyDeserializer
 */
public class AbstractExampleSolutionFileIO<Solution_> extends JacksonSolutionFileIO<Solution_> {

    public AbstractExampleSolutionFileIO(Class<Solution_> clazz) {
        super(clazz);
    }

    public AbstractExampleSolutionFileIO(Class<Solution_> clazz, ObjectMapper mapper) {
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

    protected <Key, Value, Index> Map<Key, Value> deduplicateMap(Map<Key, Value> originalMap, Map<Index, Key> index,
            Function<Key, Index> idFunction) {
        if (originalMap == null || originalMap.isEmpty()) {
            return originalMap;
        }

        var newMap = new LinkedHashMap<Key, Value>(originalMap.size());
        originalMap.forEach(
                (key, value) -> newMap.put(index.get(idFunction.apply(key)), value));
        return newMap;
    }

}
