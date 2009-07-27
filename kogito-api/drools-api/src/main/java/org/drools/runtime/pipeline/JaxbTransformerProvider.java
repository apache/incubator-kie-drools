package org.drools.runtime.pipeline;

import javax.xml.bind.JAXBContext;

/**
 * 
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>
 *
 */
public interface JaxbTransformerProvider {
    Transformer newJaxbFromXmlTransformer( JAXBContext jaxbCtx );
    Transformer newJaxbFromXmlCommandTransformer( JAXBContext jaxbCtx );
    Transformer newJaxbToXmlTransformer( JAXBContext jaxbCtx );
    Transformer newJaxbToXmlResultTransformer( JAXBContext jaxbCtx );
}
