package org.drools.runtime.pipeline;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface JaxbTransformerProvider {
    Transformer newJaxbFromXmlTransformer(Unmarshaller unmarshaller);

    Transformer newJaxbToXmlTransformer(Marshaller marshaller);
}
