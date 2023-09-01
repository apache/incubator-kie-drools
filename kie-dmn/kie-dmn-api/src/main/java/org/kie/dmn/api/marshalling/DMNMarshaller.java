package org.kie.dmn.api.marshalling;

import java.io.Reader;
import java.io.Writer;

import org.kie.dmn.model.api.Definitions;

public interface DMNMarshaller {

    Definitions unmarshal(final Reader isr);

    Definitions unmarshal(final String xml);

    String marshal(Object o);

    void marshal(Object o, Writer out);

}
