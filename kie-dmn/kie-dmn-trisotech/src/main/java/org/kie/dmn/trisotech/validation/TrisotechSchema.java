package org.kie.dmn.trisotech.validation;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kie.dmn.validation.DMNValidatorImpl;
import org.xml.sax.SAXException;

public class TrisotechSchema {
    static final Schema INSTANCEv1_3;
    static {
        try {
            INSTANCEv1_3 = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                                        .newSchema(new Source[]{new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DC.xsd")),
                                                                new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DI.xsd")),
                                                                new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DMNDI13.xsd")),
                                                                new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DMN13.xsd")),
                                                                new StreamSource(TrisotechSchema.class.getResourceAsStream("extension/TrisotechDMN13.xsd"))
                                        });
        } catch (SAXException e) {
            throw new RuntimeException("Unable to initialize correctly TrisotechSchema.", e);
        }
    }
}
