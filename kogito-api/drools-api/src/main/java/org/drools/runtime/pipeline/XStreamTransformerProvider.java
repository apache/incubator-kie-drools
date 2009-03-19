package org.drools.runtime.pipeline;

import com.thoughtworks.xstream.XStream;


/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 * MN: well why make this an interface? Seriously, just use concrete if it is going to change.
 * This is just making the code harder to reason over. 
 *
 */
public interface XStreamTransformerProvider {
    Transformer newXStreamFromXmlTransformer(XStream xstream);

    Transformer newXStreamToXmlTransformer(XStream xstream);
}
