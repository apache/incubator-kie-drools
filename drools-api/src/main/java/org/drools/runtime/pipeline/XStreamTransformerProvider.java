package org.drools.runtime.pipeline;

import com.thoughtworks.xstream.XStream;

public interface XStreamTransformerProvider {
    Transformer newXStreamFromXmlTransformer(XStream xstream);
    Transformer newXStreamToXmlTransformer(XStream xstream);
}
