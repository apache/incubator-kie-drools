package org.drools.io;

import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class KnowledgeResource {
    private String                source;
    private ResourceType         type;
    private ResourceConfiguration configuration;

    public KnowledgeResource(String src,
                             ResourceType type) {
        this.source = src;
        this.type = type;
    }

    public KnowledgeResource(String src,
                             ResourceType type,
                             ResourceConfiguration configuration) {
        this.source = src;
        this.type = type;
        this.configuration = configuration;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String src) {
        this.source = src;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ResourceConfiguration configuration) {
        this.configuration = configuration;
    }

}
