package org.drools.runtime.pipeline;

import org.milyn.Smooks;

public interface SmooksTransformerProvider {
    Transformer newSmooksFromSourceTransformer(Smooks smooks,
                                               String rootId);

    Transformer newSmooksToSourceTransformer(Smooks smooks);
}
