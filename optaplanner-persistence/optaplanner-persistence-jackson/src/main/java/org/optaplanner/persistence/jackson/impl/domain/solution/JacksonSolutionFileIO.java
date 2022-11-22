package org.optaplanner.persistence.jackson.impl.domain.solution;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.persistence.jackson.api.OptaPlannerJacksonModule;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class JacksonSolutionFileIO<Solution_> implements SolutionFileIO<Solution_> {

    private final Class<Solution_> clazz;
    private final ObjectMapper mapper;

    public JacksonSolutionFileIO(Class<Solution_> clazz) {
        this(clazz, new ObjectMapper());
    }

    public JacksonSolutionFileIO(Class<Solution_> clazz, ObjectMapper mapper) {
        this.clazz = clazz;
        this.mapper = mapper;
        mapper.registerModule(OptaPlannerJacksonModule.createModule());
    }

    @Override
    public String getInputFileExtension() {
        return "json";
    }

    @Override
    public String getOutputFileExtension() {
        return "json";
    }

    @Override
    public Solution_ read(File inputSolutionFile) {
        try {
            return mapper.readValue(inputSolutionFile, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed reading inputSolutionFile (" + inputSolutionFile + ").", e);
        }
    }

    @Override
    public void write(Solution_ solution, File file) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, solution);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed write", e);
        }
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
