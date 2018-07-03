package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.Component;
import org.kie.dmn.model.v1_1.extensions.Value;

import java.util.Map;

public class ComponentConverter extends DMNModelInstrumentedBaseConverter {
    public ComponentConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        switch ( nodeName ) {
            case "value": {
                ( (Component) parent ).setValue( (Value) child );
                break;
            }
            case "component": {
                ( (Component) parent ).getComponent().add( (Component) child);
                break;
            }
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        Component o = (Component) parent;
        CustomStaxReader underlyingReader = (CustomStaxReader) reader.underlyingReader();
        o.setName( underlyingReader.getAttribute( "name" ) );
        Map<String, String> currentNSCtx = underlyingReader.getNsContext();
        o.getNsContext().putAll( currentNSCtx );
        o.setLocation( underlyingReader.getLocation() );
        o.setAdditionalAttributes( underlyingReader.getAdditionalAttributes() );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new Component();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals( Component.class );
    }
}
