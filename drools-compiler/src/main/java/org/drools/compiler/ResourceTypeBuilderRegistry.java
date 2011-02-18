package org.drools.compiler;

import java.util.HashMap;
import java.util.Map;

import org.drools.builder.ResourceType;

public class ResourceTypeBuilderRegistry {

    private static final ResourceTypeBuilderRegistry INSTANCE =
        new ResourceTypeBuilderRegistry();

    private Map<ResourceType, ResourceTypeBuilder> registry;
    
    public static ResourceTypeBuilderRegistry getInstance() {
        return INSTANCE;
    }

    private ResourceTypeBuilderRegistry() {
        this.registry = new HashMap<ResourceType, ResourceTypeBuilder>();
    }

    public void register(ResourceType resourceType, ResourceTypeBuilder builder) {
        this.registry.put( resourceType, builder );
    }

    public ResourceTypeBuilder getResourceTypeBuilder(ResourceType resourceType) {
        return this.registry.get( resourceType );
    }

}
