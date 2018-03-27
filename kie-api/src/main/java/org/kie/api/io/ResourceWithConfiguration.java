package org.kie.api.io;

import java.util.function.Consumer;

import org.kie.api.internal.assembler.KieAssemblerService;

/**
 * Represent a tuple of a {@link Resource} with associated {@link ResourceConfiguration}, along with necessary kbuilder callbacks, to be used in in {@link KieAssemblerService}.
 */
public interface ResourceWithConfiguration {

    Resource getResource();

    ResourceConfiguration getResourceConfiguration();

    /**
     * callback executed on `kbuilder` as a parameter in {@link KieAssemblerService}, which will be executed before performing {@link KieAssemblerService#addResource(Object, Resource, ResourceType, ResourceConfiguration)} for the given resource {@link #getResource()}.
     */
    Consumer<Object> getBeforeAdd();

    /**
     * callback executed on `kbuilder` as a parameter in {@link KieAssemblerService}, which will be executed after performing {@link KieAssemblerService#addResource(Object, Resource, ResourceType, ResourceConfiguration)} for the given resource {@link #getResource()}.
     */
    Consumer<Object> getAfterAdd();

}
