package org.drools.runtime.pipeline;

import org.milyn.Smooks;

public interface SmooksPipelineProvider {
    Transformer newSmooksTransformer(Smooks smooks,
                                     String rootId);
}
