package org.drools.compiler;

import org.drools.builder.KnowledgeType;
import org.drools.builder.ResourceConfiguration;
import org.drools.io.Resource;

public class KnowledgeResource {
    private String                source;
    private KnowledgeType         type;
    private ResourceConfiguration configuration;

    public KnowledgeResource(String src,
                             KnowledgeType type) {
        this.source = src;
        this.type = type;
    }

    public KnowledgeResource(String src,
                             KnowledgeType type,
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

    public KnowledgeType getType() {
        return type;
    }

    public void setType(KnowledgeType type) {
        this.type = type;
    }

    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ResourceConfiguration configuration) {
        this.configuration = configuration;
    }

}
