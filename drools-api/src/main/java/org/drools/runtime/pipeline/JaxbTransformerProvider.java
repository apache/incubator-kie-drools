package org.drools.runtime.pipeline;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public interface JaxbTransformerProvider {
    Transformer newJaxbFromXmlTransformer(Unmarshaller unmarshaller);

    Transformer newJaxbToXmlTransformer(Marshaller marshaller);
}
