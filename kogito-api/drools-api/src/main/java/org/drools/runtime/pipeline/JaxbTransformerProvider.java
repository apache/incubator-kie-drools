package org.drools.runtime.pipeline;

import javax.xml.bind.Unmarshaller;

public interface JaxbTransformerProvider {
    Transformer newJaxbTransformer(Unmarshaller unmarshaller);
}
