package org.kie.efesto.common.api.model;

import java.util.Map;
import java.util.Set;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.listener.EfestoListener;

/**
 * The context of an execution
 */
public interface EfestoContext<T extends EfestoListener> {

    /**
     * Add the given <code>EfestoListener</code> to the current <code>Context</code>
     * @param toAdd
     */
    default void addEfestoListener(final T toAdd) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove the given <code>EfestoListener</code> from the current <code>Context</code>.
     * @param toRemove
     */
    default void removeEfestoListener(final T toRemove) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an <b>unmodifiable set</b> of the <code>EfestoListener</code>s registered with the
     * current instance
     */
    default Set<T> getEfestoListeners() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get <code>getGeneratedResourcesMap</code>
     */
    default Map<String, GeneratedResources> getGeneratedResourcesMap() {
        throw new UnsupportedOperationException();
    }

    /**
     * Add <code>GeneratedResources</code> with the key {@code model}
     */
    default void addGeneratedResources(String model, GeneratedResources generatedResources) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get previously generated classes with the key {@code fri}
     * @param modelLocalUriId
     * @return generatedClasses
     */
    default Map<String, byte[]> getGeneratedClasses(ModelLocalUriId modelLocalUriId) {
        return GeneratedClassesRepository.INSTANCE.getGeneratedClasses(modelLocalUriId);
    }

    /**
     * Add generated classes with the key {@code fri}
     * @param modelLocalUriId
     * @param generatedClasses
     */
    default void addGeneratedClasses(ModelLocalUriId modelLocalUriId, Map<String, byte[]> generatedClasses) {
        GeneratedClassesRepository.INSTANCE.addGeneratedClasses(modelLocalUriId, generatedClasses);
    }

    /**
     * Returns {@code true} if this map contains a mapping for the {@code fri}
     * @param localUri
     * @return {@code true} if this map contains a mapping for the {@code fri}
     */
    default boolean containsKey(ModelLocalUriId localUri) {
        return GeneratedClassesRepository.INSTANCE.containsKey(localUri);
    }

    /**
     * @return {@code Set} of {@code LocalUri} key in this map
     */
    default Set<ModelLocalUriId> localUriIdKeySet() {
        return GeneratedClassesRepository.INSTANCE.localUriIdKeySet();
    }
}
