package org.kie.internal.builder.conf;

import org.kie.api.conf.Option;

/**
 * A markup interface for KnowledgeBuilderConfiguration options
 */
public interface KnowledgeBuilderOption
        extends
        Option {
    String TYPE = "Base";

    default String type() {
        return TYPE;
    }
}
