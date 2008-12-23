package org.drools.definition.pipeline;

import org.milyn.Smooks;

public interface SmooksPipelineProvider {
    Transformer newSmooksTransformer(Smooks smooks,
                                     String rootId);
}
