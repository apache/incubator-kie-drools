package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.impl.resources.DecisionTableResourceHandler;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.builder.impl.resources.DslrResourceHandler;
import org.drools.compiler.builder.impl.resources.ResourceHandler;
import org.drools.compiler.builder.impl.resources.TemplateResourceHandler;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class ResourceHandlerManager {
    private final List<ResourceHandler> mappers;
    private final List<ResourceType> orderedResourceTypes;

    public ResourceHandlerManager(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId, Supplier<DefaultExpander> dslExpander){
        this.mappers = asList(
                new DrlResourceHandler(configuration),
                new TemplateResourceHandler(configuration, releaseId, dslExpander),
                new DslrResourceHandler(configuration, dslExpander) ,
                new DecisionTableResourceHandler(configuration, releaseId));

        this.orderedResourceTypes = asList(
                ResourceType.DRL,
                ResourceType.GDRL,
                ResourceType.RDRL,
                ResourceType.DESCR,
                ResourceType.DSLR,
                ResourceType.RDSLR,
                ResourceType.DTABLE,
                ResourceType.TDRL,
                ResourceType.TEMPLATE);
    }

    public List<ResourceType> getOrderedResourceTypes(){
        return this.orderedResourceTypes;
    }

    public List<ResourceHandler> getMappers(){
        return this.mappers;
    }

    public ResourceHandler handlerForType(ResourceType type) {
        for (ResourceHandler mapper : this.mappers) {
            if (mapper.handles(type)) {
                return mapper;
            }
        }
        throw new IllegalArgumentException("No registered mapper for type " + type);
    }

    public boolean handles(ResourceType type){
        boolean handlesType=false;
        for (ResourceHandler mapper : this.mappers) {
            if (mapper.handles(type)) {
                handlesType = true;
            }
        }
        return handlesType;
    }
}