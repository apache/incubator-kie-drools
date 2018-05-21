package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.ResultNode;
import org.kie.dmn.model.v1_1.extensions.ValueType;

import java.util.Map;

public class ResultNodeConverter extends DMNModelInstrumentedBaseConverter {
    public ResultNodeConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        ResultNode o = (ResultNode) parent;
        CustomStaxReader underlyingReader = (CustomStaxReader) reader.underlyingReader();
        String errorResult = underlyingReader.getAttribute( "errorResult" );
        if ( errorResult != null ) {
            o.setErrorResult( Boolean.getBoolean(errorResult) );
        }
        String name = underlyingReader.getAttribute( "name" );
        if ( name != null ) {
            o.setName( name );
        }
        String type = underlyingReader.getAttribute( "type" );
        if ( type != null)  {
            o.setType( type );
        }
        String cast = underlyingReader.getAttribute( "cast" );
        if (cast != null) {
            o.setCast( cast );
        }
        Map<String, String> currentNSCtx = underlyingReader.getNsContext();
        o.getNsContext().putAll( currentNSCtx );
        o.setLocation( underlyingReader.getLocation() );
        o.setAdditionalAttributes( underlyingReader.getAdditionalAttributes() );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        switch (nodeName) {
            case "computed": {
                ((ResultNode) parent).setComputed( (ValueType) child );
                break;
            }
            case "expected": {
                ((ResultNode) parent).setExpected( (ValueType) child );
                break;
            }
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new ResultNode();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals( ResultNode.class );
    }
}
