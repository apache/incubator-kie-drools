package org.kie.efesto.runtimemanager.api.model;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.cache.EfestoIdentifierClassKey;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * A generic <i>input</i> to be consumed
 */
public interface EfestoInput<T> {

    /**
     * The unique, full identifier of a given model' resource
     * @return
     */
    ModelLocalUriId getModelLocalUriId();

    T getInputData();

    /**
     * Returns the first-level cache key for the current <code>EfestoInput</code>
     * @return
     */
    default EfestoClassKey getFirstLevelCacheKey() {
        List<Type> generics = getInputData() != null ? Collections.singletonList(getInputData().getClass()) : Collections.emptyList();
        return new EfestoClassKey(this.getClass(), generics.toArray(new Type[0]));
    }

    /**
     * Returns the second-level cache key for the current <code>EfestoInput</code>
     * @return
     */
    default EfestoIdentifierClassKey getSecondLevelCacheKey() {
        return new EfestoIdentifierClassKey(this.getModelLocalUriId(), this.getFirstLevelCacheKey());
    }
}
