package org.kie.api.internal.io;

import java.io.Serializable;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

/**
 * A container for resources that have been processed by a {@link KieAssemblerService}.
 *
 * Resources are expected to be able to be looked up by a "name" or identifier.
 *
 * Each {@link ResourceTypePackage} is identified by a namespace.
 *
 * @param <T> the type of such a processed resource
 */
public interface ResourceTypePackage<T> extends Iterable<T>,
                                                Serializable {
    ResourceType getResourceType();

    /**
     * Remove artifacts inside this ResourceTypePackage which belong to the resource passed as parameter.
     * Concrete implementation of this interface shall extend this method in order to properly support incremental KieContainer updates.
     * 
     * @param resource
     * @return true if this ResourceTypePackage mutated as part of this method invocation.
     */
    default boolean removeResource(Resource resource) {
        return false;
    }

    void add(T element);

}
