package org.drools.runtime.pipeline;

import com.thoughtworks.xstream.XStream;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface XStreamTransformerProvider {
    Transformer newXStreamFromXmlTransformer(XStream xstream);

    Transformer newXStreamToXmlTransformer(XStream xstream);
}
