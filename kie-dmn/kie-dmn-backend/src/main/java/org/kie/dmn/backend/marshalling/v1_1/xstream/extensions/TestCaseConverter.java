package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.v1_1.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.InputNode;
import org.kie.dmn.model.v1_1.extensions.ResultNode;
import org.kie.dmn.model.v1_1.extensions.TestCase;

import java.util.Map;

public class TestCaseConverter extends DMNModelInstrumentedBaseConverter {
    public TestCaseConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object o, String nodeName, Object child) {
        TestCase parent = (TestCase) o;
        switch (nodeName) {
            case "inputNode": {
                parent.getInputNode().add( (InputNode) child );
                break;
            }
            case "resultNode": {
                parent.getResultNode().add( (ResultNode) child );
                break;
            }
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        TestCase o = (TestCase) parent;
        CustomStaxReader underlyingReader = (CustomStaxReader) reader.underlyingReader();
        String description = underlyingReader.getAttribute( "description" );
        if ( description != null ) {
            o.setDescription( description );
        }
        o.setId( underlyingReader.getAttribute( "id" ) );
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
    protected DMNModelInstrumentedBase createModelObject() {
        return new TestCase();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TestCase.class);
    }
}
