package org.drools.definition.pipeline;

import javax.xml.bind.Unmarshaller;

public interface JaxbPipelineProvider {
    Transformer newJaxbTransformer(Unmarshaller unmarshaller);
}
