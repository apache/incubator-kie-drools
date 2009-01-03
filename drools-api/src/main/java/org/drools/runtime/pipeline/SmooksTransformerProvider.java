package org.drools.runtime.pipeline;

import org.milyn.Smooks;

public interface SmooksTransformerProvider {
    Transformer newSmooksTransformer(Smooks smooks,
                                     String rootId);
}
