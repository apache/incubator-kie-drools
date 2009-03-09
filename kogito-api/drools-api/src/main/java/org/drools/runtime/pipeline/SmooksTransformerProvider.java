package org.drools.runtime.pipeline;

import org.milyn.Smooks;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface SmooksTransformerProvider {
    Transformer newSmooksFromSourceTransformer(Smooks smooks,
                                               String rootId);

    Transformer newSmooksToSourceTransformer(Smooks smooks);
}
