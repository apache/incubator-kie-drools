package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.Value;

public class ValueConverter extends DMNModelInstrumentedBaseConverter {
    public ValueConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        ( (Value) parent ).setText( reader.getValue() );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new Value();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals( Value.class );
    }
}
