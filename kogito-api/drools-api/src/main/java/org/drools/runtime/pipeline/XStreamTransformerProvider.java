package org.drools.runtime.pipeline;

import com.thoughtworks.xstream.XStream;

public interface XStreamTransformerProvider {
    Transformer newXStreamTransformer(XStream xstream);
}
