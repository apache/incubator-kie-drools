package org.drools.io.impl;

import java.io.Externalizable;

import org.drools.builder.ResourceType;
import org.drools.builder.ResourceConfiguration;
import org.drools.io.internal.InternalResource;

public abstract class BaseResource
    implements
    InternalResource {
    private ResourceType         resourceType;
    private ResourceConfiguration configuration;

    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ResourceConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

}
