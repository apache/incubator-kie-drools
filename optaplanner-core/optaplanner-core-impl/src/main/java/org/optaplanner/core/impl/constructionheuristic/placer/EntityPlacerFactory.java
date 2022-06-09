package org.optaplanner.core.impl.constructionheuristic.placer;

import org.optaplanner.core.config.constructionheuristic.placer.EntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.PooledEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedEntityPlacerConfig;
import org.optaplanner.core.config.constructionheuristic.placer.QueuedValuePlacerConfig;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;

public interface EntityPlacerFactory<Solution_> {

    static <Solution_> EntityPlacerFactory<Solution_> create(EntityPlacerConfig<?> entityPlacerConfig) {
        if (PooledEntityPlacerConfig.class.isAssignableFrom(entityPlacerConfig.getClass())) {
            return new PooledEntityPlacerFactory<>((PooledEntityPlacerConfig) entityPlacerConfig);
        } else if (QueuedEntityPlacerConfig.class.isAssignableFrom(entityPlacerConfig.getClass())) {
            return new QueuedEntityPlacerFactory<>((QueuedEntityPlacerConfig) entityPlacerConfig);
        } else if (QueuedValuePlacerConfig.class.isAssignableFrom(entityPlacerConfig.getClass())) {
            return new QueuedValuePlacerFactory<>((QueuedValuePlacerConfig) entityPlacerConfig);
        } else {
            throw new IllegalArgumentException(String.format("Unknown %s type: (%s).",
                    EntityPlacerConfig.class.getSimpleName(), entityPlacerConfig.getClass().getName()));
        }
    }

    EntityPlacer<Solution_> buildEntityPlacer(HeuristicConfigPolicy<Solution_> configPolicy);
}
