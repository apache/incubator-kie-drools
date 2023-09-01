package org.kie.api.internal.assembler;

import java.util.Collection;

import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public interface KieAssemblerService extends KieService {

    ResourceType getResourceType();

    default void addResourceBeforeRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception { }

    default void addResourcesBeforeRules(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            rd.getBeforeAdd().accept(kbuilder);
            addResourceBeforeRules(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            rd.getAfterAdd().accept(kbuilder);
        }
    }

    default void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception { }

    default void addResourcesAfterRules(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        for (ResourceWithConfiguration rd : resources) {
            rd.getBeforeAdd().accept(kbuilder);
            addResourceAfterRules(kbuilder, rd.getResource(), type, rd.getResourceConfiguration());
            rd.getAfterAdd().accept(kbuilder);
        }
    }
}