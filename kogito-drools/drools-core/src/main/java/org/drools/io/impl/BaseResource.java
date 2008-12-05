package org.drools.io.impl;

import java.io.Externalizable;

import org.drools.builder.KnowledgeType;
import org.drools.builder.ResourceConfiguration;
import org.drools.io.InternalResource;

public abstract class BaseResource
    implements
    InternalResource {
    private KnowledgeType         KnowledgeType;
    private ResourceConfiguration configuration;

    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ResourceConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setKnowledgeType(KnowledgeType knowledgeType) {
        this.KnowledgeType = knowledgeType;
    }

    public KnowledgeType getKnowledgeType() {
        return this.KnowledgeType;
    }

}
