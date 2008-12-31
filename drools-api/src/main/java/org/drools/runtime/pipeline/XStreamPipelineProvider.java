package org.drools.runtime.pipeline;

import com.thoughtworks.xstream.XStream;

public interface XStreamPipelineProvider {
    Transformer newXStreamTransformer(XStream xstream);
}
