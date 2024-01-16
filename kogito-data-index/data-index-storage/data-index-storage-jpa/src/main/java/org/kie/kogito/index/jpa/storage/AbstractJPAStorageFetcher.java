package org.kie.kogito.index.jpa.storage;

import java.util.function.Function;

import org.kie.kogito.index.jpa.model.AbstractEntity;
import org.kie.kogito.persistence.api.StorageFetcher;
import org.kie.kogito.persistence.api.query.Query;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Multi;

import jakarta.transaction.Transactional;

public class AbstractJPAStorageFetcher<E extends AbstractEntity, V> implements StorageFetcher<String, V> {

    private static final String LISTENER_NOT_AVAILABLE_IN_JPA = "Listener not available in JPA database";

    protected PanacheRepositoryBase<E, String> repository;
    protected Class<E> entityClass;
    protected Function<E, V> mapToModel;

    protected AbstractJPAStorageFetcher() {
    }

    protected AbstractJPAStorageFetcher(PanacheRepositoryBase<E, String> repository, Class<E> entityClass, Function<E, V> mapToModel) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.mapToModel = mapToModel;
    }

    @Override
    public Multi<V> objectCreatedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_JPA);
    }

    @Override
    public Multi<V> objectUpdatedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_JPA);
    }

    @Override
    public Multi<String> objectRemovedListener() {
        throw new UnsupportedOperationException(LISTENER_NOT_AVAILABLE_IN_JPA);
    }

    @Override
    public Query<V> query() {
        return new JPAQuery<>(repository, mapToModel, entityClass);
    }

    @Override
    @Transactional
    public V get(String key) {
        return repository.findByIdOptional(key).map(mapToModel).orElse(null);
    }

    @Override
    @Transactional
    public void clear() {
        repository.deleteAll();
    }
}
