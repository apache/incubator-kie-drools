package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.Component;
import org.kie.dmn.model.v1_1.extensions.InputNode;
import org.kie.dmn.model.v1_1.extensions.Value;

import java.util.Map;

public class InputNodeConverter extends DMNModelInstrumentedBaseConverter {
    public InputNodeConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        InputNode o = (InputNode) parent;
        CustomStaxReader underlyingReader = (CustomStaxReader) reader.underlyingReader();
        o.setName( underlyingReader.getAttribute( "name" ) );
        String type = underlyingReader.getAttribute( "type" );
        if ( type != null ) {
            o.setType( type );
        }
        Map<String, String> currentNSCtx = underlyingReader.getNsContext();
        o.getNsContext().putAll( currentNSCtx );
        o.setLocation( underlyingReader.getLocation() );
        o.setAdditionalAttributes( underlyingReader.getAdditionalAttributes() );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        switch ( nodeName ) {
            case "value": {
                ( (InputNode) parent ).setValue( (Value) child );
                break;
            }
            case "component": {
                ( (InputNode) parent ).getComponent().add( (Component) child);
                break;
            }
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new InputNode();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals( InputNode.class );
    }
}
